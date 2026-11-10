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
import com.trouxaapp.app.util.Alma
import com.trouxaapp.app.util.UserPrefs
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
    val isRefreshing: Boolean = false,
    // Ofensiva: dias seguidos abrindo o app. Fica só no aparelho.
    val ofensiva: Int = 0,
    val verdadesVistas: Int = 0,
    // Frase de processo do dia, sem número e sem fonte: piada, não fato novo
    val esculacho: String = ""
)

@HiltViewModel
class HojeViewModel @Inject constructor(
    private val indicadorRepo: IndicadorRepository,
    private val leisRepo: LeisRepository,
    private val painelRepo: PainelRepository,
    private val userPrefs: UserPrefs,
    feedDao: FeedDao
) : ViewModel() {

    private val projetosHoje = MutableStateFlow(-1)
    private val refreshing = MutableStateFlow(false)
    private val ofensiva = MutableStateFlow(0)
    private val verdadesVistas = MutableStateFlow(0)

    val uiState: StateFlow<HojeUiState> = combine(
        indicadorRepo.observeAll(),
        feedDao.observeAtivos(),
        projetosHoje,
        refreshing,
        painelRepo.painel,
        ofensiva,
        verdadesVistas
    ) { valores ->
        @Suppress("UNCHECKED_CAST")
        val inds = valores[0] as List<IndicadorEntity>
        val feed = valores[1] as List<FeedItemEntity>
        val novos = valores[2] as Int
        val refr = valores[3] as Boolean
        val painel = valores[4] as Painel?
        HojeUiState(
            indicadores = inds.map { it.toUi() },
            // Every row of a sync shares one timestamp, so order by the feed's
            // own ids instead: highlights first, then "did you know" items.
            destaques = feed.sortedWith(
                compareBy<FeedItemEntity>({ it.tipo != "destaque" }, { it.id.substringAfterLast('_').toIntOrNull() ?: 0 })
            ),
            projetosNovosHoje = novos,
            painel = painel,
            isRefreshing = refr,
            ofensiva = valores[5] as Int,
            verdadesVistas = valores[6] as Int,
            esculacho = Alma.esculacho()
        )
    // Seed with the cached panel so the first frame is not the "no data" state while Room loads
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HojeUiState(painel = painelRepo.painel.value))

    init {
        contarProjetosHoje()
        // Abrir o app já conta como encarar a verdade
        ofensiva.value = userPrefs.registrarVisita()
        verdadesVistas.value = userPrefs.verdadesVistas
        // Market and Congress move during the day; older than this, refetch on open
        val painel = painelRepo.painel.value
        if (painel == null || System.currentTimeMillis() - painel.atualizadoEm > 20 * 60_000L) refresh()
    }

    /** Chamado quando o usuário lê uma verdade até o fim (abre o card inteiro). */
    fun marcarVerdadeLida() {
        userPrefs.verdadesVistas = userPrefs.verdadesVistas + 1
        verdadesVistas.value = userPrefs.verdadesVistas
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
