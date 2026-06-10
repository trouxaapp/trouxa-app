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

/**
 * Refreshes BCB indicators (dolar, Selic, IPCA 12m, salario minimo).
 * A partial refresh (some series down) is logged as success with the
 * real count; zero updates with no exception is logged as failure so
 * the Settings panel does not show a green check over dead data.
 */
@HiltWorker
class IndicadorSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repo: IndicadorRepository,
    private val syncLogDao: SyncLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val updated = repo.refresh()
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
