package com.trouxaapp.app.ui.screens.comparativo

import androidx.lifecycle.ViewModel
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.data.model.BasicBasketEntry
import com.trouxaapp.app.data.model.MinimumWageEntry
import com.trouxaapp.app.data.model.President
import com.trouxaapp.app.data.model.Product
import com.trouxaapp.app.data.model.SalaryComparison
import com.trouxaapp.app.data.repository.TrouxaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class GovComparison(
    val productName: String,
    val priceA: Double,
    val priceB: Double,
    val variation: Double
)

data class ComparativoUiState(
    val yearA: Int = 2002,
    val yearB: Int = DataProvider.CURRENT_YEAR,
    val presidentA: President? = null,
    val presidentB: President? = null,
    val comparisons: List<GovComparison> = emptyList(),
    val allPresidentsBetween: String = "",
    val userSalary: String = "",
    val salaryComparison: SalaryComparison? = null,
    val basicBasket: List<BasicBasketEntry> = emptyList(),
    val minimumWage: List<MinimumWageEntry> = emptyList()
)

@HiltViewModel
class ComparativoViewModel @Inject constructor(
    private val repository: TrouxaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComparativoUiState())
    val uiState: StateFlow<ComparativoUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            basicBasket = repository.getBasicBasket(),
            minimumWage = repository.getMinimumWage()
        )
        updateComparison()
    }

    fun onYearAChanged(year: Int) {
        _uiState.value = _uiState.value.copy(yearA = year)
        updateComparison()
    }

    fun onYearBChanged(year: Int) {
        _uiState.value = _uiState.value.copy(yearB = year)
        updateComparison()
    }

    fun onSalaryChanged(salary: String) {
        _uiState.value = _uiState.value.copy(userSalary = salary)
        val value = salary.replace(",", ".").toDoubleOrNull()
        _uiState.value = _uiState.value.copy(
            salaryComparison = if (value != null && value > 0) repository.calculateSalaryComparison(value) else null
        )
    }

    private fun updateComparison() {
        val a = _uiState.value.yearA
        val b = _uiState.value.yearB
        val presA = repository.getPresidentForYear(a)
        val presB = repository.getPresidentForYear(b)

        val comparisons = DataProvider.products.mapNotNull { product ->
            val priceA = product.prices[a]?.price ?: return@mapNotNull null
            val priceB = product.prices[b]?.price ?: return@mapNotNull null
            val variation = if (priceA > 0) ((priceB - priceA) / priceA) * 100.0 else 0.0
            GovComparison(product.name, priceA, priceB, variation)
        }

        // Collect all presidents between yearA and yearB
        val minY = minOf(a, b)
        val maxY = maxOf(a, b)
        val allPres = DataProvider.presidents
            .filter { it.startYear <= maxY && (it.endYear ?: 9999) >= minY }
            .map { "${it.name} (${it.party})" }
            .distinct()

        _uiState.value = _uiState.value.copy(
            presidentA = presA,
            presidentB = presB,
            comparisons = comparisons,
            allPresidentsBetween = allPres.joinToString(", ")
        )
    }
}
