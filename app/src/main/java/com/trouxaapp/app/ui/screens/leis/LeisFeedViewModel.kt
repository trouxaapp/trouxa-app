package com.trouxaapp.app.ui.screens.leis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trouxaapp.app.data.local.LeiEntity
import com.trouxaapp.app.data.local.SyncLogDao
import com.trouxaapp.app.data.repository.LeisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OrdenacaoLeis { EM_ALTA, RECENTES }

data class LeisFeedUiState(
    val leis: List<LeiEntity> = emptyList(),
    val ordenacao: OrdenacaoLeis = OrdenacaoLeis.EM_ALTA,
    val temaFiltro: String? = null,
    val temasDisponiveis: List<String> = emptyList(),
    val isSyncing: Boolean = false,
    val erroSync: String? = null,
    val ultimaSincronizacao: Long = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LeisFeedViewModel @Inject constructor(
    private val repo: LeisRepository,
    private val syncLogDao: SyncLogDao
) : ViewModel() {

    private val ordenacao = MutableStateFlow(OrdenacaoLeis.EM_ALTA)
    private val temaFiltro = MutableStateFlow<String?>(null)
    private val isSyncing = MutableStateFlow(false)
    private val erroSync = MutableStateFlow<String?>(null)
    private val ultimaSync = MutableStateFlow(0L)

    private val leisFlow = ordenacao.flatMapLatest { ord ->
        when (ord) {
            OrdenacaoLeis.EM_ALTA -> repo.observeByTemperatura()
            OrdenacaoLeis.RECENTES -> repo.observeByRecentes()
        }
    }

    private data class SyncMeta(val syncing: Boolean, val erro: String?, val ultima: Long)

    private val syncMeta = combine(isSyncing, erroSync, ultimaSync) { syncing, erro, ultima ->
        SyncMeta(syncing, erro, ultima)
    }

    val uiState: StateFlow<LeisFeedUiState> = combine(
        leisFlow, ordenacao, temaFiltro, syncMeta
    ) { leis, ord, tema, meta ->
        LeisFeedUiState(
            leis = if (tema == null) leis else leis.filter { it.tema == tema },
            ordenacao = ord,
            temaFiltro = tema,
            temasDisponiveis = leis.map { it.tema }.distinct().sorted(),
            isSyncing = meta.syncing,
            erroSync = meta.erro,
            ultimaSincronizacao = meta.ultima
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LeisFeedUiState())

    init {
        viewModelScope.launch {
            syncLogDao.lastSuccessFor("law")?.let { ultimaSync.value = it.timestamp }
        }
    }

    fun setOrdenacao(ord: OrdenacaoLeis) { ordenacao.value = ord }

    fun setTemaFiltro(tema: String?) { temaFiltro.value = tema }

    fun forceSync() {
        if (isSyncing.value) return
        viewModelScope.launch {
            isSyncing.value = true
            erroSync.value = null
            try {
                repo.syncAll()
                ultimaSync.value = System.currentTimeMillis()
            } catch (e: Exception) {
                erroSync.value = "Não consegui falar com a Câmara agora. Mostrando o que já foi baixado."
            } finally {
                isSyncing.value = false
            }
        }
    }
}
