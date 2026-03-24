package com.trouxaapp.app.ui.screens.deputado

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.trouxaapp.app.data.remote.CamaraApiService
import com.trouxaapp.app.data.remote.DespesaApi
import com.trouxaapp.app.data.remote.DeputadoResumo
import com.trouxaapp.app.data.remote.EventoApi
import com.trouxaapp.app.util.EngagementManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeputadoListState(
    val uf: String = "",
    val deputados: List<DeputadoResumo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val needsUf: Boolean = true
)

data class DeputadoProfileState(
    val deputado: DeputadoResumo? = null,
    val totalGasto: Double = 0.0,
    val despesasPorTipo: Map<String, Double> = emptyMap(),
    val topDespesas: List<DespesaApi> = emptyList(),
    val totalEventos: Int = 0,
    val eventosPresente: Int = 0,
    val presencaPercent: Int = 0,
    val votosRecentes: List<VotoInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val telefone: String = "",
    val email: String = ""
)

data class VotoInfo(
    val descricao: String,
    val voto: String
)

@HiltViewModel
class DeputadoViewModel @Inject constructor(
    application: Application,
    private val camaraApi: CamaraApiService
) : AndroidViewModel(application) {

    private val engagement = EngagementManager(application)

    private val _listState = MutableStateFlow(DeputadoListState())
    val listState: StateFlow<DeputadoListState> = _listState.asStateFlow()

    private val _profileState = MutableStateFlow(DeputadoProfileState())
    val profileState: StateFlow<DeputadoProfileState> = _profileState.asStateFlow()

    init {
        val savedUf = engagement.userUf
        if (savedUf.isNotEmpty()) {
            _listState.value = DeputadoListState(uf = savedUf, needsUf = false)
            loadDeputados(savedUf)
        }
    }

    fun selectUf(uf: String) {
        engagement.userUf = uf
        _listState.value = DeputadoListState(uf = uf, needsUf = false, isLoading = true)
        loadDeputados(uf)
    }

    private fun loadDeputados(uf: String) {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            try {
                val response = camaraApi.getDeputadosByUf(uf)
                _listState.value = _listState.value.copy(
                    deputados = response.dados,
                    isLoading = false
                )
            } catch (e: Exception) {
                _listState.value = _listState.value.copy(
                    error = "Sem internet. Tente novamente.",
                    isLoading = false
                )
            }
        }
    }

    fun loadProfile(deputado: DeputadoResumo) {
        _profileState.value = DeputadoProfileState(deputado = deputado, isLoading = true)
        viewModelScope.launch {
            try {
                // Load details
                val detail = camaraApi.getDeputado(deputado.id)
                val gabinete = detail.dados.ultimoStatus?.gabinete

                // Load expenses
                val despesas = try {
                    camaraApi.getDespesas(deputado.id)
                } catch (_: Exception) { null }

                val totalGasto = despesas?.dados?.sumOf { it.valorLiquido } ?: 0.0
                val porTipo = despesas?.dados?.groupBy { it.tipoDespesa }
                    ?.mapValues { (_, list) -> list.sumOf { it.valorLiquido } }
                    ?.toList()
                    ?.sortedByDescending { it.second }
                    ?.take(5)
                    ?.toMap() ?: emptyMap()

                // Load events (attendance)
                val eventos = try {
                    camaraApi.getEventos(deputado.id)
                } catch (_: Exception) { null }

                val totalEventos = eventos?.dados?.size ?: 0
                val presentes = eventos?.dados?.count {
                    it.situacao.contains("Realizado", ignoreCase = true) ||
                    it.situacao.contains("Encerrad", ignoreCase = true)
                } ?: 0
                val presPercent = if (totalEventos > 0) (presentes * 100) / totalEventos else 0

                // Load recent votes
                val votosInfo = mutableListOf<VotoInfo>()
                try {
                    val votacoes = camaraApi.getVotacoes()
                    for (votacao in votacoes.dados.take(5)) {
                        try {
                            val votos = camaraApi.getVotos(votacao.id)
                            val meuVoto = votos.dados.find { it.deputado_?.id == deputado.id }
                            if (meuVoto != null) {
                                votosInfo.add(VotoInfo(
                                    descricao = votacao.descricao.take(80),
                                    voto = meuVoto.tipoVoto
                                ))
                            }
                        } catch (_: Exception) { }
                    }
                } catch (_: Exception) { }

                _profileState.value = _profileState.value.copy(
                    totalGasto = totalGasto,
                    despesasPorTipo = porTipo,
                    topDespesas = despesas?.dados?.take(10) ?: emptyList(),
                    totalEventos = totalEventos,
                    eventosPresente = presentes,
                    presencaPercent = presPercent,
                    votosRecentes = votosInfo,
                    telefone = gabinete?.telefone ?: "",
                    email = detail.dados.ultimoStatus?.email ?: "",
                    isLoading = false
                )
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    error = "Erro ao carregar perfil. Tente novamente.",
                    isLoading = false
                )
            }
        }
    }
}
