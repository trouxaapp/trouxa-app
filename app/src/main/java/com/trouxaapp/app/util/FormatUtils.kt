package com.trouxaapp.app.util

import java.text.NumberFormat
import java.util.Locale

object FormatUtils {
    private val brLocale = Locale("pt", "BR")
    private val currencyFormat = NumberFormat.getCurrencyInstance(brLocale)

    fun formatCurrency(value: Double): String {
        return currencyFormat.format(value)
    }

    fun formatPercent(value: Double): String {
        val sign = if (value > 0) "+" else ""
        return "$sign%.1f%%".format(value)
    }
}
