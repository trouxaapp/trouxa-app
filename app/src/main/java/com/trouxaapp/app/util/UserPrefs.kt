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
}
