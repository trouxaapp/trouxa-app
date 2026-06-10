package com.trouxaapp.app.ui.navigation

object NavRoutes {
    // Bottom nav tabs
    const val LEIS = "leis"
    const val HOJE = "hoje"
    const val DEPUTADOS = "deputados"
    const val ENTENDA = "entenda"

    // Detail / secondary screens
    const val LEI_DETAIL = "lei/{plId}"
    const val DEPUTADO_PROFILE = "deputado_profile/{deputadoId}"
    const val SETTINGS = "settings"
    const val SOBRE = "sobre"
    const val PRIVACIDADE = "privacidade"

    fun leiDetailRoute(plId: Int) = "lei/$plId"
    fun deputadoProfileRoute(deputadoId: Int) = "deputado_profile/$deputadoId"
}
