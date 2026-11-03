package com.trouxaapp.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LeiExplicadaTest {

    @Test
    fun extraordinaryCreditReadsValueAndDestination() {
        val e = LeiExplicada.explicar(
            "MPV",
            "Abre crédito extraordinário, em favor do Ministério de Minas e Energia, no valor de R$ 7.520.000.000,00, para o fim que especifica."
        )
        assertEquals("R$ 7,52 bilhões", e.valor)
        assertTrue(e.emUmaFrase, e.emUmaFrase.contains("para o Ministério de Minas e Energia"))
        assertEquals("", e.objetivo)
        assertTrue(e.termos.any { it.termo == "Crédito extraordinário" })
    }

    @Test
    fun amendmentNamesTheLawAndQuotesThePurpose() {
        val e = LeiExplicada.explicar(
            "PL",
            "Altera a Lei nº 8.069, de 13 de julho de 1990 (Estatuto da Criança e do Adolescente), para estabelecer critérios de contextualização."
        )
        assertEquals("Muda uma lei que já existe: Estatuto da Criança e do Adolescente (Lei 8.069/1990).", e.emUmaFrase)
        assertEquals("estabelecer critérios de contextualização", e.objetivo)
    }

    @Test
    fun knownNumberGetsItsNickname() {
        val e = LeiExplicada.explicar("PLP", "Altera a Lei Complementar nº 64, de 18 de maio de 1990, para dispor sobre o abuso do poder acadêmico.")
        assertEquals("Muda uma lei que já existe: Lei de Inelegibilidades (Lei Complementar 64/1990).", e.emUmaFrase)
    }

    @Test
    fun humanizedMoney() {
        assertEquals("R$ 1 bilhão", LeiExplicada.humanizarReais(1_000_000_000.0))
        assertEquals("R$ 64,3 milhões", LeiExplicada.humanizarReais(64_289_400.0))
        assertEquals("R$ 300 milhões", LeiExplicada.humanizarReais(300_000_000.0))
    }

    // Dumps every real ementa sample when the file exists, for eyeballing
    @Test
    fun dumpSamples() {
        val f = File(System.getProperty("ementas") ?: return)
        if (!f.exists()) return
        val out = StringBuilder()
        var vazias = 0
        f.readLines().forEach { linha ->
            val (tipo, ementa) = linha.split('\t', limit = 2)
            val e = LeiExplicada.explicar(tipo, ementa)
            if (e.emUmaFrase.isBlank()) vazias++
            out.append("[$tipo] ").append(ementa.take(150)).append('\n')
                .append("  FRASE: ").append(e.emUmaFrase).append('\n')
                .append("  OBJETIVO: ").append(e.objetivo).append('\n')
                .append("  TERMOS: ").append(e.termos.joinToString { it.termo }).append("\n\n")
        }
        out.append("sem frase: $vazias")
        File(f.parentFile, "explicadas.txt").writeText(out.toString())
    }
}
