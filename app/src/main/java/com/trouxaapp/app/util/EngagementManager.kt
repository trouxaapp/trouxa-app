package com.trouxaapp.app.util

import android.content.Context
import android.content.SharedPreferences
import com.trouxaapp.app.data.model.StreakData

/**
 * Manages progressive feature unlock and daily streak.
 *
 * Day 1: Feed "Hoje" + Histórico (preços)
 * Day 2: "Por que?" nos cards
 * Day 3: "Quem decidiu" nos cards
 * Day 5: "Seu Deputado" completo
 * Day 7: Notificações inteligentes
 * Day 14: "Cobrar" (tweet pro deputado)
 */
class EngagementManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("trouxa_engagement", Context.MODE_PRIVATE)

    init {
        if (prefs.getLong("first_open", 0L) == 0L) {
            prefs.edit().putLong("first_open", System.currentTimeMillis()).apply()
        }
        trackDayUsed()
        updateStreak()
    }

    private fun trackDayUsed() {
        val today = (System.currentTimeMillis() / 86400000L).toInt()
        val lastDay = prefs.getInt("last_day", 0)
        if (today != lastDay) {
            val count = prefs.getInt("days_used", 0)
            prefs.edit()
                .putInt("days_used", count + 1)
                .putInt("last_day", today)
                .apply()
        }
    }

    private fun updateStreak() {
        val today = (System.currentTimeMillis() / 86400000L).toInt()
        val lastStreakDay = prefs.getInt("last_streak_day", 0)
        val currentStreak = prefs.getInt("streak", 0)

        when {
            lastStreakDay == 0 -> {
                // First time
                prefs.edit()
                    .putInt("streak", 1)
                    .putInt("last_streak_day", today)
                    .putBoolean("is_returning", false)
                    .apply()
            }
            today == lastStreakDay -> {
                // Same day, do nothing
            }
            today == lastStreakDay + 1 -> {
                // Consecutive day
                prefs.edit()
                    .putInt("streak", currentStreak + 1)
                    .putInt("last_streak_day", today)
                    .putBoolean("is_returning", false)
                    .apply()
            }
            else -> {
                // Missed a day, reset
                prefs.edit()
                    .putInt("streak", 1)
                    .putInt("last_streak_day", today)
                    .putBoolean("is_returning", true)
                    .apply()
            }
        }
    }

    val daysUsed: Int get() = prefs.getInt("days_used", 1)
    val streak: Int get() = prefs.getInt("streak", 1)
    val isReturning: Boolean get() = prefs.getBoolean("is_returning", false)

    fun getStreakData(): StreakData {
        val s = streak
        val returning = isReturning
        val message = if (returning) {
            "Voltou. Bom."
        } else when {
            s >= 30 -> "1 mês. Um homem informado vira ameaça pro estado."
            s >= 14 -> "2 semanas. Você virou uma ameaça pro sistema."
            s >= 7 -> "Uma semana. Ninguém te engana mais fácil."
            s >= 3 -> "3 dias. Ja sabe mais que a maioria."
            else -> "Primeiro dia. Bem-vindo à realidade."
        }
        return StreakData(days = s, message = message, isReturning = returning)
    }

    // Feature unlocks
    val showPrices: Boolean get() = true
    val showPorque: Boolean get() = daysUsed >= 2
    val showQuemDecidiu: Boolean get() = daysUsed >= 3
    val showDeputados: Boolean get() = daysUsed >= 5
    val showNotificações: Boolean get() = daysUsed >= 7
    val showCobrar: Boolean get() = daysUsed >= 14

    val progressPercent: Int get() = when {
        daysUsed >= 14 -> 100
        daysUsed >= 7 -> 85
        daysUsed >= 5 -> 70
        daysUsed >= 3 -> 50
        daysUsed >= 2 -> 35
        else -> 20
    }

    val progressText: String get() = "Você conhece $progressPercent% da verdade. Continue."

    fun getUnlockMessage(): String? {
        return when (daysUsed) {
            2 -> "Novo desbloqueado: agora você sabe POR QUE cada preço subiu."
            3 -> "Novo desbloqueado: agora você sabe QUEM decidiu cada preço."
            5 -> "Novo desbloqueado: descubra quanto seu deputado gasta."
            7 -> "Novo desbloqueado: notificações inteligentes disponíveis."
            14 -> "Novo desbloqueado: agora você pode COBRAR seu deputado."
            else -> null
        }
    }

    fun getLockedMessage(feature: String): String {
        return when (feature) {
            "porque" -> "Volte amanhã pra descobrir POR QUE o preço do arroz subiu."
            "quem" -> "Volte em ${3 - daysUsed} dias pra descobrir QUEM decidiu o preço."
            "deputados" -> "Volte em ${5 - daysUsed} dias pra ver seu deputado."
            "cobrar" -> "Volte em ${14 - daysUsed} dias pra poder cobrar seu deputado."
            else -> "Continue voltando pra desbloquear mais."
        }
    }

    // User salary for "Seu bolso agora" card
    var userSalary: Double
        get() = prefs.getFloat("user_salary", 0f).toDouble()
        set(value) { prefs.edit().putFloat("user_salary", value.toFloat()).apply() }

    // User UF for "Seu Deputado"
    var userUf: String
        get() = prefs.getString("user_uf", "") ?: ""
        set(value) { prefs.edit().putString("user_uf", value).apply() }

    // Notification preferences
    var notifCafe: Boolean
        get() = prefs.getBoolean("notif_cafe", false)
        set(value) { prefs.edit().putBoolean("notif_cafe", value).apply() }

    var notifAlmoco: Boolean
        get() = prefs.getBoolean("notif_almoco", false)
        set(value) { prefs.edit().putBoolean("notif_almoco", value).apply() }

    var notifTrabalho: Boolean
        get() = prefs.getBoolean("notif_trabalho", true) // default ON
        set(value) { prefs.edit().putBoolean("notif_trabalho", value).apply() }

    var notifNoite: Boolean
        get() = prefs.getBoolean("notif_noite", false)
        set(value) { prefs.edit().putBoolean("notif_noite", value).apply() }

    /** For development: unlock everything */
    fun unlockAll() {
        prefs.edit().putInt("days_used", 30).apply()
    }

    /** Reset progression */
    fun reset() {
        prefs.edit().clear().apply()
    }
}
