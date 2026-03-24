package com.trouxaapp.app.data.model

data class President(
    val name: String,
    val party: String,
    val startYear: Int,
    val endYear: Int?,
    val photoUrl: String,
    val drawableRes: String,
    val initials: String,
    val facts: PresidentFacts,
    val absurdos: List<Absurdo>,
    val gabinete: Gabinete = Gabinete()
)

data class Gabinete(
    val vice: String = "",
    val ministroEconomia: String = "",
    val presBCB: String = "",
    val presPetrobras: String = "",
    val decisoesPrecos: List<String> = emptyList()
)

data class PresidentFacts(
    val positivo: String,
    val negativo: String,
    val contexto: String,
    val pontoCritico: String? = null,
    val fonte: String
)

data class Absurdo(
    val texto: String,
    val fonte: String,
    val url: String = ""
)

enum class ProductCategory(val label: String) {
    ALIMENTOS("Alimentos"),
    COMBUSTIVEL("Combustível"),
    ENERGIA("Energia"),
    SERVICOS("Serviços")
}

data class PriceEntry(
    val price: Double,
    val motivo: String,
    val quemDecidiu: String = "",
    val monthly: Map<Int, Double>? = null // month (1-12) -> price, null = no monthly data
)

data class Product(
    val id: String,
    val name: String,
    val unit: String,
    val category: ProductCategory,
    val source: String,
    val sourceUrl: String,
    val prices: Map<Int, PriceEntry>,
    val fraseCustom: String = "",
    val contextoCompleto: String = "",
    val hipocrisia: String = ""
)

data class PriceComparison(
    val product: Product,
    val selectedYear: Int,
    val selectedPrice: Double,
    val selectedMotivo: String,
    val selectedQuemDecidiu: String,
    val currentYear: Int,
    val currentPrice: Double,
    val variationPercent: Double
)

data class SalaryComparison(
    val userSalary: Double,
    val basketsToday: Double,
    val basketsIn2020: Double,
    val minimumWageToday: Double,
    val dieeseSalary: Double
)

data class MinimumWageEntry(
    val year: Int,
    val value: Double,
    val motivo: String = ""
)

data class BasicBasketEntry(
    val year: Int,
    val value: Double,
    val motivo: String = ""
)

data class LeiAprovada(
    val id: String,
    val nome: String,
    val nomePopular: String,
    val ano: Int,
    val oQueE: String,
    val quemFez: List<String>,
    val oQueMudou: String,
    val aindaEstaLa: List<String>,
    val nivelTrouxa: Int,
    val fonte: String
)

data class Governante(
    val nome: String,
    val cargo: String,
    val partido: String,
    val custoAnual: String,
    val twitter: String = "",
    val email: String = ""
)

data class PoliticalProfile(
    val id: String,
    val nome: String,
    val cargo: String,
    val partido: String = "",
    val estado: String = "",
    val mandatos: List<String> = emptyList(),
    val decisoes: List<String> = emptyList(),
    val custos: String = "",
    val escandalos: List<String> = emptyList(),
    val twitter: String = "",
    val email: String = ""
)

data class CidadaoAction(
    val title: String,
    val description: String,
    val realidade: String,
    val url: String,
    val frase: String,
    val nota: Int = 5
)

// === "Hoje" Feed Models ===

data class ActionItem(
    val texto: String,
    val destino: String, // NavRoute
    val parametro: String = ""
)

data class DataPoint(
    val dado: String,
    val fonte: String,
    val url: String = ""
)

data class ImpactCard(
    val dayOfYear: Int,
    val text: String,
    val source: String,
    val id: Int = 0,
    val explicacaoCompleta: String = "",
    val presidentesRelacionados: List<String> = emptyList(),
    val produtosRelacionados: List<String> = emptyList(),
    val oQueFazer: List<ActionItem> = emptyList(),
    val fontesUrls: Map<String, String> = emptyMap(),
    val categoriaImpacto: String = "bolso"
)

data class DidYouKnow(
    val textoResumo: String,
    val source: String = "",
    val id: Int = 0,
    val weekOfYear: Int = 0,
    val textoCompleto: String = "",
    val dadosComprobatorios: List<DataPoint> = emptyList(),
    val porQueNinguémFala: String = "",
    val oQueFazer: List<ActionItem> = emptyList(),
    val fontes: Map<String, String> = emptyMap(),
    val categoria: String = "governo"
) {
    val text: String get() = textoResumo
}

typealias VoceSabia = DidYouKnow

data class LeiResumo(
    val tag: String,
    val tituloPopular: String,
    val resumo: String,
    val autor: String,
    val detalhes: String = ""
)

data class GastoPublico(
    val texto: String,
    val complemento: String,
    val source: String,
    val id: Int = 0,
    val contextoExpandido: String = "",
    val comparacoes: List<String> = emptyList(),
    val fontesUrls: Map<String, String> = emptyMap()
)

data class StreakData(
    val days: Int,
    val message: String,
    val isReturning: Boolean = false
)

// === Entenda Expanded Models ===

data class ConceitoCompleto(
    val id: String,
    val titulo: String,
    val explicacao: String,
    val naPratica: String = "",
    val quemDecide: String = "",
    val exemplo: String = "",
    val termosRelacionados: List<String> = emptyList(),
    val fonte: String = ""
)
