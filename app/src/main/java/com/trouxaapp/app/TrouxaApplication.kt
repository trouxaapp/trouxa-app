package com.trouxaapp.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.trouxaapp.app.worker.DailyNotificationWorker
import com.trouxaapp.app.worker.EventNotificationWorker
import com.trouxaapp.app.worker.FeedSyncWorker
import com.trouxaapp.app.worker.IndicadorSyncWorker
import com.trouxaapp.app.worker.LawSyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Engine setup:
 *   1. HiltWorkerFactory wired in via Configuration.Provider (so @HiltWorker works)
 *   2. On boot: enqueueUniqueWork(REPLACE) one-shot of each sync worker -> data instant
 *   3. Periodic workers with KEEP -> mantém o cron
 *   4. Notif workers periodic
 */
@HiltAndroidApp
class TrouxaApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleEngine()
    }

    private fun scheduleEngine() {
        val wm = WorkManager.getInstance(this)
        val net = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 1) ONE-SHOT IMEDIATO: roda na hora pra popular o cache na primeira abertura
        wm.enqueueUniqueWork(
            "indicador_sync_now",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<IndicadorSyncWorker>()
                .setConstraints(net)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                .build()
        )
        wm.enqueueUniqueWork(
            "feed_sync_now",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<FeedSyncWorker>()
                .setConstraints(net)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                .build()
        )
        wm.enqueueUniqueWork(
            "lei_sync_now",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<LawSyncWorker>()
                .setConstraints(net)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)
                .build()
        )

        // 2) PERIODIC: mantém a engrenagem girando
        wm.enqueueUniquePeriodicWork(
            "indicador_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<IndicadorSyncWorker>(6, TimeUnit.HOURS)
                .setInitialDelay(6, TimeUnit.HOURS)
                .setConstraints(net)
                .build()
        )
        wm.enqueueUniquePeriodicWork(
            "lei_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<LawSyncWorker>(3, TimeUnit.HOURS)
                .setInitialDelay(3, TimeUnit.HOURS)
                .setConstraints(net)
                .build()
        )
        wm.enqueueUniquePeriodicWork(
            "feed_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<FeedSyncWorker>(12, TimeUnit.HOURS)
                .setInitialDelay(12, TimeUnit.HOURS)
                .setConstraints(net)
                .build()
        )

        // 3) NOTIFICACOES
        DailyNotificationWorker.schedule(this)
        EventNotificationWorker.schedule(this)
    }
}
