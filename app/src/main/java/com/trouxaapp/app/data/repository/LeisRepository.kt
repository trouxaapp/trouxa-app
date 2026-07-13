package com.trouxaapp.app.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.trouxaapp.app.data.local.LeiDao
import com.trouxaapp.app.data.local.LeiEntity
import com.trouxaapp.app.data.remote.CamaraApiService
import com.trouxaapp.app.util.LawClassifier
import com.trouxaapp.app.util.Temperatura
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Laws pipeline. Single source of truth for the leis table.
 *
 * Pipeline per sync:
 *  1. List PL/PEC/PLP/MPV/MP/PDL presented in the last [SYNC_WINDOW_DAYS]
 *  2. For each new bill: detail (incl. justificativa + inteiro teor URL),
 *     ALL authors (deputies resolved by id, never by name search),
 *     tramitacoes, theme classification, temperatura. Insert.
 *  3. Apply human curation from the remote feed to new AND existing rows
 *     (curation entries without sources are rejected).
 *  4. Refresh tramitacao of recent bills and recompute temperatura.
 *
 * Failures are counted and reported in SyncResult instead of swallowed,
 * so the sync log can tell the user the truth about data health.
 */
@Singleton
class LeisRepository @Inject constructor(
    private val camaraApi: CamaraApiService,
    private val leiDao: LeiDao,
    private val okHttp: OkHttpClient
) {

    companion object {
        const val CURADORIA_URL =
            "https://raw.githubusercontent.com/trouxaapp/trouxa-app/main/feed/curadoria.json"
        const val SYNC_WINDOW_DAYS = 30
        private val TIPOS = setOf("PL", "PEC", "PLP", "MPV", "MP", "PDL")
    }

    private val gson = Gson()

    data class SyncResult(val novas: Int, val falhas: Int, val curadas: Int, val tramitacoesAtualizadas: Int)

    data class AutorInfo(
        val nome: String = "",
        val tipo: String = "",
        val deputadoId: Int = 0,
        val partido: String = "",
        val uf: String = "",
        val foto: String = "",
        val email: String = ""
    )

    data class FonteCuradoria(val titulo: String? = null, val url: String? = null)

    fun observeByTemperatura(): Flow<List<LeiEntity>> = leiDao.observeByTemperatura()
    fun observeByRecentes(): Flow<List<LeiEntity>> = leiDao.observeByRecentes()
    fun observeById(plId: Int): Flow<LeiEntity?> = leiDao.observeById(plId)
    suspend fun countApresentadasDesde(isoDate: String): Int = leiDao.countApresentadasDesde(isoDate)
    suspend fun maxId(): Int? = leiDao.maxId()
    suspend fun getById(plId: Int): LeiEntity? = leiDao.getById(plId)

    suspend fun syncAll(): SyncResult {
        val (novas, falhas) = syncNovas()
        val curadas = applyCuradoria()
        val tramitacoes = refreshTramitacoes(limit = 60)
        purgeAntigas()
        return SyncResult(novas, falhas, curadas, tramitacoes)
    }

    private suspend fun syncNovas(): Pair<Int, Int> = coroutineScope {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        val dataFim = fmt.format(cal.time)
        cal.add(Calendar.DAY_OF_MONTH, -SYNC_WINDOW_DAYS)
        val dataInicio = fmt.format(cal.time)

        // If the listing itself fails we want the worker to see the exception
        // and log a failed sync, not a green check over an empty run.
        // siglaTipo filters server-side so the 300-row window is not wasted
        // on requerimentos and other types we would drop here anyway.
        val paginas = (1..3).map { pagina ->
            async {
                camaraApi.getProposicoes(
                    itens = 100,
                    pagina = pagina,
                    dataApresentacaoInicio = dataInicio,
                    dataApresentacaoFim = dataFim,
                    siglaTipo = TIPOS.joinToString(",")
                ).dados ?: emptyList()
            }
        }.awaitAll().flatten()

        val candidatas = paginas
            .distinctBy { it.id }
            .filter { (it.siglaTipo ?: "") in TIPOS }
            .filterNot { isPdlDeConcessao(it.siglaTipo ?: "", it.ementa ?: "") }

        val limiter = Semaphore(6)
        var falhas = 0
        val resultados = candidatas.map { prop ->
            async {
                if (leiDao.exists(prop.id) > 0) return@async false
                limiter.withPermit {
                    try {
                        processProposicao(prop.id, prop.siglaTipo ?: "", prop.numero, prop.ano, prop.ementa ?: "")
                        true
                    } catch (_: Exception) {
                        falhas++
                        false
                    }
                }
            }
        }.awaitAll()
        Pair(resultados.count { it }, falhas)
    }

    /**
     * Broadcast-license PDLs ("Aprova o ato que renova/outorga/autoriza a
     * concessão de rádio/TV...") arrive in batches of dozens per day and are
     * administrative noise for this feed. Treaty and veto-sustaining PDLs do
     * not start with these phrases and are kept.
     */
    private fun isPdlDeConcessao(siglaTipo: String, ementa: String): Boolean {
        if (siglaTipo != "PDL") return false
        val e = Normalizer.normalize(ementa.trim().lowercase(Locale("pt", "BR")), Normalizer.Form.NFD)
            .replace(Regex("""\p{Mn}+"""), "")
        return e.startsWith("aprova o ato que renova") ||
            e.startsWith("aprova o ato que outorga") ||
            e.startsWith("aprova o ato que autoriza")
    }

    private suspend fun processProposicao(id: Int, siglaTipo: String, numero: Int, ano: Int, ementaLista: String) {
        val det = camaraApi.getProposicao(id).dados
        val ementa = det?.ementa ?: ementaLista
        val status = det?.statusProposicao

        val autores = fetchAutores(id)
        val principal = autores.firstOrNull() ?: AutorInfo()

        var tramitacoesJson = ""
        var tramitacaoDatas = emptyList<String>()
        var statusData = status?.dataHora ?: ""
        var statusDesc = status?.descricaoTramitacao ?: ""
        var situacao = status?.descricaoSituacao ?: ""
        var despacho = status?.despacho ?: ""
        try {
            val trams = camaraApi.getTramitacoes(id).dados ?: emptyList()
            if (trams.isNotEmpty()) {
                val ultimas = trams.takeLast(10)
                tramitacoesJson = gson.toJson(ultimas)
                tramitacaoDatas = trams.mapNotNull { it.dataHora }
                val ultima = trams.last()
                if (statusData.isBlank()) statusData = ultima.dataHora ?: ""
                if (statusDesc.isBlank()) statusDesc = ultima.descricaoTramitacao ?: ""
                if (situacao.isBlank()) situacao = ultima.descricaoSituacao ?: ""
                if (despacho.isBlank()) despacho = ultima.despacho ?: ""
            }
        } catch (_: Exception) {
            // tramitacao is optional at insert time; refreshTramitacoes retries later.
            // The card shows "sem movimentação registrada" rather than a fake status.
        }

        val classificacao = LawClassifier.classify(siglaTipo, ementa)
        val dataApresentacao = det?.dataApresentacao ?: ""
        val temp = Temperatura.compute(
            siglaTipo = siglaTipo,
            dataApresentacao = dataApresentacao,
            ultimaSituacao = situacao,
            despacho = despacho,
            tramitacaoDatas = tramitacaoDatas
        )

        leiDao.insert(
            LeiEntity(
                plId = id,
                siglaTipo = siglaTipo,
                numero = numero,
                ano = ano,
                ementa = ementa,
                ementaDetalhada = det?.ementaDetalhada ?: "",
                keywords = det?.keywords ?: "",
                urlInteiroTeor = det?.urlInteiroTeor ?: "",
                justificativa = det?.justificativa ?: "",
                tituloPopular = classificacao.tituloPopular,
                tema = classificacao.tema,
                autoresJson = gson.toJson(autores),
                autorNome = principal.nome,
                autorPartido = principal.partido,
                autorUf = principal.uf,
                autorFoto = principal.foto,
                autorTipo = principal.tipo,
                autorEmail = principal.email,
                dataApresentacao = dataApresentacao,
                ultimoStatusData = statusData,
                ultimoStatusDescricao = statusDesc,
                ultimaSituacao = situacao,
                despacho = despacho,
                tramitacoesJson = tramitacoesJson,
                tramitacaoAtualizadaEm = if (statusData.isNotBlank() || tramitacoesJson.isNotBlank()) System.currentTimeMillis() else 0,
                temperatura = temp.valor,
                temperaturaMotivosJson = gson.toJson(temp.motivos),
                sincronizadoEm = System.currentTimeMillis()
            )
        )
    }

    /**
     * Resolves ALL authors. Deputies are resolved by the id embedded in the
     * author uri (exact match), giving real party/UF/photo/email from the
     * Camara. Non-deputy authors (Senado, Poder Executivo, comissoes) keep
     * name + tipo only. Full resolution capped at 4 to bound API calls.
     */
    private suspend fun fetchAutores(propId: Int): List<AutorInfo> {
        val autores = try {
            camaraApi.getAutores(propId).dados ?: emptyList()
        } catch (_: Exception) {
            return emptyList()
        }
        var resolvidos = 0
        return autores.mapNotNull { autor ->
            val nome = autor.nome ?: return@mapNotNull null
            val depId = autor.uri
                ?.substringAfterLast("/deputados/", "")
                ?.takeWhile { it.isDigit() }
                ?.toIntOrNull() ?: 0
            if (depId > 0 && resolvidos < 4) {
                resolvidos++
                try {
                    val st = camaraApi.getDeputado(depId).dados?.ultimoStatus
                    AutorInfo(
                        nome = st?.nome ?: nome,
                        tipo = autor.tipo ?: "Deputado",
                        deputadoId = depId,
                        partido = st?.siglaPartido ?: "",
                        uf = st?.siglaUf ?: "",
                        foto = st?.urlFoto ?: "https://www.camara.leg.br/internet/deputado/bandep/$depId.jpg",
                        email = st?.email ?: st?.gabinete?.email ?: ""
                    )
                } catch (_: Exception) {
                    AutorInfo(nome = nome, tipo = autor.tipo ?: "Deputado", deputadoId = depId)
                }
            } else {
                AutorInfo(nome = nome, tipo = autor.tipo ?: "")
            }
        }
    }

    /**
     * Applies human curation to new AND existing rows. The old pipeline only
     * applied curation at insert time, which froze wrong content forever;
     * this one re-applies on every sync so corrections actually propagate.
     * Entries without at least one source are rejected: opinion without a
     * source does not enter this app.
     */
    suspend fun applyCuradoria(): Int {
        val overrides = fetchCuradoria()
        var aplicadas = 0
        for ((plId, cur) in overrides) {
            val fontes = (cur.fontes ?: emptyList()).filter { !it.url.isNullOrBlank() }
            if (fontes.isEmpty()) continue
            if (leiDao.exists(plId) == 0) continue
            leiDao.updateCuradoria(
                id = plId,
                resumo = cur.resumo ?: "",
                prosJson = gson.toJson(cur.pros ?: emptyList<String>()),
                contrasJson = gson.toJson(cur.contras ?: emptyList<String>()),
                contexto = cur.contexto ?: "",
                fontesJson = gson.toJson(fontes),
                ts = System.currentTimeMillis()
            )
            aplicadas++
        }
        return aplicadas
    }

    /**
     * Refreshes tramitacao for the most recently presented bills and
     * recomputes temperatura from the fresh facts.
     */
    suspend fun refreshTramitacoes(limit: Int = 60): Int {
        var atualizadas = 0
        val ids = leiDao.idsMaisRecentes(limit)
        for (id in ids) {
            if (refreshTramitacaoFor(id)) atualizadas++
        }
        return atualizadas
    }

    /** Refresh a single bill. Used on detail open so status is truly current. */
    suspend fun refreshTramitacaoFor(plId: Int): Boolean {
        val lei = leiDao.getById(plId) ?: return false
        return try {
            var statusData = ""
            var statusDesc = ""
            var situacao = ""
            var despacho = ""
            try {
                camaraApi.getProposicao(plId).dados?.statusProposicao?.let {
                    statusData = it.dataHora ?: ""
                    statusDesc = it.descricaoTramitacao ?: ""
                    situacao = it.descricaoSituacao ?: ""
                    despacho = it.despacho ?: ""
                }
            } catch (_: Exception) {}

            val trams = camaraApi.getTramitacoes(plId).dados ?: emptyList()
            var tramJson = lei.tramitacoesJson
            var datas = emptyList<String>()
            if (trams.isNotEmpty()) {
                tramJson = gson.toJson(trams.takeLast(10))
                datas = trams.mapNotNull { it.dataHora }
                val ultima = trams.last()
                if (statusData.isBlank()) statusData = ultima.dataHora ?: ""
                if (statusDesc.isBlank()) statusDesc = ultima.descricaoTramitacao ?: ""
                if (situacao.isBlank()) situacao = ultima.descricaoSituacao ?: ""
                if (despacho.isBlank()) despacho = ultima.despacho ?: ""
            }
            if (statusData.isBlank() && trams.isEmpty()) return false

            val temp = Temperatura.compute(
                siglaTipo = lei.siglaTipo,
                dataApresentacao = lei.dataApresentacao,
                ultimaSituacao = situacao.ifBlank { lei.ultimaSituacao },
                despacho = despacho.ifBlank { lei.despacho },
                tramitacaoDatas = datas
            )
            leiDao.updateTramitacao(
                id = plId,
                statusData = statusData.ifBlank { lei.ultimoStatusData },
                statusDescricao = statusDesc.ifBlank { lei.ultimoStatusDescricao },
                situacao = situacao.ifBlank { lei.ultimaSituacao },
                despacho = despacho.ifBlank { lei.despacho },
                tramJson = tramJson,
                ts = System.currentTimeMillis(),
                temperatura = temp.valor,
                motivosJson = gson.toJson(temp.motivos)
            )
            true
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun purgeAntigas() {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -(SYNC_WINDOW_DAYS * 3))
        leiDao.purgeAntigasSemCuradoria(fmt.format(cal.time))
    }

    private fun fetchCuradoria(): Map<Int, CuradoriaEntry> {
        return try {
            val req = Request.Builder().url(CURADORIA_URL).build()
            val body = okHttp.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return emptyMap()
                resp.body?.string() ?: return emptyMap()
            }
            val type = object : TypeToken<CuradoriaRemote>() {}.type
            val parsed = gson.fromJson<CuradoriaRemote>(body, type) ?: return emptyMap()
            (parsed.leis ?: emptyMap())
                .mapNotNull { (k, v) -> k.toIntOrNull()?.let { it to v } }
                .toMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }

    data class CuradoriaRemote(
        val versao: Int = 0,
        val atualizado: String? = null,
        val leis: Map<String, CuradoriaEntry>? = null
    )

    data class CuradoriaEntry(
        val resumo: String? = null,
        val pros: List<String>? = null,
        val contras: List<String>? = null,
        val contexto: String? = null,
        val fontes: List<FonteCuradoria>? = null
    )
}
