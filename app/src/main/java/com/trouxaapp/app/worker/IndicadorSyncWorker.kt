package com.trouxaapp.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.trouxaapp.app.data.local.SyncLogDao
import com.trouxaapp.app.data.local.SyncLogEntity
import com.trouxaapp.app.data.repository.IndicadorRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Refreshes BCB indicators (dolar, Selic, IPCA 12m, salario minimo).
 * A partial refresh (some series down) is logged as success with the
 * real count. Zero updates with cached rows still present is success
 * with a note (the app keeps serving honest dated values); zero updates
 * over an empty table is the only real failure worth retrying.
 */
@HiltWorker
class IndicadorSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repo: IndicadorRepository,
    private val syncLogDao: SyncLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Keep the telemetry table bounded; without this it grows forever.
        syncLogDao.purgeOlderThan(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7))
        return try {
            val updated = repo.refresh()
            if (updated == 0 && repo.hasAny()) {
                // BCB down but we still have cached values with their honest
                // dates. That is a healthy state; retrying only floods the log.
                syncLogDao.insert(
                    SyncLogEntity(
                        worker = "indicador",
                        timestamp = System.currentTimeMillis(),
                        success = true,
                        itemsChanged = 0,
                        error = "remoto indisponível; usando dados locais"
                    )
                )
                return Result.success()
            }
            syncLogDao.insert(
                SyncLogEntity(
                    worker = "indicador",
                    timestamp = System.currentTimeMillis(),
                    success = updated > 0,
                    itemsChanged = updated,
                    error = if (updated == 0) "nenhuma série retornou dado válido" else ""
                )
            )
            if (updated > 0) Result.success() else Result.retry()
        } catch (e: Exception) {
            syncLogDao.insert(
                SyncLogEntity(
                    worker = "indicador",
                    timestamp = System.currentTimeMillis(),
                    success = false,
                    error = "${e.javaClass.simpleName}: ${e.message ?: "unknown"}"
                )
            )
            Result.retry()
        }
    }
}
