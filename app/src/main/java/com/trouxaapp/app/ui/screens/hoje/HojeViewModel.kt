package com.trouxaapp.app.ui.screens.hoje

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.data.local.HojeDataProvider
import com.trouxaapp.app.data.model.GastoPublico
import com.trouxaapp.app.data.model.ImpactCard
import com.trouxaapp.app.data.model.LeiResumo
import com.trouxaapp.app.data.model.Product
import com.trouxaapp.app.data.model.StreakData
import com.trouxaapp.app.data.model.VoceSabia
import com.trouxaapp.app.data.repository.GovDataRepository
import com.trouxaapp.app.util.EngagementManager
import com.trouxaapp.app.worker.DataSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class HojeUiState(
    val impactCard: ImpactCard? = null,
    val leisResumo: List<LeiResumo> = emptyList(),
    val salarioMinimo: Double = 1518.0,
    val cestaBasica: Double = 860.0,
    val userSalary: Double = 0.0,
    val gastoPublico: GastoPublico? = null,
    val precoDestaque: Product? = null,
    val precoDestaqueVariation: Double = 0.0,
    val voceSabia: VoceSabia? = null,
    val streakData: StreakData = StreakData(1, "Primeiro dia. Bem-vindo à realidade."),
    val unlockMessage: String? = null,
    val dolar: Double = 5.52,
    val selic: Double = 14.25,
    val ipca: Double = 5.2,
    val indicatorsUpdate: String = "mar/2026",
    val absurdoDaSemana: String = "",
    val absurdoFonte: String = ""
)

@HiltViewModel
class HojeViewModel @Inject constructor(
    application: Application,
    private val govDataRepository: GovDataRepository
) : AndroidViewModel(application) {

    private val engagement = EngagementManager(application)
    private val _uiState = MutableStateFlow(HojeUiState())
    val uiState: StateFlow<HojeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        loadLawsFromRoom()
    }

    private fun loadData() {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        val impactCard = HojeDataProvider.getImpactCardForDay(dayOfYear)
        val gastoPublico = HojeDataProvider.getGastoPublicoRotating(dayOfYear)
        val voceSabia = HojeDataProvider.getVoceSabiaForWeek(dayOfYear)

        // Start with hardcoded laws as fallback
        val leisResumo = HojeDataProvider.leisResumo.take(3)

        // Rotate product to show (not always the highest variation)
        val products = DataProvider.products.filter { it.category.label != "Serviços" }
        val productIndex = dayOfYear % products.size
        val product = products[productIndex]

        val variation = product.let { p ->
            val old = p.prices[2020]?.price ?: 0.0
            val cur = p.prices[DataProvider.CURRENT_YEAR]?.price ?: 0.0
            if (old > 0) ((cur - old) / old) * 100.0 else 0.0
        }

        val streakData = engagement.getStreakData()
        val unlockMessage = engagement.getUnlockMessage()
        val userSalary = engagement.userSalary

        // Economic indicators from BCB cache
        val indicators = DataSyncWorker.getIndicators(getApplication())

        // Absurdo da semana
        val weekOfYear = dayOfYear / 7
        val absurdos = listOf(
            "Judiciário brasileiro: 60 dias de férias por ano, auxílio-moradia mesmo com imóvel próprio, R\$ 35 mil/mês de salário base. Penduricalhos podem dobrar." to "CNJ",
            "Saidinha de presos: milhares de condenados liberados em feriados. 30% não voltam. Custo por preso: R\$ 2.400/mês. Por aluno: R\$ 583/mês." to "DEPEN, MEC",
            "Congresso funciona 3 dias por semana (terça a quinta). Deputado ganha R\$ 46 mil/mês. Você trabalha 5 dias." to "Câmara dos Deputados",
            "Auxílio-paletó do STF: R\$ 61 mil por ano pra comprar roupa. Mais que o salário anual de um professor." to "STF, MEC",
            "Deputado mais faltão gastou R\$ 1,2 milhão em 2024 e compareceu a 30% das sessões. Cada presença custou R\$ 18 mil." to "Câmara dos Deputados",
            "Emendas Pix de 2025: R\$ 8,5 bilhões enviados sem detalhar destino. Mesmo esquema do Orçamento Secreto." to "TCU",
            "Cartão corporativo da Presidência: R\$ 55 milhões em 2025. Sem prestação de contas detalhada." to "Portal da Transparência",
            "11.469 obras públicas paradas. R\$ 15,9 bilhões jogados fora. 70% são de saúde e educação." to "TCU, 2025",
            "Militares inativos custam R\$ 58 bilhões/ano. O Bolsa Família inteiro custa R\$ 55 bilhões." to "Tesouro Nacional",
            "Professor ganha R\$ 4.580/mês em média. Deputado: R\$ 46.366 + R\$ 57 mil em cota + R\$ 165 mil pra gabinete." to "MEC, Câmara",
            "90% dos homicídios no Brasil ficam sem solução. O país gasta R\$ 100 bilhões/ano em segurança pública." to "FBSP",
            "Fila do SUS: 1,3 milhão esperando cirurgia. Tempo médio: 2 anos e 4 meses. Seu imposto paga isso." to "Ministério da Saúde",
            "Merenda escolar: R\$ 0,36 por refeição. Almoço do deputado: cota parlamentar sem limite claro." to "FNDE, Câmara"
        )
        val absurdoIndex = weekOfYear % absurdos.size
        val (absurdoTexto, absurdoFonte) = absurdos[absurdoIndex]

        _uiState.value = HojeUiState(
            impactCard = impactCard,
            leisResumo = leisResumo,
            salarioMinimo = DataProvider.minimumWage.last().value,
            cestaBasica = DataProvider.basicBasket.last().value,
            userSalary = userSalary,
            gastoPublico = gastoPublico,
            precoDestaque = product,
            precoDestaqueVariation = variation,
            voceSabia = voceSabia,
            streakData = streakData,
            unlockMessage = unlockMessage,
            dolar = indicators.dolar,
            selic = indicators.selic,
            ipca = indicators.ipca,
            indicatorsUpdate = indicators.lastUpdate,
            absurdoDaSemana = absurdoTexto,
            absurdoFonte = absurdoFonte
        )
    }

    private fun loadLawsFromRoom() {
        viewModelScope.launch {
            try {
                govDataRepository.getAnalyzedLeis().collect { leis ->
                    if (leis.isNotEmpty()) {
                        val leisForFeed = leis.take(3).map { lei ->
                            val tag = when {
                                lei.justificativaNivel.contains("Provisória", ignoreCase = true) -> "EM VIGOR"
                                lei.nivelTrouxa >= 7 -> "ATENÇÃO"
                                else -> "EM PAUTA"
                            }
                            LeiResumo(
                                tag = tag,
                                tituloPopular = lei.tituloPopular,
                                resumo = lei.comoTeAfeta,
                                autor = if (lei.autorNome.isNotEmpty()) "${lei.autorNome} (${lei.autorPartido}-${lei.autorUf})" else lei.siglaTipo + " " + lei.numero + "/" + lei.ano,
                                detalhes = lei.resumo
                            )
                        }
                        _uiState.value = _uiState.value.copy(leisResumo = leisForFeed)
                    }
                }
            } catch (_: Exception) {
                // Keep hardcoded fallback
            }
        }
    }

    fun setUserSalary(salary: Double) {
        engagement.userSalary = salary
        _uiState.value = _uiState.value.copy(userSalary = salary)
    }
}
