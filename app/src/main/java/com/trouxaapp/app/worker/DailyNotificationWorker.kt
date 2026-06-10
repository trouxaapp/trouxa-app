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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Optional daily digest, opt-in via UserPrefs.notifResumoDiario.
 *
 * Replaces the old 190-template system whose phrases hardcoded 2024/2025
 * numbers. This digest is assembled exclusively from live cached data with
 * reference dates. If a part has no fresh data, that part is omitted; if
 * nothing fresh exists, NO notification is sent. Silence over invention.
 *
 * WorkManager periodic jobs drift, so doWork() re-checks the wall clock:
 * outside 08-21h it skips (no 3 AM digests), and lastDigestDay prevents
 * duplicates when the scheduler fires twice in a day.
 */
@HiltWorker
class DailyNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val leiDao: LeiDao,
    private val indicadorRepo: IndicadorRepository
) : CoroutineWorker(context, params) {

    companion object {
        private const val CHANNEL_ID = "resumo_diario"
        private const val NOTIF_ID = 1001

        fun schedule(context: Context) {
            // Anchor the first run near 19h local; after that, the in-run
            // clock checks handle WorkManager drift.
            val now = Calendar.getInstance()
            val target = (now.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 19)
                set(Calendar.MINUTE, 0)
                if (before(now)) add(Calendar.DAY_OF_MONTH, 1)
            }
            val delayMin = (target.timeInMillis - now.timeInMillis) / 60000L
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "daily_digest",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<DailyNotificationWorker>(24, TimeUnit.HOURS)
                    .setInitialDelay(delayMin, TimeUnit.MINUTES)
                    .setConstraints(
                        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                    )
                    .build()
            )
        }
    }

    override suspend fun doWork(): Result {
        val prefs = UserPrefs(context)
        if (!prefs.notifResumoDiario) return Result.success()
        if (!hasNotifPermission()) return Result.success()

        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        if (hour < 8 || hour > 21) return Result.success()

        val dayStamp = cal.get(Calendar.YEAR) * 1000 + cal.get(Calendar.DAY_OF_YEAR)
        if (prefs.lastDigestDay == dayStamp) return Result.success()

        val partes = mutableListOf<String>()

        // Bills presented today, straight from the local Camara cache
        val hojeIso = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
        val novasHoje = leiDao.countApresentadasDesde(hojeIso)
        if (novasHoje > 0) {
            partes.add(
                if (novasHoje == 1) "1 projeto novo apresentado na Câmara hoje"
                else "$novasHoje projetos novos apresentados na Câmara hoje"
            )
        }

        val ptBr = Locale("pt", "BR")
        indicadorRepo.getBySerie(BcbApiService.DOLAR)?.let {
            if (!indicadorRepo.isStale(it)) {
                partes.add(String.format(ptBr, "dólar R$ %.2f (PTAX %s)", it.valor, it.data))
            }
        }
        indicadorRepo.getBySerie(BcbApiService.SELIC)?.let {
            if (!indicadorRepo.isStale(it)) {
                partes.add(String.format(ptBr, "Selic %.2f%% ao ano", it.valor))
            }
        }

        // Nothing fresh, nothing sent
        if (partes.isEmpty()) return Result.success()

        val corpo = partes.joinToString(" · ").replaceFirstChar { it.uppercase(ptBr) } +
            ". Fontes: Câmara dos Deputados e Banco Central."

        sendNotification("Resumo do dia", corpo)
        prefs.lastDigestDay = dayStamp
        return Result.success()
    }

    private fun hasNotifPermission(): Boolean =
        android.os.Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    private fun sendNotification(titulo: String, corpo: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Resumo diário", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Um resumo por dia com dados oficiais e suas datas"
            }
        )
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pending = PendingIntent.getActivity(
            context, 0, intent,
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
