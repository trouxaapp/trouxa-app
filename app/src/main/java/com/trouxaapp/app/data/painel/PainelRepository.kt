package com.trouxaapp.app.data.painel

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fetches the daily panel straight from the official sources, in parallel.
 * A block that fails keeps whatever the last good fetch had (with its own
 * reference date), so a flaky source shows old-but-dated data instead of
 * a hole or, worse, a made-up number.
 */
@Singleton
class PainelRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: OkHttpClient
) {

    private val gson = Gson()
    private val arquivo get() = File(context.filesDir, "painel.json")
    private val mutex = Mutex()
    private val _painel = MutableStateFlow(carregar())
    val painel: StateFlow<Painel?> = _painel

    companion object {
        private const val CAMARA = "https://dadosabertos.camara.leg.br/api/v2"
        private const val SGS = "https://api.bcb.gov.br/dados/serie/bcdata.sgs"
        private const val SGS_PAGINA = "https://www3.bcb.gov.br/sgspub/"
        // IPCA by subitem, last released month
        const val SIDRA_IPCA = "https://apisidra.ibge.gov.br/values/t/7060/n1/all/v/63/p/last%201/c315/all"
        private const val SIDRA_PAGINA = "https://sidra.ibge.gov.br/tabela/7060"
        // 2026 presidential runoff (pleito 6258), official TSE totals
        private const val TSE_2026_PRESIDENTE =
            "https://resultados.tse.jus.br/oficial/ele2026/6258/dados/br/br-c0001-e006258-u.json"
        private val FEEDS = listOf(
            "https://www.camara.leg.br/noticias/rss/ultimas-noticias" to "Agência Câmara",
            "https://www.camara.leg.br/noticias/rss/dinamico/ELEICOES" to "Agência Câmara",
            "https://www12.senado.leg.br/noticias/rss" to "Agência Senado",
            "https://agenciabrasil.ebc.com.br/rss/politica/feed.xml" to "Agência Brasil",
            "https://agenciabrasil.ebc.com.br/rss/economia/feed.xml" to "Agência Brasil"
        )
        private val CREDITOS_VALIDADE = TimeUnit.HOURS.toMillis(20)
        // Same order as the fetches below; the B3 parser names the index itself
        private val NOMES_MERCADO = listOf("Dólar (PTAX venda)", "Ibovespa (bolsa)")
        private val NOMES_PARADOS = listOf("Selic (meta do Copom)", "Inflação em 12 meses (IPCA)", "Salário mínimo")
    }

    // One flaky source must not drop its card: fall back to the last good one, still dated
    private fun manter(novos: List<Indicador?>, antigos: List<Indicador>, nomes: List<String>): List<Indicador> =
        novos.mapIndexedNotNull { i, novo -> novo ?: antigos.firstOrNull { it.nome == nomes[i] } }

    suspend fun atualizar(): Painel = mutex.withLock {
        withContext(Dispatchers.IO) {
            val antes = _painel.value ?: Painel()
            val agora = System.currentTimeMillis()
            val novo = coroutineScope {
                val dolar = async { indicadorDiario(1, NOMES_MERCADO[0], 15) { PainelParsers.reais(it) } }
                val ibov = async { texto("https://cotacao.b3.com.br/mds/api/v1/InstrumentQuotation/IBOV")?.let(PainelParsers::ibovespa) }
                val selic = async { indicadorDegrau(432, NOMES_PARADOS[0], 700) { "%.2f%% ao ano".format(Locale("pt", "BR"), it) } }
                val ipca = async { indicadorDegrau(13522, NOMES_PARADOS[1], 200, mensal = true) { PainelParsers.pct(it) } }
                val minimo = async { indicadorDegrau(1619, NOMES_PARADOS[2], 700) { PainelParsers.reais(it) } }
                val precos = async { texto(SIDRA_IPCA)?.let { PainelParsers.precos(it, SIDRA_PAGINA) } }
                val camara = async { texto("$CAMARA/eventos?dataInicio=${hojeIso()}&dataFim=${hojeIso()}&itens=100")?.let(PainelParsers::camaraHoje) }
                val votacoes = async { votacoesRecentes() }
                val senado = async { senadoRecente() }
                val eleicao = async { texto(TSE_2026_PRESIDENTE)?.let { PainelParsers.eleicao(it, "https://resultados.tse.jus.br/oficial/app/index.html") } }
                val noticias = async { noticias() }
                val creditos = async {
                    if (agora - antes.creditosEm < CREDITOS_VALIDADE && antes.creditos.isNotEmpty()) null
                    else creditosPorAno()
                }

                val cred = creditos.await()
                Painel(
                    mercado = manter(listOf(dolar.await(), ibov.await()), antes.mercado, NOMES_MERCADO),
                    parados = manter(listOf(selic.await(), ipca.await(), minimo.await()), antes.parados, NOMES_PARADOS),
                    precos = precos.await() ?: antes.precos,
                    camaraHoje = camara.await() ?: antes.camaraHoje,
                    votacoesCamara = votacoes.await().ifEmpty { antes.votacoesCamara },
                    senado = senado.await() ?: antes.senado,
                    creditos = cred ?: antes.creditos,
                    creditosEm = if (cred != null) agora else antes.creditosEm,
                    eleicao = eleicao.await() ?: antes.eleicao,
                    noticias = noticias.await().ifEmpty { antes.noticias },
                    atualizadoEm = agora
                )
            }
            salvar(novo)
            _painel.value = novo
            novo
        }
    }

    private suspend fun indicadorDiario(serie: Int, nome: String, dias: Int, formatar: (Double) -> String): Indicador? {
        val pontos = sgs(serie, dias) ?: return null
        return PainelParsers.variacaoDiaria(nome, pontos, formatar, "Banco Central (SGS $serie)", SGS_PAGINA)
    }

    private suspend fun indicadorDegrau(
        serie: Int, nome: String, dias: Int, mensal: Boolean = false, formatar: (Double) -> String
    ): Indicador? {
        val pontos = sgs(serie, dias) ?: return null
        return PainelParsers.desdeQuando(nome, pontos, formatar, "Banco Central (SGS $serie)", SGS_PAGINA, mensal)
    }

    // Date-bounded on purpose: some series are forward-filled weeks ahead.
    // The SGS answers an HTML error page for windows longer than ~2 years.
    private suspend fun sgs(serie: Int, dias: Int): List<PainelParsers.Ponto>? {
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val agora = System.currentTimeMillis()
        val ini = fmt.format(agora - TimeUnit.DAYS.toMillis(dias.toLong()))
        val fim = fmt.format(agora)
        val json = texto("$SGS.$serie/dados?formato=json&dataInicial=$ini&dataFinal=$fim") ?: return null
        return PainelParsers.pontosSgs(json, agora).ifEmpty { null }
    }

    private suspend fun votacoesRecentes(): List<Votacao> {
        val fim = hojeIso()
        val ini = isoDiasAtras(30)
        val json = texto("$CAMARA/votacoes?dataInicio=$ini&dataFim=$fim&idOrgao=180&ordem=DESC&ordenarPor=dataHoraRegistro&itens=100")
            ?: return emptyList()
        val brutas = PainelParsers.votacoes(json).take(10)
        return coroutineScope {
            brutas.map { b ->
                async {
                    // Vote ids are "{bill id}-{sequence}"
                    val prop = b.id.substringBefore('-').takeIf { it.all(Char::isDigit) && it.isNotEmpty() }
                        ?.let { texto("$CAMARA/proposicoes/$it")?.let(PainelParsers::proposicao) }
                    // Party orientation is only meaningful for roll-call votes
                    val o = if (!b.nominal) emptyList()
                    else texto("$CAMARA/votacoes/${b.id}/orientacoes")?.let(PainelParsers::orientacoes).orEmpty()
                    b.votacao.copy(
                        orientacoes = o,
                        proposicao = prop?.first,
                        siglaTipo = prop?.second,
                        ementa = prop?.third
                    )
                }
            }.map { it.await() }
        }
    }

    // The Senate publishes one file per day; walk back to the last day with a session
    private suspend fun senadoRecente(): SenadoDia? {
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.US).apply { timeZone = TimeZone.getTimeZone("America/Sao_Paulo") }
        val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"))
        repeat(10) {
            val json = texto("https://legis.senado.leg.br/dadosabertos/plenario/resultado/${fmt.format(cal.time)}.json")
            json?.let(PainelParsers::senado)?.let { return it }
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        return null
    }

    private suspend fun noticias(): List<Noticia> = coroutineScope {
        FEEDS.map { (url, fonte) ->
            async { bytes(url)?.let { PainelParsers.rss(PainelParsers.decodificar(it), fonte) }.orEmpty() }
        }.flatMap { it.await() }
            .distinctBy { it.link }
            .sortedByDescending { it.publicadaEm }
            .take(30)
    }

    private suspend fun creditosPorAno(): List<CreditoAno>? {
        val anoAtual = Calendar.getInstance().get(Calendar.YEAR)
        val anos = (2019..anoAtual).toList()
        val resultado = coroutineScope {
            anos.map { ano ->
                async {
                    var url: String? = "$CAMARA/proposicoes?siglaTipo=MPV&ano=$ano&itens=100&ordem=ASC&ordenarPor=id"
                    val valores = mutableListOf<Double>()
                    var paginas = 0
                    while (url != null && paginas < 5) {
                        val json = texto(url) ?: return@async null
                        val (v, next) = PainelParsers.creditosDaPagina(json)
                        valores += v
                        url = next
                        paginas++
                    }
                    CreditoAno(ano, valores.sum(), valores.size, PainelParsers.presidenciaDoAno(ano))
                }
            }.map { it.await() }
        }
        // All or nothing: a series with a missing year would mislead
        return if (resultado.any { it == null }) null else resultado.filterNotNull()
    }

    private fun texto(url: String): String? = bytes(url)?.toString(Charsets.UTF_8)

    // The BCB answers an occasional 502; one retry clears nearly all of them.
    // A 4xx is an answer (the Senate's "no session that day"), not a failure.
    private fun bytes(url: String): ByteArray? {
        val req = Request.Builder().url(url)
            .header("Accept", "application/json, application/xml, text/xml, */*")
            .build()
        repeat(2) { tentativa ->
            runCatching {
                client.newCall(req).execute().use { r ->
                    if (r.isSuccessful) return r.body?.bytes()
                    if (r.code < 500) return null
                }
            }
            if (tentativa == 0) Thread.sleep(1500)
        }
        return null
    }

    private fun hojeIso() = isoDiasAtras(0)

    private fun isoDiasAtras(dias: Int): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"))
        cal.add(Calendar.DAY_OF_MONTH, -dias)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = cal.timeZone }.format(cal.time)
    }

    private fun carregar(): Painel? = runCatching {
        if (!arquivo.exists()) null else gson.fromJson(arquivo.readText(), Painel::class.java)
    }.getOrNull()

    private fun salvar(p: Painel) {
        runCatching {
            val tmp = File(arquivo.parentFile, "painel.json.tmp")
            tmp.writeText(gson.toJson(p))
            tmp.renameTo(arquivo)
        }
    }
}
