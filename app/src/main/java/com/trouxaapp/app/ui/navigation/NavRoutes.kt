package com.trouxaapp.app.ui.navigation

import android.net.Uri

object NavRoutes {
    // Main tabs
    const val HOJE = "hoje"
    const val REPRESENTANTES = "representantes"
    const val HISTORICO = "historico"
    const val ENTENDA = "entenda"

    // Legacy alias
    const val SEU_DEPUTADO = REPRESENTANTES
    const val HOME = HISTORICO

    // Detail screens
    const val IMPACT_DETAIL = "impact_detail/{impactId}"
    const val BUDGET_DETAIL = "budget_detail"
    const val FACT_DETAIL = "fact_detail/{factId}"
    const val STREAK = "streak"
    const val SETTINGS = "settings"
    const val DEPUTADO_PROFILE = "deputado_profile/{deputadoId}"
    const val DETAIL = "detail/{productId}"
    const val COMPARATIVO = "comparativo"
    const val SOBRE = "sobre"
    const val CIDADAO = "cidadao"
    const val POR_QUE_NADA_MUDA = "por_que_nada_muda"
    const val LEIS_DO_POVO = "leis_do_povo"
    const val PROFILE = "profile/{profileId}"
    const val COBRAR = "cobrar/{depId}/{depNome}/{partido}/{uf}/{contexto}"

    fun impactDetailRoute(impactId: Int) = "impact_detail/$impactId"
    fun factDetailRoute(factId: Int) = "fact_detail/$factId"
    fun deputadoProfileRoute(deputadoId: Int) = "deputado_profile/$deputadoId"
    fun detailRoute(productId: String) = "detail/$productId"
    fun profileRoute(profileId: String) = "profile/$profileId"

    fun cobrarRoute(depId: Int, depNome: String, partido: String, uf: String, contexto: String): String {
        return "cobrar/$depId/${Uri.encode(depNome)}/${Uri.encode(partido)}/${Uri.encode(uf)}/${Uri.encode(contexto)}"
    }
}
