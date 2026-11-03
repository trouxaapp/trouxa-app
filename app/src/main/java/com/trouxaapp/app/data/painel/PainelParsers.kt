package com.trouxaapp.app.data.painel

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.trouxaapp.app.util.LeiExplicada
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

/**
 * Pure parsers for every panel source. No network, no Android types, so
 * they run in plain JVM tests against saved responses. Each one returns
 * null or an empty list when the payload is not what we expect: a wrong
 * shape must never turn into a wrong number on screen.
 */
object PainelParsers {

    private val BR = Locale("pt", "BR")

    data class Ponto(val data: String, val valor: Double, val ts: Long)

    // ---------- Banco Central (SGS) ----------

    /** SGS rows [{data: "dd/MM/yyyy", valor: "5.4935"}], keeping only dates up to [agora]. */
    fun pontosSgs(json: String, agora: Long): List<Ponto> {
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return runCatching {
            JsonParser.parseString(json).asJsonArray.mapNotNull { el ->
                val o = el.asJsonObject
                val data = o.str("data")
                val valor = o.str("valor").replace(",", ".").toDoubleOrNull() ?: return@mapNotNull null
                val ts = runCatching { fmt.parse(data)?.time }.getOrNull() ?: return@mapNotNull null
                if (ts > agora) null else Ponto(data, valor, ts)
            }.sortedBy { it.ts }
        }.getOrDefault(emptyList())
    }

    /** Daily series: today's value against the previous business day. */
    fun variacaoDiaria(
        nome: String, pontos: List<Ponto>, formatar: (Double) -> String,
        fonte: String, fonteUrl: String
    ): Indicador? {
        val ultimo = pontos.lastOrNull() ?: return null
        val anterior = pontos.getOrNull(pontos.size - 2)
        val (mudanca, direcao) = if (anterior == null) "" to 0 else {
            val pct = (ultimo.valor - anterior.valor) / anterior.valor * 100
            when {
                abs(pct) < 0.005 -> "Igual a ${anterior.data.take(5)}" to 0
                pct > 0 -> "Subiu ${pct(pct)} desde ${anterior.data.take(5)}" to 1
                else -> "Caiu ${pct(-pct)} desde ${anterior.data.take(5)}" to -1
            }
        }
        return Indicador(nome, formatar(ultimo.valor), mudanca, direcao, ultimo.data, fonte, fonteUrl)
    }

    /**
     * Step series (Selic target, minimum wage, IPCA 12m): how long the
     * current value has held, or what it was before the last change.
     */
    fun desdeQuando(
        nome: String, pontos: List<Ponto>, formatar: (Double) -> String,
        fonte: String, fonteUrl: String, mensal: Boolean = false
    ): Indicador? {
        val ultimo = pontos.lastOrNull() ?: return null
        val iMudanca = pontos.indexOfLast { abs(it.valor - ultimo.valor) > 1e-9 }
        if (iMudanca < 0) {
            return Indicador(nome, formatar(ultimo.valor), "Sem mudança no período consultado", 0, ultimo.data, fonte, fonteUrl)
        }
        val antes = pontos[iMudanca]
        val desde = pontos[iMudanca + 1]
        val direcao = if (ultimo.valor > antes.valor) 1 else -1
        val texto = if (mensal) {
            "${if (direcao > 0) "Subiu" else "Caiu"}: era ${formatar(antes.valor)} no mês anterior"
        } else {
            "${if (direcao > 0) "Último aumento" else "Último corte"} em ${desde.data} (era ${formatar(antes.valor)}). Sem mudança desde então."
        }
        // A step that has not moved in months is news in itself: unchanged
        return Indicador(nome, formatar(ultimo.valor), texto, if (mensal) direcao else 0, ultimo.data, fonte, fonteUrl)
    }

    // ---------- B3 ----------

