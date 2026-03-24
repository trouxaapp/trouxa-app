package com.trouxaapp.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CamaraApiService {

    @GET("proposicoes")
    suspend fun getProposicoes(
        @Query("ordem") ordem: String = "DESC",
        @Query("ordenarPor") ordenarPor: String = "id",
        @Query("itens") itens: Int = 20,
        @Query("codTema") codTema: Int? = null
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
    suspend fun getDeputados(
        @Query("nome") nome: String? = null,
        @Query("itens") itens: Int = 15
    ): DeputadosResponse

    // New endpoints for "Seu Deputado" feature
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
        @Query("ano") ano: List<Int> = listOf(2025, 2026),
        @Query("itens") itens: Int = 100,
        @Query("ordem") ordem: String = "DESC",
        @Query("ordenarPor") ordenarPor: String = "dataDocumento"
    ): DespesasResponse

    @GET("deputados/{id}/eventos")
    suspend fun getEventos(
        @Path("id") id: Int,
        @Query("dataInicio") dataInicio: String = "2025-01-01",
        @Query("itens") itens: Int = 100
    ): EventosResponse

    @GET("votacoes")
    suspend fun getVotacoes(
        @Query("ordem") ordem: String = "DESC",
        @Query("ordenarPor") ordenarPor: String = "dataHoraRegistro",
        @Query("itens") itens: Int = 10
    ): VotacoesResponse

    @GET("votacoes/{id}/votos")
    suspend fun getVotos(@Path("id") id: String): VotosResponse
}

// Existing response models
data class AutoresResponse(val dados: List<AutorApi>)
data class AutorApi(
    val uri: String = "",
    val nome: String = "",
    val codTipo: Int = 0,
    val tipo: String = ""
)

data class DeputadosResponse(val dados: List<DeputadoResumo>)
data class DeputadoResumo(
    val id: Int = 0,
    val uri: String = "",
    val nome: String = "",
    val siglaPartido: String = "",
    val siglaUf: String = "",
    val urlFoto: String = "",
    val email: String = ""
)

data class DeputadoDetailResponse(val dados: DeputadoDetalhe)
data class DeputadoDetalhe(
    val id: Int = 0,
    val nomeCivil: String = "",
    val ultimoStatus: DeputadoStatus? = null
)

data class DeputadoStatus(
    val nome: String = "",
    val siglaPartido: String = "",
    val siglaUf: String = "",
    val email: String = "",
    val gabinete: GabineteDeputado? = null
)

data class GabineteDeputado(
    val nome: String = "",
    val predio: String = "",
    val sala: String = "",
    val andar: String = "",
    val telefone: String = "",
    val email: String = ""
)

data class ProposicoesResponse(val dados: List<ProposicaoApi>)
data class ProposicaoDetailResponse(val dados: ProposicaoDetalhe)
data class TramitacoesResponse(val dados: List<Tramitacao>)

data class ProposicaoApi(
    val id: Int,
    val uri: String = "",
    val siglaTipo: String = "",
    val codTipo: Int = 0,
    val numero: Int = 0,
    val ano: Int = 0,
    val ementa: String = ""
)

data class ProposicaoDetalhe(
    val id: Int = 0,
    val siglaTipo: String = "",
    val numero: Int = 0,
    val ano: Int = 0,
    val ementa: String = "",
    val ementaDetalhada: String = "",
    val statusProposicao: StatusProposicao? = null
)

data class StatusProposicao(
    val dataHora: String = "",
    val descricaoTramitacao: String = "",
    val descricaoSituacao: String = "",
    val despacho: String = ""
)

data class Tramitacao(
    val dataHora: String = "",
    val descricaoTramitacao: String = "",
    val descricaoSituacao: String = "",
    val despacho: String = ""
)

// New response models for "Seu Deputado"
data class DespesasResponse(val dados: List<DespesaApi>)
data class DespesaApi(
    val ano: Int = 0,
    val mes: Int = 0,
    val tipoDespesa: String = "",
    val valorDocumento: Double = 0.0,
    val valorLiquido: Double = 0.0,
    val dataDocumento: String = "",
    val nomeFornecedor: String = "",
    val cnpjCpfFornecedor: String = ""
)

data class EventosResponse(val dados: List<EventoApi>)
data class EventoApi(
    val id: Int = 0,
    val dataHoraInicio: String = "",
    val dataHoraFim: String? = null,
    val situacao: String = "",
    val descricaoTipo: String = "",
    val descricao: String = ""
)

data class VotacoesResponse(val dados: List<VotacaoApi>)
data class VotacaoApi(
    val id: String = "",
    val data: String = "",
    val dataHoraRegistro: String = "",
    val siglaOrgao: String = "",
    val descricao: String = "",
    val aprovacao: Int? = null
)

data class VotosResponse(val dados: List<VotoApi>)
data class VotoApi(
    val tipoVoto: String = "",
    val deputado_: VotoDeputado? = null
)

data class VotoDeputado(
    val id: Int = 0,
    val nome: String = "",
    val siglaPartido: String = "",
    val siglaUf: String = ""
)
