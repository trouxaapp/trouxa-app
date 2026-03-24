package com.trouxaapp.app.data.repository

import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.data.model.BasicBasketEntry
import com.trouxaapp.app.data.model.MinimumWageEntry
import com.trouxaapp.app.data.model.President
import com.trouxaapp.app.data.model.PriceComparison
import com.trouxaapp.app.data.model.Product
import com.trouxaapp.app.data.model.ProductCategory
import com.trouxaapp.app.data.model.SalaryComparison
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrouxaRepository @Inject constructor() {

    fun getPresidentForYear(year: Int): President? {
        return DataProvider.getPresidentForYear(year)
    }

    fun getAllPresidents(): List<President> = DataProvider.presidents

    fun getProductsByCategory(category: ProductCategory): List<Product> {
        return DataProvider.getProductsByCategory(category)
    }

    fun getAllProducts(): List<Product> = DataProvider.products

    fun getProductById(id: String): Product? = DataProvider.getProductById(id)

    fun getPriceComparisons(year: Int, category: ProductCategory?): List<PriceComparison> {
        val products = if (category != null) {
            getProductsByCategory(category)
        } else {
            getAllProducts()
        }

        return products.mapNotNull { product ->
            val selectedEntry = product.prices[year] ?: return@mapNotNull null
            val currentEntry = product.prices[DataProvider.CURRENT_YEAR] ?: return@mapNotNull null
            val variation = ((currentEntry.price - selectedEntry.price) / selectedEntry.price) * 100.0

            PriceComparison(
                product = product,
                selectedYear = year,
                selectedPrice = selectedEntry.price,
                selectedMotivo = selectedEntry.motivo,
                selectedQuemDecidiu = selectedEntry.quemDecidiu,
                currentYear = DataProvider.CURRENT_YEAR,
                currentPrice = currentEntry.price,
                variationPercent = variation
            )
        }
    }

    fun getBasicBasket(): List<BasicBasketEntry> = DataProvider.basicBasket

    fun getMinimumWage(): List<MinimumWageEntry> = DataProvider.minimumWage

    fun getBasketPriceForYear(year: Int): Double {
        return DataProvider.basicBasket.find { it.year == year }?.value ?: 0.0
    }

    fun getMinimumWageForYear(year: Int): Double {
        return DataProvider.minimumWage.find { it.year == year }?.value ?: 0.0
    }

    fun getDieeseSalaryForYear(year: Int): Double {
        return DataProvider.dieeseSalary[year] ?: 0.0
    }

    fun getContextBanner(year: Int): String? = DataProvider.getContextBanner(year)

    fun calculateSalaryComparison(userSalary: Double): SalaryComparison {
        val basketToday = getBasketPriceForYear(DataProvider.CURRENT_YEAR)
        val basketIn2020 = getBasketPriceForYear(2020)

        return SalaryComparison(
            userSalary = userSalary,
            basketsToday = if (basketToday > 0) userSalary / basketToday else 0.0,
            basketsIn2020 = if (basketIn2020 > 0) userSalary / basketIn2020 else 0.0,
            minimumWageToday = getMinimumWageForYear(DataProvider.CURRENT_YEAR),
            dieeseSalary = getDieeseSalaryForYear(DataProvider.CURRENT_YEAR)
        )
    }
}
