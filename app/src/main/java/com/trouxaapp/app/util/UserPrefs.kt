package com.trouxaapp.app.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Local-only user preferences. Replaces the old EngagementManager
 * (streak/gamification dropped in the rewrite).
 *
 * LGPD note: everything here stays on the device. The only personal-ish
 * datum is the optional UF, used to list the user's state deputies.
 * Nothing is ever transmitted anywhere.
 */
class UserPrefs(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("trouxa_prefs", Context.MODE_PRIVATE)

    var onboardingDone: Boolean
        get() = prefs.getBoolean("onboarding_done", false)
        set(value) { prefs.edit().putBoolean("onboarding_done", value).apply() }

    var userUf: String
        get() = prefs.getString("user_uf", "") ?: ""
        set(value) { prefs.edit().putString("user_uf", value).apply() }

    // Notifications are strictly opt-in (consent given in onboarding or settings)
    var notifEventos: Boolean
        get() = prefs.getBoolean("notif_eventos", false)
        set(value) { prefs.edit().putBoolean("notif_eventos", value).apply() }

    var notifResumoDiario: Boolean
        get() = prefs.getBoolean("notif_resumo_diario", false)
        set(value) { prefs.edit().putBoolean("notif_resumo_diario", value).apply() }

    // Last-seen state for event diffing (written by EventNotificationWorker)
    var lastSeenLeiId: Int
        get() = prefs.getInt("last_seen_lei_id", 0)
        set(value) { prefs.edit().putInt("last_seen_lei_id", value).apply() }

    var lastSeenSelic: Float
        get() = prefs.getFloat("last_seen_selic", 0f)
        set(value) { prefs.edit().putFloat("last_seen_selic", value).apply() }

    var lastSeenDolar: Float
        get() = prefs.getFloat("last_seen_dolar", 0f)
        set(value) { prefs.edit().putFloat("last_seen_dolar", value).apply() }

    var lastEventNotifTimestamp: Long
        get() = prefs.getLong("last_event_notif_ts", 0L)
        set(value) { prefs.edit().putLong("last_event_notif_ts", value).apply() }

    var lastDigestDay: Int
        get() = prefs.getInt("last_digest_day", 0)
        set(value) { prefs.edit().putInt("last_digest_day", value).apply() }

    // ---- ofensiva (o vício) ----------------------------------------------
    // Contagem de dias seguidos abrindo o app. Guardada como número do dia
    // (epoch em dias, UTC) porque comparar data em string dá trabalho e o
    // fuso não muda nada aqui: o que interessa é "hoje" != "ontem" != "antes".

    private val inicioDoDia: Long get() = System.currentTimeMillis() / 86_400_000L

    var ofensiva: Int
        get() = prefs.getInt("ofensiva", 0)
        private set(value) { prefs.edit().putInt("ofensiva", value).apply() }

    private var ultimoDiaVisto: Long
        get() = prefs.getLong("ultimo_dia_visto", 0L)
        set(value) { prefs.edit().putLong("ultimo_dia_visto", value).apply() }

    var verdadesVistas: Int
        get() = prefs.getInt("verdades_vistas", 0)
        set(value) { prefs.edit().putInt("verdades_vistas", value).apply() }

    /**
     * Registra a visita de hoje e devolve a ofensiva atualizada.
     * Ontem conta como sequência; qualquer buraco maior zera em 1.
     */
    fun registrarVisita(): Int {
        val hoje = inicioDoDia
        when (ultimoDiaVisto) {
            hoje -> Unit
            hoje - 1 -> ofensiva += 1
            else -> ofensiva = 1
        }
        ultimoDiaVisto = hoje
        return ofensiva
    }
}
