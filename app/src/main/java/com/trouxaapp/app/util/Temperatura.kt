package com.trouxaapp.app.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Objective activity score for a bill, 0 to 100.
 *
 * Replaces the old "nivelTrouxa" (a per-theme constant masquerading as a
 * per-law judgment). Here every point comes from a verifiable procedural
 * fact returned by the Camara API, and each contributing fact is listed
 * in `motivos` so the UI can show exactly WHY a bill is ranked hot.
 * Nothing here is an opinion about whether the bill is good or bad.
 */
object Temperatura {

    data class Resultado(val valor: Int, val motivos: List<String>)

    fun compute(
        siglaTipo: String,
        dataApresentacao: String,
        ultimaSituacao: String,
        despacho: String,
        tramitacaoDatas: List<String>,
        agora: Long = System.currentTimeMillis()
    ): Resultado {
        var pontos = 0
        val motivos = mutableListOf<String>()

        if (siglaTipo == "MPV" || siglaTipo == "MP") {
            pontos += 25
            motivos.add("Medida Provisória: já está valendo e o Congresso tem 120 dias pra votar")
        }

        val urgente = listOf(ultimaSituacao, despacho).any {
            it.contains("urgênc", ignoreCase = true) || it.contains("urgenc", ignoreCase = true)
        }
        if (urgente) {
            pontos += 30
            motivos.add("Tramita em regime de urgência")
        }

        if (ultimaSituacao.contains("Pronta para a Pauta", ignoreCase = true) ||
            ultimaSituacao.contains("Pronta para Pauta", ignoreCase = true)
        ) {
            pontos += 20
            motivos.add("Pronta pra entrar na pauta de votação")
        }

        val datas = tramitacaoDatas.mapNotNull(::parseDate)
        val mov7 = datas.count { agora - it <= TimeUnit.DAYS.toMillis(7) }
        val mov30 = datas.count { agora - it <= TimeUnit.DAYS.toMillis(30) }
        if (mov7 > 0) {
            pontos += minOf(20, mov7 * 7)
            motivos.add(if (mov7 == 1) "1 movimentação nos últimos 7 dias" else "$mov7 movimentações nos últimos 7 dias")
        } else if (mov30 > 0) {
            pontos += minOf(12, mov30 * 3)
            motivos.add(if (mov30 == 1) "1 movimentação nos últimos 30 dias" else "$mov30 movimentações nos últimos 30 dias")
        }

        parseDate(dataApresentacao)?.let { apresentada ->
            if (agora - apresentada <= TimeUnit.DAYS.toMillis(7)) {
                pontos += 10
                motivos.add("Apresentada há menos de 7 dias")
            }
        }

        return Resultado(valor = minOf(100, pontos), motivos = motivos)
    }

    // SimpleDateFormat is not thread-safe and compute() runs from up to 6
    // parallel sync coroutines, so the formats are created per call instead
    // of shared as object-level fields.
    private fun parseDate(s: String): Long? {
        if (s.isBlank()) return null
        return try {
            if (s.length >= 16 && s.contains('T'))
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).parse(s.take(16))?.time
            else
                SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(s.take(10))?.time
        } catch (_: Exception) {
            null
        }
    }
}
