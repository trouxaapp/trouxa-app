package com.trouxaapp.app.data.remote

import retrofit2.http.GET

/**
 * API service for future backend migration.
 * v1: JSON file on GitHub Pages / Vercel
 * v2: FastAPI backend with cache
 * v3: Real-time APIs (IBGE SIDRA, ANP, BCB SGS)
 */
interface TrouxaApiService {

    @GET("data.json")
    suspend fun getData(): TrouxaApiData
}

data class TrouxaApiData(
    val updated_at: String,
    val products: List<ApiProduct>,
    val presidents: List<ApiPresident>
)

data class ApiProduct(
    val id: String,
    val name: String,
    val category: String,
    val source: String,
    val source_url: String,
    val prices: Map<String, Double>,
    val reasons: Map<String, String> = emptyMap(),
    val responsible: Map<String, ApiResponsible> = emptyMap()
)

data class ApiResponsible(
    val name: String,
    val role: String,
    val government: String,
    val action: String
)

data class ApiPresident(
    val name: String,
    val nickname: String,
    val party: String,
    val start_year: Int,
    val end_year: Int?,
    val photo: String,
    val economy_minister: String,
    val bc_president: String,
    val petrobras_president: String,
    val positives: List<String>,
    val negatives: List<String>,
    val scandals: List<String>,
    val approval_rating: String = ""
)
