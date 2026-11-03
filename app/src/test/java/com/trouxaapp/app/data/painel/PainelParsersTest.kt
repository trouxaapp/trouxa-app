package com.trouxaapp.app.data.painel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PainelParsersTest {

    @Test
    fun stepSeriesSaysSinceWhen() {
        val json = """[{"data":"01/01/2026","valor":"14.25"},{"data":"02/01/2026","valor":"14.25"},
            {"data":"17/09/2026","valor":"13.75"},{"data":"18/09/2026","valor":"13.75"},{"data":"01/01/2099","valor":"9"}]"""
        val pontos = PainelParsers.pontosSgs(json, agora = 4_102_444_800_000 - 1) // before 2100, after 2026
            .filter { it.data != "01/01/2099" }
        val ind = PainelParsers.desdeQuando("Selic", pontos, { "%.2f%%".format(it) }, "BCB", "")!!
        assertTrue(ind.mudanca, ind.mudanca.startsWith("Último corte em 17/09/2026"))
    }

    @Test
    fun placarAndRedacaoFinal() {
        val json = """{"dados":[
            {"id":"1-1","data":"2026-10-28","siglaOrgao":"PLEN","uriEvento":"https://x/eventos/777","descricao":"Rejeitado o Requerimento. Sim: 134; Não: 285; Abstenção: 2; Total: 421","aprovacao":0},
            {"id":"1-2","data":"2026-10-28","siglaOrgao":"PLEN","uriEvento":"","descricao":"Aprovada a Redação Final.","aprovacao":1}]}"""
        val v = PainelParsers.votacoes(json)
        assertEquals(1, v.size)
        assertEquals("Sim 134 x Não 285, 2 abstenções", v[0].votacao.placar)
        assertEquals(false, v[0].votacao.aprovada)
        assertEquals("https://www.camara.leg.br/evento-legislativo/777", v[0].votacao.url)
    }

    @Test
    fun symbolicVotesNeedABill() {
        val json = """{"dados":[
            {"id":"2-1","data":"2026-10-28","descricao":"Aprovado, por unanimidade, o Requerimento de Urgência (Art. 155 do RICD).","aprovacao":1},
            {"id":"2-2","data":"2026-10-27","descricao":"Mantido o texto.","aprovacao":null},
            {"id":"2-3","data":"2026-10-27","descricao":"Aprovado o Substitutivo ao Projeto de Lei nº 5.239, de 2025, adotado pela relatora.","aprovacao":1},
            {"id":"2-4","data":"2026-10-27","descricao":"Aprovada a Medida Provisória nº 1.375, de 2026, na forma do Projeto de Lei de Conversão.","aprovacao":1}]}"""
        assertEquals(listOf("2-3", "2-4"), PainelParsers.votacoes(json).map { it.id })
    }

    @Test
    fun creditTypoStillParses() {
        val json = """{"dados":[{"ementa":"Abre crédito extraordinário em favor do Ministério das Cidades, no valor de R${'$'}N195.000.000,00, para os fins que especifica."},
            {"ementa":"Altera a Medida Provisória nº 1.354, que abre crédito extraordinário"}],"links":[]}"""
        val (v, next) = PainelParsers.creditosDaPagina(json)
        assertEquals(listOf(195_000_000.0), v)
        assertEquals(null, next)
    }

    // Parses saved real responses when -Ppainel=<dir> is given
    @Test
    fun amostrasReais() {
        val dir = File(System.getProperty("painel") ?: return)
        if (!dir.isDirectory) return
        val out = StringBuilder()
        File(dir, "ibov.json").takeIf { it.exists() }?.let { out.append(PainelParsers.ibovespa(it.readText())).append("\n") }
        File(dir, "dolar.json").takeIf { it.exists() }?.let {
            out.append(PainelParsers.variacaoDiaria("Dólar", PainelParsers.pontosSgs(it.readText(), System.currentTimeMillis()), PainelParsers::reais, "BCB", "")).append("\n")
        }
        File(dir, "selic.json").takeIf { it.exists() }?.let {
            out.append(PainelParsers.desdeQuando("Selic", PainelParsers.pontosSgs(it.readText(), System.currentTimeMillis()), { "%.2f%%".format(it) }, "BCB", "")).append("\n")
        }
        File(dir, "ipca.json").takeIf { it.exists() }?.let {
            val p = PainelParsers.precos(it.readText(), "")
            assertNotNull(p); out.append(p).append("\n")
        }
        File(dir, "ev.json").takeIf { it.exists() }?.let { out.append(PainelParsers.camaraHoje(it.readText())).append("\n") }
        File(dir, "vot.json").takeIf { it.exists() }?.let { PainelParsers.votacoes(it.readText()).take(6).forEach { v -> out.append(v).append("\n") } }
        File(dir, "sen.json").takeIf { it.exists() }?.let { out.append(PainelParsers.senado(it.readText())).append("\n") }
        File(dir, "tse.json").takeIf { it.exists() }?.let { out.append(PainelParsers.eleicao(it.readText(), "")).append("\n") }
        for (f in listOf("eleicoes.xml", "senado.xml", "abr.xml")) {
            File(dir, f).takeIf { it.exists() }?.let {
                PainelParsers.rss(PainelParsers.decodificar(it.readBytes()), f).take(2).forEach { n -> out.append(n).append("\n") }
            }
        }
        File(dir, "saida.txt").writeText(out.toString())
    }
}
