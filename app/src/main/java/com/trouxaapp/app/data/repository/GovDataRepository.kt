package com.trouxaapp.app.data.repository

import com.trouxaapp.app.data.local.AnalyzedLeiDao
import com.trouxaapp.app.data.local.AnalyzedLeiEntity
import com.trouxaapp.app.data.remote.CamaraApiService
import com.trouxaapp.app.util.LawTranslator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GovDataRepository @Inject constructor(
    private val camaraApi: CamaraApiService,
    private val leiDao: AnalyzedLeiDao
) {
    fun getAnalyzedLeis(): Flow<List<AnalyzedLeiEntity>> = leiDao.getAllAnalyzed()
    fun getAnalyzedLeisByGravity(): Flow<List<AnalyzedLeiEntity>> = leiDao.getAllByGravity()

    suspend fun getAnalyzedLei(id: Int): AnalyzedLeiEntity? = leiDao.getById(id)

    /** Fetch new laws from Câmara, translate via templates, save to Room */
    suspend fun syncLeis() {
        try {
            val response = camaraApi.getProposicoes(itens = 50)
            for (prop in response.dados) {
                if (leiDao.exists(prop.id) > 0) continue
                if (prop.siglaTipo !in listOf("PL", "PEC", "PLP", "MPV", "PDL")) continue

                // Fetch author
                var autorNome = ""
                var autorPartido = ""
                var autorUf = ""
                try {
                    val autores = camaraApi.getAutores(prop.id)
                    val firstAutor = autores.dados.firstOrNull()
                    autorNome = firstAutor?.nome ?: ""
                    // Try to get party info from deputy lookup
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
                    quemGanha = "",
                    quemPerde = "",
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
        } catch (_: Exception) {}
    }
}
