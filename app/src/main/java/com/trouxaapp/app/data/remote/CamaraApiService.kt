package com.trouxaapp.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Camara dos Deputados open data API v2.
 * https://dadosabertos.camara.leg.br/api/v2/
 *
 * IMPORTANT (Gson + Kotlin trap): Gson instantiates these DTOs via
 * reflection and IGNORES Kotlin default values. A field missing from the
 * JSON becomes null at runtime even when the type is non-null. So every
 * String field below is declared String? and call sites null-coalesce.
 * Numeric primitives are safe (Gson leaves them at 0).
 */
interface CamaraApiService {

    @GET("proposicoes")
    suspend fun getProposicoes(
        @Query("ordem") ordem: String = "DESC",
        @Query("ordenarPor") ordenarPor: String = "id",
        @Query("itens") itens: Int = 20,
        @Query("pagina") pagina: Int = 1,
        @Query("dataApresentacaoInicio") dataApresentacaoInicio: String? = null,
        @Query("dataApresentacaoFim") dataApresentacaoFim: String? = null,
        @Query("siglaTipo") siglaTipo: String? = null
    ): ProposicoesResponse

    @GET("proposicoes/{id}")
    suspend fun getProposicao(@Path("id") id: Int): ProposicaoDetailResponse

    @GET("proposicoes/{id}/tramitacoes")
    suspend fun getTramitacoes(@Path("id") id: Int): TramitacoesResponse

    @GET("proposicoes/{id}/autores")
    suspend fun getAutores(@Path("id") id: Int): AutoresResponse

    @GET("deputados/{id}")
    suspend fun getDeputado(@Path("id") id: Int): DeputadoDetailResponse

    @GET("deputados")
    suspend fun getDeputadosByUf(
        @Query("siglaUf") siglaUf: String,
        @Query("ordem") ordem: String = "ASC",
        @Query("ordenarPor") ordenarPor: String = "nome",
        @Query("itens") itens: Int = 100
    ): DeputadosResponse

    @GET("deputados/{id}/despesas")
    suspend fun getDespesas(
        @Path("id") id: Int,
        @Query("ano") ano: List<Int>,
        @Query("itens") itens: Int = 100,
        @Query("ordem") ordem: String = "DESC",
        @Query("ordenarPor") ordenarPor: String = "dataDocumento"
    ): DespesasResponse
}

// ---- proposicoes ----

data class ProposicoesResponse(val dados: List<ProposicaoApi>? = null)

data class ProposicaoApi(
    val id: Int = 0,
    val uri: String? = null,
    val siglaTipo: String? = null,
    val codTipo: Int = 0,
    val numero: Int = 0,
    val ano: Int = 0,
    val ementa: String? = null
)

data class ProposicaoDetailResponse(val dados: ProposicaoDetalhe? = null)

data class ProposicaoDetalhe(
    val id: Int = 0,
    val siglaTipo: String? = null,
    val numero: Int = 0,
    val ano: Int = 0,
    val ementa: String? = null,
    val ementaDetalhada: String? = null,
    val keywords: String? = null,
    val dataApresentacao: String? = null,
    val urlInteiroTeor: String? = null,
    val justificativa: String? = null,
    val statusProposicao: StatusProposicao? = null
)

data class StatusProposicao(
    val dataHora: String? = null,
    val descricaoTramitacao: String? = null,
    val descricaoSituacao: String? = null,
    val despacho: String? = null
)

data class TramitacoesResponse(val dados: List<Tramitacao>? = null)

data class Tramitacao(
    val dataHora: String? = null,
    val descricaoTramitacao: String? = null,
    val descricaoSituacao: String? = null,
    val despacho: String? = null
)

// ---- autores ----

data class AutoresResponse(val dados: List<AutorApi>? = null)

data class AutorApi(
    // uri points at /deputados/{id} when the author is a deputy; we parse
    // the id from it and resolve the deputy directly. Never resolve authors
    // by free-text name search: homonyms attach the wrong person.
    val uri: String? = null,
    val nome: String? = null,
    val codTipo: Int = 0,
    val tipo: String? = null
)

// ---- deputados ----

data class DeputadosResponse(val dados: List<DeputadoResumo>? = null)

data class DeputadoResumo(
    val id: Int = 0,
    val uri: String? = null,
    val nome: String? = null,
    val siglaPartido: String? = null,
    val siglaUf: String? = null,
    val urlFoto: String? = null,
    val email: String? = null
)

data class DeputadoDetailResponse(val dados: DeputadoDetalhe? = null)

data class DeputadoDetalhe(
    val id: Int = 0,
    val nomeCivil: String? = null,
    val ultimoStatus: DeputadoStatus? = null
)

data class DeputadoStatus(
    val nome: String? = null,
    val siglaPartido: String? = null,
    val siglaUf: String? = null,
    val urlFoto: String? = null,
    val email: String? = null,
    val gabinete: GabineteDeputado? = null
)

data class GabineteDeputado(
    val nome: String? = null,
    val predio: String? = null,
    val sala: String? = null,
    val andar: String? = null,
    val telefone: String? = null,
    val email: String? = null
)

// ---- despesas ----

data class DespesasResponse(val dados: List<DespesaApi>? = null)

data class DespesaApi(
    val ano: Int = 0,
    val mes: Int = 0,
    val tipoDespesa: String? = null,
    val valorDocumento: Double = 0.0,
    val valorLiquido: Double = 0.0,
    val dataDocumento: String? = null,
    val nomeFornecedor: String? = null,
    val cnpjCpfFornecedor: String? = null
)
