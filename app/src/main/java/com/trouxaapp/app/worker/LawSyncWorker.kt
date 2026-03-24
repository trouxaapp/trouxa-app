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
import com.trouxaapp.app.data.local.AnalyzedLeiEntity
import com.trouxaapp.app.data.local.AnalyzedLeiDao
import com.trouxaapp.app.data.remote.CamaraApiService
import com.trouxaapp.app.util.LawTranslator
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Background worker that syncs and translates laws every 6 hours.
 * Uses template-based translation instead of Gemini AI.
 */
class LawSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "trouxa_law_sync"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val work = PeriodicWorkRequestBuilder<LawSyncWorker>(6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                work
            )
        }
    }

    override suspend fun doWork(): Result {
        val camaraApi = Retrofit.Builder()
            .baseUrl("https://dadosabertos.camara.leg.br/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CamaraApiService::class.java)

        val db = androidx.room.Room.databaseBuilder(
            applicationContext,
            com.trouxaapp.app.data.local.TrouxaDatabase::class.java,
            "trouxa_db"
        ).fallbackToDestructiveMigration().build()

        val leiDao = db.analyzedLeiDao()

        try {
            // 1. Fetch recent propositions from Câmara
            val response = camaraApi.getProposicoes(itens = 50)

            for (prop in response.dados) {
                if (leiDao.exists(prop.id) > 0) continue
                if (prop.siglaTipo !in listOf("PL", "PEC", "PLP", "MPV", "PDL")) continue

                // Fetch author info
                var autorNome = ""
                var autorPartido = ""
                var autorUf = ""
                try {
                    val autores = camaraApi.getAutores(prop.id)
                    val firstAutor = autores.dados.firstOrNull()
                    autorNome = firstAutor?.nome ?: ""
                    if (autorNome.isNotEmpty()) {
                        try {
                            val deps = camaraApi.getDeputados(nome = autorNome, itens = 1)
                            deps.dados.firstOrNull()?.let {
                                autorPartido = it.siglaPartido
                                autorUf = it.siglaUf
                            }
                        } catch (_: Exception) {}
                    }
                } catch (_: Exception) {}

                // Translate using templates
                val translated = LawTranslator.translate(
                    siglaTipo = prop.siglaTipo,
                    ementa = prop.ementa
                )

                val entity = AnalyzedLeiEntity(
                    plId = prop.id,
                    siglaTipo = prop.siglaTipo,
                    numero = prop.numero,
                    ano = prop.ano,
                    ementa = prop.ementa,
                    tituloPopular = translated.titulo,
                    resumo = translated.impacto,
                    comoTeAfeta = translated.impacto,
                    nivelTrouxa = translated.nivelEstimado,
                    justificativaNivel = translated.tipoExplicacao,
                    acaoCidadao = "Acompanhe a votacao e cobre seu deputado.",
                    autorNome = autorNome,
                    autorPartido = autorPartido,
                    autorUf = autorUf,
                    status = "analyzed",
                    analyzedAt = System.currentTimeMillis(),
                    fonte = "template"
                )
                leiDao.insert(entity)
            }
        } catch (_: Exception) {
            return Result.retry()
        } finally {
            db.close()
        }

        return Result.success()
    }
}