    fun ibovespa(json: String): Indicador? = runCatching {
        val root = JsonParser.parseString(json).asJsonObject
        val qtn = root.getAsJsonArray("Trad")[0].asJsonObject
            .getAsJsonObject("scty").getAsJsonObject("SctyQtn")
        val atual = qtn.get("curPrc").asDouble
        val flut = qtn.get("prcFlcn").asDouble
        val quando = root.getAsJsonObject("Msg").str("dtTm") // "2026-11-03 17:06:57"
        val data = runCatching {
            val d = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(quando)!!
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(d)
        }.getOrDefault(quando)
        val direcao = when { abs(flut) < 0.005 -> 0; flut > 0 -> 1; else -> -1 }
        Indicador(
            nome = "Ibovespa (bolsa)",
            valor = "%,.0f pontos".format(BR, atual),
            mudanca = when (direcao) {
                0 -> "Estável em relação ao fechamento anterior"
                1 -> "Subiu ${pct(flut)} no dia"
                else -> "Caiu ${pct(-flut)} no dia"
            },
            direcao = direcao,
            dataReferencia = data,
            fonte = "B3",
            fonteUrl = "https://www.b3.com.br/pt_br/market-data-e-indices/indices/indices-amplos/ibovespa.htm"
        )
    }.getOrNull()

    // ---------- IBGE SIDRA (IPCA by subitem) ----------

    private val SUBITEM = Regex("""^(\d{7})\.(.+)$""")

    fun precos(json: String, url: String): Precos? = runCatching {
        val linhas = JsonParser.parseString(json).asJsonArray.drop(1).map { it.asJsonObject }
        val geral = linhas.firstOrNull { it.str("D4C") == "7169" } ?: return null
        val itens = linhas.mapNotNull { o ->
            val m = SUBITEM.find(o.str("D4N")) ?: return@mapNotNull null
            val v = o.str("V").toDoubleOrNull() ?: return@mapNotNull null
            ItemPreco(m.groupValues[2].trim(), v)
        }
        if (itens.isEmpty()) return null
        Precos(
            mes = geral.str("D3N"),
            indiceGeral = geral.str("V").toDouble(),
            altas = itens.filter { it.variacao > 0 }.sortedByDescending { it.variacao }.take(8),
            quedas = itens.filter { it.variacao < 0 }.sortedBy { it.variacao }.take(5),
            fonte = "IBGE, IPCA por subitem",
            fonteUrl = url
        )
    }.getOrNull()

    // ---------- Câmara ----------

    fun camaraHoje(json: String): CamaraHoje? = runCatching {
        val eventos = JsonParser.parseString(json).asJsonObject.getAsJsonArray("dados").map { it.asJsonObject }
        val plen = eventos.filter { ev ->
            ev.getAsJsonArray("orgaos")?.any { it.asJsonObject.str("sigla") == "PLEN" } == true
        }.maxByOrNull { it.str("dataHoraInicio") }
        val plenario = plen?.let { "${it.str("descricaoTipo")}, ${it.str("situacao").lowercase(BR)}" }
            ?: "Sem sessão no Plenário marcada para hoje"
        val comissoes = eventos.filter { it !== plen }
        CamaraHoje(
            plenario = plenario,
            reunioes = comissoes.count { !it.str("situacao").startsWith("Cancelad") },
            canceladas = comissoes.count { it.str("situacao").startsWith("Cancelad") }
        )
    }.getOrNull()

    private val PLACAR = Regex("""Sim:\s*(\d+);\s*N[ãa]o:\s*(\d+)(?:;\s*Absten[çc][ãa]o:\s*(\d+))?""")

    // A vote line only says something on its own when it names the bill
    private val CITA_PROPOSICAO = Regex(
        """(Projeto de (Lei|Decreto Legislativo|Resolu[çc][ãa]o)|Medida Provis[óo]ria|Proposta de Emenda)[^.;]*?n[º°o.]""",
        RegexOption.IGNORE_CASE
    )

    data class VotacaoBruta(val id: String, val votacao: Votacao, val nominal: Boolean)

    fun votacoes(json: String): List<VotacaoBruta> = runCatching {
        JsonParser.parseString(json).asJsonObject.getAsJsonArray("dados").mapNotNull { el ->
            val o = el.asJsonObject
            val desc = o.str("descricao").trim()
            // Final wording sign-offs are procedural and repeat the vote above them
            if (desc.isBlank() || desc.contains("Redação Final", ignoreCase = true)) return@mapNotNull null
            val m = PLACAR.find(desc)
            // Roll calls always stay (they carry the score); symbolic ones only if they name the bill
            if (m == null && !CITA_PROPOSICAO.containsMatchIn(desc)) return@mapNotNull null
            val placar = m?.let {
                buildString {
                    append("Sim ${it.groupValues[1]} x Não ${it.groupValues[2]}")
                    if (it.groupValues[3].isNotBlank()) append(", ${it.groupValues[3]} abstenções")
                }
            }.orEmpty()
            val idEvento = o.str("uriEvento").substringAfterLast('/')
            VotacaoBruta(
                id = o.str("id"),
                votacao = Votacao(
                    data = o.str("data"),
                    descricao = desc,
                    aprovada = when (o.get("aprovacao")?.takeIf { !it.isJsonNull }?.asInt) { 1 -> true; 0 -> false; else -> null },
                    placar = placar,
                    orientacoes = emptyList(),
                    orgao = o.str("siglaOrgao"),
                    url = if (idEvento.isNotBlank()) "https://www.camara.leg.br/evento-legislativo/$idEvento" else ""
                ),
                nominal = m != null
            )
        }
    }.getOrDefault(emptyList())

