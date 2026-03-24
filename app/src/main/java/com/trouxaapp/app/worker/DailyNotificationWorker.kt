package com.trouxaapp.app.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.trouxaapp.app.MainActivity
import com.trouxaapp.app.R
import com.trouxaapp.app.data.local.HojeDataProvider
import com.trouxaapp.app.util.EngagementManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class DailyNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_CAFE = "trouxa_cafe"
        const val CHANNEL_ALMOCO = "trouxa_almoco"
        const val CHANNEL_TRABALHO = "trouxa_trabalho"
        const val CHANNEL_NOITE = "trouxa_noite"

        fun schedule(context: Context) {
            scheduleAt(context, "trouxa_notif_cafe", 7)
            scheduleAt(context, "trouxa_notif_almoco", 12)
            scheduleAt(context, "trouxa_notif_trabalho", 18)
            scheduleAt(context, "trouxa_notif_noite", 20)
        }

        private fun scheduleAt(context: Context, name: String, hour: Int) {
            val work = PeriodicWorkRequestBuilder<DailyNotificationWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(calculateDelayToHour(hour), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                name,
                ExistingPeriodicWorkPolicy.KEEP,
                work
            )
        }

        private fun calculateDelayToHour(hour: Int): Long {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, if (hour == 20) 30 else 0)
                set(Calendar.SECOND, 0)
            }
            if (target.before(now)) target.add(Calendar.DAY_OF_MONTH, 1)
            return target.timeInMillis - now.timeInMillis
        }
    }

    override suspend fun doWork(): Result {
        createChannels()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val engagement = EngagementManager(applicationContext)
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        // Check user preferences
        val shouldSend = when {
            hour in 6..9 -> engagement.notifCafe
            hour in 11..13 -> engagement.notifAlmoco
            hour in 17..19 -> engagement.notifTrabalho
            hour in 20..23 -> engagement.notifNoite
            else -> false
        }

        if (!shouldSend) return Result.success()

        val phrase = HojeDataProvider.getNotifByHourV2(hour, dayOfYear)
        val channelId = when {
            hour in 6..9 -> CHANNEL_CAFE
            hour in 11..13 -> CHANNEL_ALMOCO
            hour in 17..19 -> CHANNEL_TRABALHO
            else -> CHANNEL_NOITE
        }

        val notifId = when {
            hour in 6..9 -> 1001
            hour in 11..13 -> 1002
            hour in 17..19 -> 1003
            else -> 1004
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pending = PendingIntent.getActivity(
            applicationContext, notifId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("TROUXA")
            .setContentText(phrase)
            .setStyle(NotificationCompat.BigTextStyle().bigText(phrase))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(notifId, notification)
        return Result.success()
    }

    private fun createChannels() {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channels = listOf(
            NotificationChannel(CHANNEL_CAFE, "Café (7h)", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Dado do dia pela manhã"
            },
            NotificationChannel(CHANNEL_ALMOCO, "Almoço (12h)", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Dado na hora do almoço"
            },
            NotificationChannel(CHANNEL_TRABALHO, "Trabalho (18h)", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Dado no fim do expediente"
            },
            NotificationChannel(CHANNEL_NOITE, "Noite (20:30)", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Reflexao da noite"
            }
        )
        channels.forEach { manager.createNotificationChannel(it) }
    }
}
