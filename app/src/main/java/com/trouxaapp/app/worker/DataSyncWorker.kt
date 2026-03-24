package com.trouxaapp.app.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.trouxaapp.app.data.remote.BcbApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Syncs economic indicators from BCB API every 6 hours.
 * Stores in SharedPreferences for quick access.
 */
class DataSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "trouxa_data_sync"
        const val PREFS_NAME = "trouxa_indicators"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val work = PeriodicWorkRequestBuilder<DataSyncWorker>(6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                work
            )
        }

        fun getIndicators(context: Context): EconomicIndicators {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return EconomicIndicators(
                dolar = prefs.getFloat("dolar", 5.52f).toDouble(),
                selic = prefs.getFloat("selic", 14.25f).toDouble(),
                ipca = prefs.getFloat("ipca", 5.2f).toDouble(),
                lastUpdate = prefs.getString("last_update", "mar/2026") ?: "mar/2026"
            )
        }
    }

    override suspend fun doWork(): Result {
        val bcbApi = Retrofit.Builder()
            .baseUrl(BcbApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BcbApiService::class.java)

        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        try {
            // Dólar (último valor apenas)
            val dolarData = bcbApi.getSerieUltimos(BcbApiService.DOLAR, 1)
            dolarData.lastOrNull()?.valor?.toDoubleOrNull()?.let {
                editor.putFloat("dolar", it.toFloat())
            }

            // Selic meta COPOM (último valor)
            val selicData = bcbApi.getSerieUltimos(BcbApiService.SELIC, 1)
            selicData.lastOrNull()?.valor?.toDoubleOrNull()?.let {
                editor.putFloat("selic", it.toFloat())
            }

            // IPCA (últimos 12 meses, somar)
            val ipcaData = bcbApi.getSerieUltimos(BcbApiService.IPCA, 12)
            if (ipcaData.isNotEmpty()) {
                val accumulated = ipcaData.sumOf { it.valor.toDoubleOrNull() ?: 0.0 }
                editor.putFloat("ipca", accumulated.toFloat())
            }

            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            editor.putString("last_update", sdf.format(java.util.Date()))
            editor.apply()

            android.util.Log.i("DataSyncWorker", "Dados BCB atualizados com sucesso")

        } catch (e: Exception) {
            android.util.Log.e("DataSyncWorker", "Erro ao sincronizar BCB", e)
            return Result.retry()
        }

        return Result.success()
    }
}

data class EconomicIndicators(
    val dolar: Double = 5.52,
    val selic: Double = 14.25,
    val ipca: Double = 5.2,
    val lastUpdate: String = "mar/2026"
)