    /** "PL 5239/2025" plus the official summary, from /proposicoes/{id}. */
    fun proposicao(json: String): Triple<String, String, String>? = runCatching {
        val d = JsonParser.parseString(json).asJsonObject.getAsJsonObject("dados")
        val sigla = d.str("siglaTipo")
        val ementa = d.str("ementa").trim()
        if (sigla.isBlank() || ementa.isBlank()) return null
        Triple("$sigla ${d.str("numero")}/${d.str("ano")}", sigla, ementa)
    }.getOrNull()

    fun orientacoes(json: String): List<String> = runCatching {
        JsonParser.parseString(json).asJsonObject.getAsJsonArray("dados").mapNotNull { el ->
            val o = el.asJsonObject
            val quem = o.str("siglaPartidoBloco")
            val voto = o.str("orientacaoVoto")
            if (quem.isBlank() || voto.isBlank()) null else "$quem: $voto"
        }
    }.getOrDefault(emptyList())

    /** Sums the values of "Abre crédito extraordinário" MPs in one page of /proposicoes. */
    fun creditosDaPagina(json: String): Pair<List<Double>, String?> = runCatching {
        val root = JsonParser.parseString(json).asJsonObject
        val valores = root.getAsJsonArray("dados").mapNotNull { el ->
            val ementa = el.asJsonObject.str("ementa")
            if (!Regex("""^abre cr[ée]dito extraordin""", RegexOption.IGNORE_CASE).containsMatchIn(ementa.trim())) {
                return@mapNotNull null
            }
            // The Camara has at least one typo like "R$N195.000.000,00"
            Regex("""R\$\D{0,2}([\d.]+(?:,\d{2})?)""").find(ementa)?.groupValues?.get(1)
                ?.replace(".", "")?.replace(",", ".")?.toDoubleOrNull()
        }
        val next = root.getAsJsonArray("links")?.firstOrNull { it.asJsonObject.str("rel") == "next" }
            ?.asJsonObject?.str("href")
        valores to next
    }.getOrDefault(emptyList<Double>() to null)

    // Terms start on January 1st, so a calendar year belongs to one president
    fun presidenciaDoAno(ano: Int): String = when (ano) {
        in 2019..2022 -> "Bolsonaro"
        in 2023..2026 -> "Lula"
        in 2016..2018 -> "Temer"
        else -> ""
    }

    // ---------- Senado ----------

    fun senado(json: String): SenadoDia? = runCatching {
        val sessoes = JsonParser.parseString(json).asJsonObject.getAsJsonObject("ResultadoPlenario")
            .getAsJsonObject("Sessoes")?.get("Sessao")?.listaOuUm().orEmpty()
        if (sessoes.isEmpty()) return null
        val itens = sessoes.flatMap { s ->
            s.asJsonObject.getAsJsonObject("Itens")?.get("Item")?.listaOuUm().orEmpty().map { it.asJsonObject }
        }.mapNotNull { i ->
            val resultado = i.str("textoResultado")
                .removePrefix("Resultado da matéria:").trim()
                .substringBefore("\n").trim()
            if (resultado.isBlank()) null
            else ItemSenado(
                identificacao = i.str("identificacao").trim(),
                ementa = i.str("ementaPapeleta").trim().replaceFirstChar { it.uppercase(BR) },
                resultado = resultado
            )
        }
        if (itens.isEmpty()) return null
        val s0 = sessoes.first().asJsonObject
        SenadoDia(s0.str("dataSessao"), s0.str("descricaoTipoSessao"), itens)
    }.getOrNull()

    // ---------- TSE ----------

