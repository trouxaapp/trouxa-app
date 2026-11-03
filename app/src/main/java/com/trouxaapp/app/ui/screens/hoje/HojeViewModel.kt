package com.trouxaapp.app.ui.screens.hoje

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trouxaapp.app.data.local.FeedDao
import com.trouxaapp.app.data.local.FeedItemEntity
import com.trouxaapp.app.data.local.IndicadorEntity
import com.trouxaapp.app.data.painel.Painel
import com.trouxaapp.app.data.painel.PainelRepository
import com.trouxaapp.app.data.repository.IndicadorRepository
import com.trouxaapp.app.data.repository.LeisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class IndicadorUi(
    val serie: Int,
    val nome: String,
    val valor: Double,
    val dataReferencia: String,
    val atualizadoEm: Long,
    val stale: Boolean
)

data class HojeUiState(
    val indicadores: List<IndicadorUi> = emptyList(),
    val destaques: List<FeedItemEntity> = emptyList(),
    val projetosNovosHoje: Int = -1, // -1 = unknown (cache not built yet)
    val painel: Painel? = null,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class HojeViewModel @Inject constructor(
    private val indicadorRepo: IndicadorRepository,
    private val leisRepo: LeisRepository,
    private val painelRepo: PainelRepository,
    feedDao: FeedDao
) : ViewModel() {

    private val projetosHoje = MutableStateFlow(-1)
    private val refreshing = MutableStateFlow(false)

    val uiState: StateFlow<HojeUiState> = combine(
        indicadorRepo.observeAll(),
        feedDao.observeAtivos(),
        projetosHoje,
        refreshing,
        painelRepo.painel
    ) { inds, feed, novos, refr, painel ->
        HojeUiState(
            indicadores = inds.map { it.toUi() },
            // Every row of a sync shares one timestamp, so order by the feed's
            // own ids instead: highlights first, then "did you know" items.
            destaques = feed.sortedWith(
                compareBy<FeedItemEntity>({ it.tipo != "destaque" }, { it.id.substringAfterLast('_').toIntOrNull() ?: 0 })
            ),
            projetosNovosHoje = novos,
            painel = painel,
            isRefreshing = refr
        )
    // Seed with the cached panel so the first frame is not the "no data" state while Room loads
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HojeUiState(painel = painelRepo.painel.value))

    init {
        contarProjetosHoje()
        // Market and Congress move during the day; older than this, refetch on open
        val painel = painelRepo.painel.value
        if (painel == null || System.currentTimeMillis() - painel.atualizadoEm > 20 * 60_000L) refresh()
    }

    private fun contarProjetosHoje() {
        viewModelScope.launch {
            val hoje = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)
            projetosHoje.value = leisRepo.countApresentadasDesde(hoje)
        }
    }

    fun refresh() {
        if (refreshing.value) return
        viewModelScope.launch {
            refreshing.value = true
            try { indicadorRepo.refresh() } catch (_: Exception) {}
            try { painelRepo.atualizar() } catch (_: Exception) {}
            contarProjetosHoje()
            refreshing.value = false
        }
    }

    private fun IndicadorEntity.toUi() = IndicadorUi(
        serie = serie,
        nome = nome,
        valor = valor,
        dataReferencia = data,
        atualizadoEm = atualizadoEm,
        stale = indicadorRepo.isStale(this)
    )
}
