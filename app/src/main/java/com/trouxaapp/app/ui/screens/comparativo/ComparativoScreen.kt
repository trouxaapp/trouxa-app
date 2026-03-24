package com.trouxaapp.app.ui.screens.comparativo

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.ui.theme.Green500
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.ui.theme.SurfaceVariant
import com.trouxaapp.app.util.FormatUtils
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparativoScreen(
    onBack: () -> Unit,
    viewModel: ComparativoViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("COMPARAR GOVERNOS", style = MaterialTheme.typography.headlineMedium, color = Red600, fontWeight = FontWeight.Black)
            Text("Selecione dois anos e veja lado a lado.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(16.dp))

            // Year A slider
            YearPickerRow("Ano A", state.yearA, state.presidentA?.let { "${it.name} (${it.party})" } ?: "") {
                viewModel.onYearAChanged(it)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Year B slider
            YearPickerRow("Ano B", state.yearB, state.presidentB?.let { "${it.name} (${it.party})" } ?: "") {
                viewModel.onYearBChanged(it)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comparison table
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("Produto", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1.2f))
                        Text(state.yearA.toString(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF60A5FA), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        Text(state.yearB.toString(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Red600, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        Text("Var.", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)

                    state.comparisons.forEach { comp ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(comp.productName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1.2f))
                            Text(FormatUtils.formatCurrency(comp.priceA), style = MaterialTheme.typography.bodySmall, color = Color(0xFF60A5FA), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            Text(FormatUtils.formatCurrency(comp.priceB), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            val varColor = if (comp.variation > 5) Red600 else if (comp.variation < -5) Green500 else Color(0xFF737373)
                            Text(FormatUtils.formatPercent(comp.variation), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = varColor, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Powerful conclusion
            if (state.allPresidentsBetween.isNotEmpty()) {
                val years = kotlin.math.abs(state.yearB - state.yearA)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1917))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Em $years anos, quem governou:",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFFFBBF24)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            state.allPresidentsBetween,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Todos.",
                            style = MaterialTheme.typography.titleLarge,
                            color = Red600,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Não é culpa de um governo. É o sistema. Todos falharam.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(24.dp))

            // SALARY SECTION
            Text("SEU SALÁRIO", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.userSalary,
                onValueChange = viewModel::onSalaryChanged,
                label = { Text("Quanto você ganha? (R\$)") },
                placeholder = { Text("Ex: 2000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Red600, cursorColor = Red600, focusedLabelColor = Red600),
                singleLine = true
            )

            state.salaryComparison?.let { comp ->
                Spacer(modifier = Modifier.height(16.dp))

                val salary = comp.userSalary
                val cestaPrice = 860.0
                val aluguel = 1200.0
                val total = cestaPrice + aluguel
                val totalPct = total / salary * 100

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SalaryBar("Salário total", 100.0, Green500)
                        Spacer(modifier = Modifier.height(8.dp))
                        SalaryBar("Cesta básica: ${FormatUtils.formatCurrency(cestaPrice)} (%.0f%%)".format(cestaPrice / salary * 100), (cestaPrice / salary * 100).coerceAtMost(100.0), Red600)
                        Spacer(modifier = Modifier.height(8.dp))
                        SalaryBar("Aluguel studio SP: ${FormatUtils.formatCurrency(aluguel)} (%.0f%%)".format(aluguel / salary * 100), (aluguel / salary * 100).coerceAtMost(100.0), Red600)
                        Spacer(modifier = Modifier.height(8.dp))
                        SalaryBar("Total básico: %.0f%%".format(totalPct), totalPct.coerceAtMost(120.0), if (totalPct > 100) Color(0xFFEF4444) else Red600)

                        if (totalPct > 100) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Seu salário não cobre o básico.", style = MaterialTheme.typography.bodyMedium, color = Red600, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("DIEESE diz que você deveria ganhar: ${FormatUtils.formatCurrency(comp.dieeseSalary)}", style = MaterialTheme.typography.bodyMedium, color = Red600)
                        Text("Você recebe %.0f%% do necessário.".format((salary / comp.dieeseSalary) * 100), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Red600)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val text = buildString {
                            appendLine("Comparação ${state.yearA} vs ${state.yearB}:")
                            state.comparisons.forEach { appendLine("${it.productName}: ${FormatUtils.formatCurrency(it.priceA)} → ${FormatUtils.formatCurrency(it.priceB)} (${FormatUtils.formatPercent(it.variation)})") }
                            appendLine()
                            appendLine("Governaram: ${state.allPresidentsBetween}")
                            appendLine("Todos.")
                            append("\n— Trouxa App")
                        }
                        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text) }, "Compartilhar"))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Red600)
                ) { Text("Compartilhar comparação", color = Color.White) }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun YearPickerRow(label: String, year: Int, presidentLabel: String, onChanged: (Int) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$year", style = MaterialTheme.typography.titleMedium, color = Red600, fontWeight = FontWeight.Bold)
        }
        if (presidentLabel.isNotEmpty()) {
            Text(presidentLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Slider(
            value = year.toFloat(),
            onValueChange = { onChanged(it.roundToInt()) },
            valueRange = DataProvider.START_YEAR.toFloat()..DataProvider.CURRENT_YEAR.toFloat(),
            steps = DataProvider.CURRENT_YEAR - DataProvider.START_YEAR - 1,
            colors = SliderDefaults.colors(thumbColor = Red600, activeTrackColor = Red600, inactiveTrackColor = SurfaceVariant)
        )
    }
}

@Composable
private fun SalaryBar(label: String, percent: Double, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.White)
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF262626))
        ) {
            Box(modifier = Modifier.fillMaxWidth(fraction = (percent / 120.0).toFloat().coerceIn(0f, 1f)).height(16.dp).clip(RoundedCornerShape(4.dp)).background(color))
        }
    }
}
