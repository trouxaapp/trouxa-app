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
import java.util.concurrent.TimeUnit

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
        const val ASSET_FEED = "feed.json"
        private const val PREFS = "feed_sync"
        private const val KEY_APLICADO = "atualizado_aplicado"
    }

    // "atualizado" (ISO date) of the last feed written to the table, remote or
    // bundled. Lets a newer APK replace stale bundled content even while the
    // remote is unreachable, without an older bundle overriding a newer remote.
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    override suspend fun doWork(): Result {
        val agora = System.currentTimeMillis()
        // Keep the telemetry table bounded; without this it grows forever.
        syncLogDao.purgeOlderThan(agora - TimeUnit.DAYS.toMillis(7))

        // Remote is authoritative: when it answers, it wins and can retract content.
        try {
            val request = Request.Builder().url(FEED_URL).build()
            val body = okHttp.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) throw IllegalStateException("HTTP ${resp.code}")
                resp.body?.string() ?: throw IllegalStateException("empty body")
            }

            val (entities, descartados) = parseFeed(body, agora)
            // Replace unconditionally: an emptied remote feed must empty the app.
            feedDao.replaceAll(entities)
            marcarAplicado(body)
            syncLogDao.insert(
                SyncLogEntity(
                    worker = "feed",
                    timestamp = agora,
                    success = true,
                    itemsChanged = entities.size,
                    error = if (descartados > 0) "$descartados itens sem fonte descartados" else ""
                )
            )
            return Result.success()
        } catch (_: Exception) {
            // Remote unreachable (e.g. repo privado -> 404). Seed from the feed
            // bundled in the APK so the curated content still shows, but only
            // when we have nothing yet: a reachable remote must always override.
            return seedFromAsset(agora)
        }
    }

    private suspend fun seedFromAsset(agora: Long): Result {
        return try {
            val body = applicationContext.assets.open(ASSET_FEED)
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            val bundled = atualizadoDe(body)
            val aplicado = prefs.getString(KEY_APLICADO, "").orEmpty()
            val bundledMaisNovo = bundled.isNotEmpty() && bundled > aplicado
            if (feedDao.getAtivos().isNotEmpty() && !bundledMaisNovo) {
                // Content is intact, so this run is healthy. Retrying here is
                // pointless (a private repo 404s forever) and every retry used
                // to write another failure row, flooding the health panel.
                syncLogDao.insert(
                    SyncLogEntity(
                        worker = "feed",
                        timestamp = agora,
                        success = true,
                        error = "remoto indisponível; usando conteúdo local"
                    )
                )
                return Result.success()
            }
            val (entities, _) = parseFeed(body, agora)
            feedDao.replaceAll(entities)
            marcarAplicado(body)
            syncLogDao.insert(
                SyncLogEntity(
                    worker = "feed",
                    timestamp = agora,
                    success = true,
                    itemsChanged = entities.size,
                    error = "feed local (remoto indisponivel)"
                )
            )
            Result.success()
        } catch (e2: Exception) {
            syncLogDao.insert(
                SyncLogEntity(
                    worker = "feed",
                    timestamp = agora,
                    success = false,
                    error = "${e2.javaClass.simpleName}: ${e2.message ?: "unknown"}"
                )
            )
            Result.retry()
        }
    }

    private fun atualizadoDe(body: String): String = runCatching {
        Gson().fromJson(body, FeedRemoteData::class.java)?.atualizado?.trim().orEmpty()
    }.getOrDefault("")

    private fun marcarAplicado(body: String) {
        prefs.edit().putString(KEY_APLICADO, atualizadoDe(body)).apply()
    }

    /** Parses a feed body and drops every item missing fonte + fonte_url + data_referencia + texto. */
    private fun parseFeed(body: String, agora: Long): Pair<List<FeedItemEntity>, Int> {
        val feed = Gson().fromJson(body, FeedRemoteData::class.java)
            ?: throw IllegalStateException("unparseable feed")
        val itens = feed.itens ?: emptyList()
        var descartados = 0
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
                numero = item.numero?.trim().orEmpty(),
                selo = item.selo?.trim().orEmpty(),
                veredito = item.veredito?.trim().orEmpty(),
                imagemUrl = item.imagem?.url?.trim().orEmpty(),
                // Artwork without credit is dropped to empty rather than shown
                // as if we owned it
                imagemCredito = item.imagem?.credito?.trim().orEmpty(),
                ativo = item.ativo ?: true,
                atualizadoEm = agora
            )
        }
        return entities to descartados
    }
}

// Mirrors feed/feed.json schema v4 in the app repo ("numero" and the punch
// fields are optional; fonte + fonte_url + data_referencia stay mandatory)
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
    val numero: String? = null,    // optional, rendered big on cards
    val selo: String? = null,      // optional curated stamp, e.g. "FICOU MAIS CARO"
    val veredito: String? = null,  // optional curated punch line
    val imagem: FeedRemoteImagem? = null,
    val ativo: Boolean? = true
)

data class FeedRemoteImagem(
    val url: String? = null,
    val credito: String? = null    // "arte: @trouxaapp" / "foto: Câmara"
)
