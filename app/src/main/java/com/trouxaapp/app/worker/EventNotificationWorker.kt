package com.trouxaapp.app.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.trouxaapp.app.MainActivity
import com.trouxaapp.app.R
import com.trouxaapp.app.data.local.LeiDao
import com.trouxaapp.app.data.remote.BcbApiService
import com.trouxaapp.app.data.repository.IndicadorRepository
import com.trouxaapp.app.util.UserPrefs
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Event-driven push. Strictly opt-in (UserPrefs.notifEventos).
 *
 * Integrity rules, in order of importance:
 *  1. Never push a number that did not come from an API with a reference
 *     date. If the cache is stale (>36h) or absent, there is no push.
 *  2. The verb matches the procedural fact: a presented bill was
 *     "apresentado", never "colocado em votação".
 *  3. Laws outrank indicator events (they are the core of the app), and
 *     last-seen state only advances when its own notification fires, so
 *     losing a priority round does not silently swallow a law forever.
 */
@HiltWorker
class EventNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val leiDao: LeiDao,
    private val indicadorRepo: IndicadorRepository
) : CoroutineWorker(context, params) {

    companion object {
        private const val CHANNEL_ID = "eventos"
        private const val NOTIF_ID = 2001
        private val THROTTLE_MS = TimeUnit.HOURS.toMillis(6)
        private const val QUIET_START = 22
        private const val QUIET_END = 7
        private const val TEMPERATURA_MINIMA = 40

        fun schedule(context: Context) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "event_notification",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<EventNotificationWorker>(3, TimeUnit.HOURS)
                    .setConstraints(
                        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                    )
                    .build()
            )
        }
    }

    override suspend fun doWork(): Result {
        val prefs = UserPrefs(context)
        if (!prefs.notifEventos) return Result.success()
        if (!hasNotifPermission()) return Result.success()

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hour >= QUIET_START || hour < QUIET_END) return Result.success()

        val agora = System.currentTimeMillis()
        if (agora - prefs.lastEventNotifTimestamp < THROTTLE_MS) return Result.success()

        // Priority 1: new bill with real activity
        val maxId = leiDao.maxId() ?: 0
        if (maxId > prefs.lastSeenLeiId) {
            val nova = leiDao.getById(maxId)
            if (nova != null && nova.temperatura >= TEMPERATURA_MINIMA) {
                val autor = buildString {
                    append(nova.autorNome.ifBlank { "autoria não informada" })
                    if (nova.autorPartido.isNotBlank() && nova.autorUf.isNotBlank()) {
                        append(" (${nova.autorPartido}-${nova.autorUf})")
                    }
                }
                notify(
                    titulo = "Projeto em movimento na Câmara",
                    corpo = "${nova.siglaTipo} ${nova.numero}/${nova.ano}: ${nova.tituloPopular}. Apresentado por $autor.",
                    plId = nova.plId
                )
                prefs.lastSeenLeiId = maxId
                prefs.lastEventNotifTimestamp = agora
                return Result.success()
            }
            // Low-activity bills advance the cursor without a push;
            // they are still visible in the feed.
            if (nova != null && nova.temperatura < TEMPERATURA_MINIMA) {
                prefs.lastSeenLeiId = maxId
            }
        }

        // Priority 2: Selic change. Both sides of the comparison must be
        // real BCB values; the first run only records the baseline.
        val selic = indicadorRepo.getBySerie(BcbApiService.SELIC)
        if (selic != null && !indicadorRepo.isStale(selic)) {
            val anterior = prefs.lastSeenSelic
            if (anterior > 0f && abs(selic.valor - anterior) >= 0.05) {
                notify(
                    titulo = "Selic mudou",
                    corpo = String.format(
                        Locale("pt", "BR"),
                        "De %.2f%% pra %.2f%% ao ano. Decisão do Copom, dado do Banco Central de %s.",
                        anterior, selic.valor, selic.data
                    ),
                    plId = 0
                )
                prefs.lastSeenSelic = selic.valor.toFloat()
                prefs.lastEventNotifTimestamp = agora
                return Result.success()
            }
            if (anterior <= 0f) prefs.lastSeenSelic = selic.valor.toFloat()
        }

        // Priority 3: dollar swing >= 2% between two real PTAX readings
        val dolar = indicadorRepo.getBySerie(BcbApiService.DOLAR)
        if (dolar != null && !indicadorRepo.isStale(dolar)) {
            val anterior = prefs.lastSeenDolar
            if (anterior > 0f) {
                val variacao = (dolar.valor - anterior) / anterior * 100
                if (abs(variacao) >= 2.0) {
                    notify(
                        titulo = "Dólar se mexeu",
                        corpo = String.format(
                            Locale("pt", "BR"),
                            "De R$ %.2f pra R$ %.2f (%+.1f%%). Cotação PTAX do Banco Central de %s.",
                            anterior, dolar.valor, variacao, dolar.data
                        ),
                        plId = 0
                    )
                    prefs.lastSeenDolar = dolar.valor.toFloat()
                    prefs.lastEventNotifTimestamp = agora
                    return Result.success()
                }
            } else {
                prefs.lastSeenDolar = dolar.valor.toFloat()
            }
        }

        return Result.success()
    }

    private fun hasNotifPermission(): Boolean =
        android.os.Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    private fun notify(titulo: String, corpo: String, plId: Int) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Acontecimentos", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Projetos novos em movimento e mudanças em indicadores oficiais"
            }
        )
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (plId > 0) putExtra("plId", plId)
        }
        val pending = PendingIntent.getActivity(
            context, plId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_splash)
            .setContentTitle(titulo)
            .setContentText(corpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(corpo))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
        nm.notify(NOTIF_ID, notif)
    }
}
