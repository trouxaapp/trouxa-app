package com.trouxaapp.app.ui.screens.home

import androidx.lifecycle.ViewModel
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.data.model.President
import com.trouxaapp.app.data.model.PriceComparison
import com.trouxaapp.app.data.model.ProductCategory
import com.trouxaapp.app.data.repository.TrouxaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class HomeUiState(
    val selectedYear: Int = DataProvider.CURRENT_YEAR,
    val selectedMonth: Int? = null, // null = annual view, 1-12 = month
    val showMonthSelector: Boolean = false,
    val hasMonthlyData: Boolean = false,
    val president: President? = null,
    val selectedCategory: ProductCategory? = null,
    val priceComparisons: List<PriceComparison> = emptyList(),
    val contextBanner: String? = null,
    val rotatingPhrase: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TrouxaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            rotatingPhrase = DataProvider.provocativePhrases.random()
        )
        updateForYear(DataProvider.START_YEAR)
    }

    fun onYearChanged(year: Int) {
        _uiState.value = _uiState.value.copy(
            selectedMonth = null,
            showMonthSelector = false
        )
        updateForYear(year)
    }

    fun onCategorySelected(category: ProductCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        updateForYear(_uiState.value.selectedYear)
    }

    fun toggleMonthSelector() {
        val current = _uiState.value
        if (current.hasMonthlyData) {
            _uiState.value = current.copy(
                showMonthSelector = !current.showMonthSelector,
                selectedMonth = if (!current.showMonthSelector) null else current.selectedMonth
            )
        }
    }

    fun onMonthSelected(month: Int?) {
        _uiState.value = _uiState.value.copy(selectedMonth = month)
        // Prices stay the same at annual level; month just provides extra detail
    }

    private fun updateForYear(year: Int) {
        val president = repository.getPresidentForYear(year)
        val category = _uiState.value.selectedCategory
        val comparisons = repository.getPriceComparisons(year, category)

        _uiState.value = _uiState.value.copy(
            selectedYear = year,
            president = president,
            priceComparisons = comparisons,
            contextBanner = DataProvider.getContextBanner(year),
            hasMonthlyData = DataProvider.hasMonthlyData(year)
        )
    }
}
