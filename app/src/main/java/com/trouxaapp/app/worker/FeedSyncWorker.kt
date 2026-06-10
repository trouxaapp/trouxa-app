package com.trouxaapp.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.trouxaapp.app.data.local.FeedDao
import com.trouxaapp.app.data.local.FeedItemEntity
import com.trouxaapp.app.data.local.SyncLogDao
import com.trouxaapp.app.data.local.SyncLogEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Pulls curated content from the app repo (feed/feed.json, schema v3).
 *
 * Validation is the whole point: an item without fonte + fonte_url +
 * data_referencia is DROPPED here, so nothing sourceless can ever reach
 * the UI. An empty (or fully invalid) feed clears the table, which makes
 * retraction of bad content actually work.
 */
@HiltWorker
class FeedSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val feedDao: FeedDao,
    private val syncLogDao: SyncLogDao,
    private val okHttp: OkHttpClient
) : CoroutineWorker(context, params) {

    companion object {
        const val FEED_URL =
            "https://raw.githubusercontent.com/trouxaapp/trouxa-app/main/feed/feed.json"
    }

    override suspend fun doWork(): Result {
        try {
            val request = Request.Builder().url(FEED_URL).build()
            val body = okHttp.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) {
                    syncLogDao.insert(SyncLogEntity(worker = "feed", timestamp = System.currentTimeMillis(), success = false, error = "HTTP ${resp.code}"))
                    return Result.retry()
                }
                resp.body?.string() ?: throw IllegalStateException("empty body")
            }

            val feed = Gson().fromJson(body, FeedRemoteData::class.java)
                ?: throw IllegalStateException("unparseable feed")

            val itens = feed.itens ?: emptyList()
            var descartados = 0
            val agora = System.currentTimeMillis()
            val entities = itens.mapNotNull { item ->
                val fonte = item.fonte?.trim().orEmpty()
                val fonteUrl = item.fonteUrl?.trim().orEmpty()
                val dataRef = item.dataReferencia?.trim().orEmpty()
                val texto = item.texto?.trim().orEmpty()
                if (fonte.isEmpty() || fonteUrl.isEmpty() || dataRef.isEmpty() || texto.isEmpty()) {
                    descartados++
                    return@mapNotNull null
                }
                FeedItemEntity(
                    id = "${item.tipo ?: "destaque"}_${item.id}",
                    tipo = item.tipo ?: "destaque",
                    titulo = item.titulo?.trim().orEmpty(),
                    texto = texto,
                    fonte = fonte,
                    fonteUrl = fonteUrl,
                    dataReferencia = dataRef,
                    ativo = item.ativo ?: true,
                    atualizadoEm = agora
                )
            }

            // Replace unconditionally: an emptied remote feed must empty the app.
            feedDao.replaceAll(entities)

            val erro = if (descartados > 0) "$descartados itens sem fonte descartados" else ""
            syncLogDao.insert(
                SyncLogEntity(
                    worker = "feed",
                    timestamp = agora,
                    success = true,
                    itemsChanged = entities.size,
                    error = erro
                )
            )
            return Result.success()
        } catch (e: Exception) {
            syncLogDao.insert(
                SyncLogEntity(
                    worker = "feed",
                    timestamp = System.currentTimeMillis(),
                    success = false,
                    error = "${e.javaClass.simpleName}: ${e.message ?: "unknown"}"
                )
            )
            return Result.retry()
        }
    }
}

// Mirrors feed/feed.json schema v3 in the app repo
data class FeedRemoteData(
    val versao: Int = 0,
    val atualizado: String? = null,
    val itens: List<FeedRemoteItem>? = null
)

data class FeedRemoteItem(
    val id: Int = 0,
    val tipo: String? = null,      // "destaque" | "voce_sabia"
    val titulo: String? = null,
    val texto: String? = null,
    val fonte: String? = null,
    @SerializedName("fonte_url") val fonteUrl: String? = null,
    @SerializedName("data_referencia") val dataReferencia: String? = null,
    val ativo: Boolean? = true
)
