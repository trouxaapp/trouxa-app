package com.trouxaapp.app

import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.data.model.ProductCategory
import com.trouxaapp.app.data.repository.TrouxaRepository
import com.trouxaapp.app.ui.screens.home.HomeViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {

    private lateinit var repository: TrouxaRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        repository = TrouxaRepository()
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun `initial state has start year selected`() {
        assertEquals(DataProvider.START_YEAR, viewModel.uiState.value.selectedYear)
    }

    @Test
    fun `initial state shows all products`() {
        assertTrue(viewModel.uiState.value.priceComparisons.isNotEmpty())
        assertEquals(DataProvider.products.size, viewModel.uiState.value.priceComparisons.size)
    }

    @Test
    fun `changing year updates president`() {
        viewModel.onYearChanged(2020)
        assertEquals("Jair Bolsonaro", viewModel.uiState.value.president?.name)
    }

    @Test
    fun `selecting category filters products`() {
        viewModel.onCategorySelected(ProductCategory.COMBUSTIVEL)
        assertTrue(viewModel.uiState.value.priceComparisons.all {
            it.product.category == ProductCategory.COMBUSTIVEL
        })
    }

    @Test
    fun `context banner shows for pandemic years`() {
        viewModel.onYearChanged(2020)
        assertNotNull(viewModel.uiState.value.contextBanner)
        assertTrue(viewModel.uiState.value.contextBanner!!.contains("pandemia"))
    }

    @Test
    fun `context banner shows for 2022 war`() {
        viewModel.onYearChanged(2022)
        assertNotNull(viewModel.uiState.value.contextBanner)
        assertTrue(viewModel.uiState.value.contextBanner!!.contains("Guerra"))
    }

    @Test
    fun `no context banner for normal years`() {
        viewModel.onYearChanged(2019)
        assertNull(viewModel.uiState.value.contextBanner)
    }

    @Test
    fun `rotating phrase is set`() {
        assertTrue(viewModel.uiState.value.rotatingPhrase.isNotEmpty())
    }

    @Test
    fun `provocative phrases work for different variations`() {
        assertEquals("Dobrou. E seu salário?", DataProvider.getProvocativePhrase(150.0))
        assertEquals("Caiu. Dessa vez.", DataProvider.getProvocativePhrase(-5.0))
    }

    @Test
    fun `presidents have absurdos`() {
        assertTrue(DataProvider.presidents.all { it.absurdos.isNotEmpty() })
    }
}
