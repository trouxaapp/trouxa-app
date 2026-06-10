package com.trouxaapp.app.ui.screens.leis

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trouxaapp.app.data.local.LeiEntity
import com.trouxaapp.app.data.repository.LeisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeiDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: LeisRepository
) : ViewModel() {

    private val plId: Int = savedStateHandle.get<String>("plId")?.toIntOrNull() ?: 0

    val lei: StateFlow<LeiEntity?> = repo.observeById(plId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _refreshing = MutableStateFlow(false)
    val refreshing: StateFlow<Boolean> = _refreshing

    init {
        // Refresh this bill's tramitacao on open so the status shown is
        // genuinely current, with the fetch timestamp stored alongside.
        viewModelScope.launch {
            _refreshing.value = true
            try { repo.refreshTramitacaoFor(plId) } catch (_: Exception) {}
            _refreshing.value = false
        }
    }
}
