package com.trouxaapp.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Banco Central do Brasil - SGS (Sistema Gerenciador de Séries Temporais)
 * URL: https://api.bcb.gov.br/dados/serie/bcdata.sgs.{codigo}/dados?formato=json
 *
 * Useful series codes:
 * 433  - IPCA monthly
 * 4390 - Selic target rate
 * 1    - USD/BRL exchange rate
 * 7478 - Inflation expectations 12 months
 *
 * JSON. No API key needed. Free.
 */
interface BcbApiService {

    @GET("dados/serie/bcdata.sgs.{codigo}/dados/ultimos/{n}")
    suspend fun getSerieUltimos(
        @Path("codigo") codigo: Int,
        @Path("n") n: Int = 1,
        @Query("formato") formato: String = "json"
    ): List<BcbDataPoint>

    companion object {
        const val BASE_URL = "https://api.bcb.gov.br/"
        const val IPCA = 433
        const val SELIC = 432  // Meta COPOM (não 4390 que é diária)
        const val DOLAR = 1
    }
}

data class BcbDataPoint(
    val data: String = "",
    val valor: String = ""
)
