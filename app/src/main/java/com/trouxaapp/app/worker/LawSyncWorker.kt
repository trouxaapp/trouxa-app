package com.trouxaapp.app.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.trouxaapp.app.data.local.SyncLogDao
import com.trouxaapp.app.data.local.SyncLogEntity
import com.trouxaapp.app.data.repository.LeisRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Syncs bills from the Camara: new bills, curation overrides, tramitacao
 * refresh. Partial failures are reported in the log entry instead of
 * hidden behind a green check.
 */
@HiltWorker
class LawSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repo: LeisRepository,
    private val syncLogDao: SyncLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Keep the telemetry table bounded; without this it grows forever.
        syncLogDao.purgeOlderThan(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7))
        return try {
            val r = repo.syncAll()
            syncLogDao.insert(
                SyncLogEntity(
                    worker = "law",
                    timestamp = System.currentTimeMillis(),
                    success = true,
                    itemsChanged = r.novas + r.tramitacoesAtualizadas + r.curadas,
                    error = if (r.falhas > 0) "${r.falhas} proposições falharam no processamento" else ""
                )
            )
            Result.success()
        } catch (e: Exception) {
            Log.e("LawSyncWorker", "sync failed", e)
            syncLogDao.insert(
                SyncLogEntity(
                    worker = "law",
                    timestamp = System.currentTimeMillis(),
                    success = false,
                    error = "${e.javaClass.simpleName}: ${e.message ?: "unknown"}"
                )
            )
            Result.retry()
        }
    }
}
