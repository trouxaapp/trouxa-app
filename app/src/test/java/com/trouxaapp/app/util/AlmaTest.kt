package com.trouxaapp.app.util

import com.trouxaapp.app.data.local.LeiEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * A alma só pode ser engraçada se continuar sendo verdadeira: cada teste
 * aqui amarra um selo/veredito num fato do projeto.
 */
class AlmaTest {

    private fun lei(
        sigla: String = "PL",
        ementa: String = "",
        situacao: String = "",
        despacho: String = ""
    ) = LeiEntity(
        plId = 1,
        siglaTipo = sigla,
        ementa = ementa,
        ultimaSituacao = situacao,
        despacho = despacho
    )

    @Test
    fun `medida provisoria carimba JA VALE porque ja esta valendo`() {
        val selos = Alma.seloDoProjeto(lei(sigla = "MPV", ementa = "Abre crédito extraordinário"))
        assertTrue(selos.contains("JÁ VALE"))
    }

    @Test
    fun `credito extraordinario diz que o gasto vem antes do voto`() {
        val v = Alma.vereditoDoProjeto(lei(ementa = "Abre crédito extraordinário, no valor de R\$ 1.000.000.000,00"))
        assertTrue(v.contains("antes de qualquer deputado votar"))
    }

    @Test
    fun `pec avisa o quorum real`() {
        val v = Alma.vereditoDoProjeto(lei(sigla = "PEC", ementa = "Altera a Constituição Federal"))
        assertTrue(v.contains("308 deputados"))
    }

    @Test
    fun `projeto que so da nome nao muda a vida de ninguem`() {
        val l = lei(ementa = "Denomina Rodovia Federal o trecho da BR-101")
        assertTrue(Alma.seloDoProjeto(l).contains("SÓ HOMENAGEM"))
        assertTrue(Alma.vereditoDoProjeto(l).contains("Passa fácil"))
    }

    @Test
    fun `criar beneficio sem valor conhecido nao inventa numero`() {
        val v = Alma.vereditoDoProjeto(lei(ementa = "Institui auxílio emergencial para famílias"))
        assertTrue(v.contains("despesa nova"))
        assertTrue(!v.contains("R\$"))
    }

    @Test
    fun `com valor, o veredito usa o valor`() {
        val v = Alma.vereditoDoProjeto(
            lei(ementa = "Institui auxílio para famílias"),
            valor = "R$ 300 milhões"
        )
        assertTrue(v.startsWith("R$ 300 milhões"))
    }

    @Test
    fun `curadoria humana vence a regra`() {
        val p = Alma.punchDoProjeto(
            lei(ementa = "Denomina Rodovia Federal o trecho da BR-101"),
            seloCurado = "MAIS UM NOME",
            vereditoCurado = "Texto escrito por humano."
        )
        assertEquals(listOf("MAIS UM NOME"), p.selos)
        assertEquals("Texto escrito por humano.", p.veredito)
    }

    @Test
    fun `ementa sem padrao conhecido nao gera veredito`() {
        val p = Alma.punchDoProjeto(lei(ementa = "Dispõe sobre critérios de rateio do salário-educação"))
        assertTrue(p.veredito.isBlank())
    }

    @Test
    fun `esculacho e estavel dentro do dia e muda entre dias`() {
        assertEquals(Alma.esculacho(20261110L), Alma.esculacho(20261110L))
        assertTrue(Alma.esculacho(1L).isNotBlank())
    }

    @Test
    fun `veredito do credito fala o total e a quantidade de MPs`() {
        val v = Alma.vereditoCredito(2020, 500e9, 30)
        assertTrue(v.contains("R$ 500"))
        assertTrue(v.contains("30 MPs"))
    }

    @Test
    fun `minimo diz o quanto cobre do necessario`() {
        val v = Alma.vereditoSalarioMinimo(1621.0, 7750.0)
        assertTrue(v.contains("21%"))
    }
}
