package com.trouxaapp.app

import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.data.model.ProductCategory
import com.trouxaapp.app.data.repository.TrouxaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepositoryTest {

    private lateinit var repository: TrouxaRepository

    @Before
    fun setup() {
        repository = TrouxaRepository()
    }

    @Test
    fun `getPresidentForYear returns correct president`() {
        assertEquals("Michel Temer", repository.getPresidentForYear(2017)?.name)
        assertEquals("Jair Bolsonaro", repository.getPresidentForYear(2021)?.name)
        assertEquals("Luiz Inácio Lula da Silva", repository.getPresidentForYear(2024)?.name)
    }

    @Test
    fun `getProductsByCategory returns correct products`() {
        val combustivel = repository.getProductsByCategory(ProductCategory.COMBUSTIVEL)
        assertEquals(2, combustivel.size)
        assertTrue(combustivel.all { it.category == ProductCategory.COMBUSTIVEL })
    }

    @Test
    fun `calculateSalaryComparison returns valid data`() {
        val comparison = repository.calculateSalaryComparison(3000.0)
        assertTrue(comparison.basketsToday > 0)
        assertTrue(comparison.basketsIn2020 > comparison.basketsToday)
        assertTrue(comparison.dieeseSalary > comparison.minimumWageToday)
    }

    @Test
    fun `cidadao actions exist`() {
        assertTrue(DataProvider.cidadaoActions.isNotEmpty())
        assertTrue(DataProvider.cidadaoActions.all { it.url.startsWith("http") })
    }

    @Test
    fun `context banner returns null for normal years`() {
        assertNull(DataProvider.getContextBanner(2019))
        assertNotNull(DataProvider.getContextBanner(2020))
        assertNotNull(DataProvider.getContextBanner(2022))
    }
}
