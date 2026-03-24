package com.trouxaapp.app.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.data.model.President
import com.trouxaapp.app.data.model.Product
import com.trouxaapp.app.data.repository.TrouxaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class DetailUiState(
    val product: Product? = null,
    val presidents: List<President> = emptyList(),
    val overallVariation: Double = 0.0
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TrouxaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        val productId = savedStateHandle.get<String>("productId") ?: ""
        loadProduct(productId)
    }

    private fun loadProduct(productId: String) {
        val product = repository.getProductById(productId) ?: return
        val firstPrice = product.prices[DataProvider.START_YEAR]?.price ?: 0.0
        val lastPrice = product.prices[DataProvider.CURRENT_YEAR]?.price ?: 0.0
        val variation = if (firstPrice > 0) ((lastPrice - firstPrice) / firstPrice) * 100.0 else 0.0

        _uiState.value = DetailUiState(
            product = product,
            presidents = repository.getAllPresidents(),
            overallVariation = variation
        )
    }
}
