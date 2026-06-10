package com.trouxaapp.app.data.repository

import com.trouxaapp.app.data.local.IndicadorDao
import com.trouxaapp.app.data.local.IndicadorEntity
import com.trouxaapp.app.data.remote.BcbApiService
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Economic indicators from the Banco Central SGS API.
 *
 * No hardcoded fallbacks, ever. The old version invented dolar 5.68 /
 * Selic 14.25 when the API failed and persisted it as live data, which
 * then leaked into push notifications. Here the contract is simple:
 * a value either comes from the BCB with its official reference date,
 * or it does not exist and the UI says so.
 */
@Singleton
class IndicadorRepository @Inject constructor(
    private val bcbApi: BcbApiService,
    private val indicadorDao: IndicadorDao
) {

    companion object {
        // Older than this and the value must be flagged as stale in the UI
        val STALE_AFTER_MS = TimeUnit.HOURS.toMillis(36)

        val SERIES = listOf(
            BcbApiService.DOLAR to "Dólar (PTAX venda)",
            BcbApiService.SELIC to "Selic (meta Copom)",
            BcbApiService.IPCA_12M to "IPCA acumulado 12 meses",
            BcbApiService.SALARIO_MINIMO to "Salário mínimo"
        )
    }

    fun observeAll(): Flow<List<IndicadorEntity>> = indicadorDao.observeAll()

    suspend fun getBySerie(serie: Int): IndicadorEntity? = indicadorDao.getBySerie(serie)

    fun isStale(ind: IndicadorEntity, agora: Long = System.currentTimeMillis()): Boolean =
        agora - ind.atualizadoEm > STALE_AFTER_MS

    /**
     * Fetches all series from the BCB and persists only values that parsed
     * cleanly WITH their reference date. Returns how many were updated.
     * A series that fails simply keeps its previous row (with its honest
     * old date) or stays absent.
     */
    suspend fun refresh(): Int {
        var updated = 0
        for ((serie, nome) in SERIES) {
            try {
                val ponto = bcbApi.getSerieUltimos(serie).firstOrNull() ?: continue
                val valor = ponto.valor.replace(",", ".").toDoubleOrNull() ?: continue
                if (ponto.data.isBlank()) continue
                indicadorDao.insert(
                    IndicadorEntity(
                        serie = serie,
                        nome = nome,
                        valor = valor,
                        data = ponto.data,
                        atualizadoEm = System.currentTimeMillis()
                    )
                )
                updated++
            } catch (_: Exception) {
                // skip this series; the worker reports partial updates via count
            }
        }
        return updated
    }
}
