package com.trouxaapp.app.util

import java.text.Normalizer
import java.text.NumberFormat
import java.util.Locale

object FormatUtils {
    private val brLocale = Locale("pt", "BR")
    private val currencyFormat = NumberFormat.getCurrencyInstance(brLocale)

    // People type "credito" for "crédito" and "jose" for "José"; search compares without accents
    fun semAcento(s: String): String =
        Normalizer.normalize(s, Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "").lowercase(brLocale)

    fun formatCurrency(value: Double): String {
        return currencyFormat.format(value)
    }

    fun formatPercent(value: Double): String {
        val sign = if (value > 0) "+" else ""
        return "$sign%.1f%%".format(value)
    }
}
