package com.trouxaapp.app.ui.screens.leis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trouxaapp.app.data.local.AnalyzedLeiEntity
import com.trouxaapp.app.data.remote.CamaraApiService
import com.trouxaapp.app.data.remote.ProposicaoApi
import com.trouxaapp.app.data.repository.GovDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LeisOrdem(val label: String) {
    RECENTES("Mais recentes"),
    GRAVES("Mais graves"),
}

data class LeisUiState(
    val analyzedLeis: List<AnalyzedLeiEntity> = emptyList(),
    val rawLeis: List<ProposicaoApi> = emptyList(),
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val error: String? = null,
    val ordem: LeisOrdem = LeisOrdem.RECENTES
)

@HiltViewModel
class LeisViewModel @Inject constructor(
    private val govDataRepository: GovDataRepository,
    private val camaraApi: CamaraApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeisUiState())
    val uiState: StateFlow<LeisUiState> = _uiState.asStateFlow()

    init {
        loadAnalyzed()
        loadRawFallback()
        triggerSync()
    }

    fun onOrdemChanged(ordem: LeisOrdem) {
        _uiState.value = _uiState.value.copy(ordem = ordem)
        loadAnalyzed()
    }

    fun retry() {
        loadRawFallback()
        triggerSync()
    }

    private fun loadAnalyzed() {
        viewModelScope.launch {
            val flow = when (_uiState.value.ordem) {
                LeisOrdem.RECENTES -> govDataRepository.getAnalyzedLeis()
                LeisOrdem.GRAVES -> govDataRepository.getAnalyzedLeisByGravity()
            }
            flow.collect { leis ->
                _uiState.value = _uiState.value.copy(analyzedLeis = leis, isLoading = false)
            }
        }
    }

    private fun loadRawFallback() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = camaraApi.getProposicoes(itens = 20)
                _uiState.value = _uiState.value.copy(rawLeis = response.dados, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Sem conexão.")
            }
        }
    }

    private fun triggerSync() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            try {
                govDataRepository.syncLeis()
            } catch (_: Exception) {}
            _uiState.value = _uiState.value.copy(isSyncing = false)
        }
    }
}