    fun eleicao(json: String, url: String): Eleicao? = runCatching {
        val root = JsonParser.parseString(json).asJsonObject
        val cargo = root.getAsJsonArray("carg")[0].asJsonObject
        val turno = root.str("t")
        val candidatos = cargo.getAsJsonArray("agr").flatMap { agr ->
            agr.asJsonObject.getAsJsonArray("par").flatMap { par ->
                val sigla = par.asJsonObject.str("sg")
                par.asJsonObject.getAsJsonArray("cand").map { c ->
                    val o = c.asJsonObject
                    Candidato(
                        nome = o.str("nmu").ifBlank { o.str("nm") },
                        partido = sigla,
                        percentual = o.str("pvap"),
                        votos = o.str("vap").toLongOrNull() ?: 0,
                        eleito = o.str("e") == "s"
                    )
                }
            }
        }.sortedByDescending { it.votos }
        if (candidatos.isEmpty()) return null
        Eleicao(
            titulo = "${cargo.str("nmn")}, ${turno}º turno",
            dataTotalizacao = "${root.str("dg")} ${root.str("hg")}".trim(),
            candidatos = candidatos,
            fonteUrl = url
        )
    }.getOrNull()

    // ---------- RSS ----------

    /** Decodes using the charset the XML prolog declares (the Senado feed is ISO-8859-1). */
    fun decodificar(bytes: ByteArray): String {
        val prologo = String(bytes, 0, minOf(bytes.size, 200), Charsets.ISO_8859_1)
        val cs = Regex("""encoding=["']([\w-]+)["']""").find(prologo)?.groupValues?.get(1)
        return String(bytes, runCatching { Charset.forName(cs ?: "UTF-8") }.getOrDefault(Charsets.UTF_8))
    }

    private val ITEM = Regex("""<item[\s>](.*?)</item>""", RegexOption.DOT_MATCHES_ALL)

    fun rss(xml: String, fonte: String): List<Noticia> = ITEM.findAll(xml).mapNotNull { m ->
        val corpo = m.groupValues[1]
        val titulo = limparHtml(tag(corpo, "title"))
        val link = tag(corpo, "link").trim()
        if (titulo.isBlank() || !link.startsWith("http")) return@mapNotNull null
        Noticia(
            titulo = titulo,
            resumo = limparHtml(tag(corpo, "description")).let { if (it.length > 220) it.take(220).substringBeforeLast(' ') + "…" else it },
            link = link,
            fonte = fonte,
            publicadaEm = data(tag(corpo, "pubDate").trim())
        )
    }.toList()

    private fun tag(corpo: String, nome: String): String =
        Regex("""<$nome(?:\s[^>]*)?>(.*?)</$nome>""", RegexOption.DOT_MATCHES_ALL).find(corpo)?.groupValues?.get(1)
            ?.replace(Regex("""^\s*<!\[CDATA\[(.*)]]>\s*$""", RegexOption.DOT_MATCHES_ALL), "$1")
            .orEmpty()

    fun limparHtml(s: String): String {
        var t = s.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"")
            .replace("&#39;", "'").replace("&nbsp;", " ")
        t = t.replace(Regex("""<[^>]+>"""), " ").replace("&amp;", "&")
        return t.replace('\u00A0', ' ').replace(Regex("""\s+"""), " ").trim()
    }

    private fun data(s: String): Long {
        if (s.isBlank()) return 0
        val formatos = listOf("EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd MMM yyyy HH:mm:ss Z", "yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd'T'HH:mm:ss")
        for (f in formatos) {
            val r = runCatching {
                SimpleDateFormat(f, Locale.US).apply { timeZone = TimeZone.getTimeZone("America/Sao_Paulo") }.parse(s)?.time
            }.getOrNull()
            if (r != null) return r
        }
        return 0
    }

    // ---------- helpers ----------

    fun pct(v: Double) = "%.2f%%".format(BR, v).replace(",00%", "%")

    fun reais(v: Double) = "R$ %,.2f".format(BR, v)

    fun bilhoes(v: Double) = LeiExplicada.humanizarReais(v)

    private fun JsonObject.str(k: String): String =
        get(k)?.takeIf { !it.isJsonNull && it.isJsonPrimitive }?.asString.orEmpty()

    private fun JsonElement.listaOuUm(): List<JsonElement> =
        if (this is JsonArray) this.toList() else listOf(this)
}
