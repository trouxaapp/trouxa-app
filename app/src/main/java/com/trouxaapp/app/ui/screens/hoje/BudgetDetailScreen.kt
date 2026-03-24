package com.trouxaapp.app.ui.screens.hoje

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.util.EngagementManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailScreen(
    onBack: () -> Unit,
    application: Application? = null
) {
    val salarioMinimo = DataProvider.minimumWage.last().value
    val cestaBasica = DataProvider.basicBasket.last().value
    val aluguelMedio = 1200.0
    val transporte = 350.0
    val contas = 280.0 // luz + água + gas
    val dieese = 7500.0

    val engagement = application?.let { EngagementManager(it) }
    val salary = engagement?.userSalary?.takeIf { it > 0 } ?: salarioMinimo
    val sobra = salary - cestaBasica
    val sobraTudo = salary - cestaBasica - aluguelMedio - transporte - contas

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SEU BOLSO", color = Red600, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // PRA ONDE VAI SEU DINHEIRO
            item {
                SectionCard("PRA ONDE VAI SEU DINHEIRO") {
                    val items = listOf(
                        Triple("Cesta básica", cestaBasica, Red600),
                        Triple("Aluguel medio", aluguelMedio, Color(0xFFFBBF24)),
                        Triple("Transporte", transporte, Color(0xFF60A5FA)),
                        Triple("Luz + água + gas", contas, Color(0xFF818CF8)),
                    )
                    items.forEach { (label, valor, cor) ->
                        val percent = (valor / salary * 100).toInt()
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("$label:", color = Color(0xFFD4D4D4), fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text("R\$ ${String.format("%.0f", valor)} ($percent%)", color = cor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        LinearProgressIndicator(
                            progress = { (valor / salary).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = cor,
                            trackColor = Color(0xFF262626)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    val sobraColor = if (sobraTudo > 0) Color(0xFF22C55E) else Red600
                    Text(
                        if (sobraTudo > 0) "Sobra: R\$ ${String.format("%.0f", sobraTudo)} por mês. R\$ ${String.format("%.0f", sobraTudo / 30)} por dia."
                        else "Faltam R\$ ${String.format("%.0f", -sobraTudo)} todo mês. Seu salário não cobre o basico.",
                        color = sobraColor, fontWeight = FontWeight.Bold, fontSize = 15.sp
                    )
                    Text(
                        "Pra saúde, educação, lazer, roupas, emergencias.",
                        color = Color(0xFF737373), fontSize = 13.sp
                    )
                }
            }

            // COMPARACAO COM DIEESE
            item {
                SectionCard("COMPARACAO COM O DIEESE") {
                    val percentDieese = (salary / dieese * 100).toInt()
                    val falta = dieese - salary
                    Text(
                        "O DIEESE calcula que o salário mínimo necessário deveria ser R\$ ${String.format("%.0f", dieese)}.",
                        color = Color(0xFFD4D4D4), fontSize = 15.sp, lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Você ganha $percentDieese% do que deveria. Faltam R\$ ${String.format("%.0f", falta)}.",
                        color = Red600, fontWeight = FontWeight.Bold, fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (salary / dieese).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Red600,
                        trackColor = Color(0xFF262626)
                    )
                }
            }

            // QUANTO DO SEU TRABALHO VAI PRO GOVERNO
            item {
                SectionCard("QUANTO VAI PRO GOVERNO") {
                    val taxRate = when {
                        salary <= salarioMinimo * 3 -> 28
                        salary <= salarioMinimo * 10 -> 25
                        else -> 20
                    }
                    val taxValue = salary * taxRate / 100
                    Text(
                        "De cada R\$ 100 que você ganha, cerca de R\$ $taxRate voltam pro governo " +
                        "em impostos indiretos (embutidos no preço de tudo que compra). " +
                        "Fora IR e INSS que já descontaram antes.",
                        color = Color(0xFFD4D4D4), fontSize = 15.sp, lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Estimativa: R\$ ${String.format("%.0f", taxValue)}/mes em impostos indiretos.",
                        color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Carga por faixa:", color = Color(0xFF737373), fontSize = 12.sp)
                    Text("Até 3 SM: ~28% (quem ganha menos paga mais proporcionalmente)", color = Color(0xFFA3A3A3), fontSize = 13.sp)
                    Text("3-10 SM: ~25%", color = Color(0xFFA3A3A3), fontSize = 13.sp)
                    Text("10+ SM: ~20%", color = Color(0xFFA3A3A3), fontSize = 13.sp)
                }
            }

            // PODER DE COMPRA
            item {
                SectionCard("SEU SALARIO AO LONGO DO TEMPO") {
                    val baskets2010 = DataProvider.minimumWage.find { it.year == 2010 }?.value?.let { w ->
                        DataProvider.basicBasket.find { it.year == 2010 }?.value?.let { b ->
                            w / b
                        }
                    } ?: 2.12
                    val baskets2026 = salarioMinimo / cestaBasica
                    Text(
                        "Em 2010, o salário mínimo comprava ${String.format("%.2f", baskets2010)} cestas básicas. " +
                        "Hoje compra ${String.format("%.2f", baskets2026)}. " +
                        "Você perdeu ${String.format("%.0f", (1 - baskets2026/baskets2010) * 100)}% de poder de compra em alimentos.",
                        color = Color(0xFFD4D4D4), fontSize = 15.sp, lineHeight = 22.sp
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
