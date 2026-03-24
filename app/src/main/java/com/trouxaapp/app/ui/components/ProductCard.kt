package com.trouxaapp.app.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.data.model.PriceComparison
import com.trouxaapp.app.ui.theme.Green500
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.util.FormatUtils
import java.io.File
import kotlin.math.roundToInt

private fun formatWorkTime(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return if (h > 0) "${h}h${if (m > 0) "${m}min" else ""}" else "${m}min"
}

@Composable
fun ProductCard(
    comparison: PriceComparison,
    selectedMonth: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMotivo by remember { mutableStateOf(false) }

    val monthlyPrices = if (selectedMonth != null)
        DataProvider.getMonthlyPrices(comparison.product.id, comparison.selectedYear) else null
    val monthlyPrice = monthlyPrices?.get(selectedMonth)

    // Calculate work time
    val startWage = DataProvider.minimumWage.find { it.year == comparison.selectedYear }?.value
    val currentWage = DataProvider.minimumWage.find { it.year == DataProvider.CURRENT_YEAR }?.value
    val minutesOld = if (startWage != null && startWage > 0 && comparison.selectedPrice > 0) (comparison.selectedPrice / (startWage / 220.0) * 60).toInt() else 0
    val minutesNow = if (currentWage != null && currentWage > 0 && comparison.currentPrice > 0) (comparison.currentPrice / (currentWage / 220.0) * 60).toInt() else 0
    val multiplier = if (minutesOld > 0) (minutesNow.toDouble() / minutesOld).let { "%.1f".format(it) } else ""

    // Salary units
    val unitsOld = if (startWage != null && comparison.selectedPrice > 0) (startWage / comparison.selectedPrice).toInt() else 0
    val unitsNow = if (currentWage != null && comparison.currentPrice > 0) (currentWage / comparison.currentPrice).toInt() else 0

    val isSameYear = comparison.selectedYear == comparison.currentYear

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: name + share
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(comparison.product.name.uppercase(), style = MaterialTheme.typography.labelMedium, color = Color(0xFF9CA3AF), letterSpacing = 2.sp)
                IconButton(onClick = { shareProductCard(context, comparison) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Share, "Compartilhar", modifier = Modifier.size(16.dp), tint = Color(0xFF737373))
                }
            }

            if (!isSameYear) {
                Spacer(modifier = Modifier.height(6.dp))

                // HUMAN comparison - the main content
                Text("Em ${comparison.selectedYear}: ${FormatUtils.formatCurrency(comparison.selectedPrice)}", color = Color(0xFF737373), fontSize = 13.sp)
                Text("Hoje: ${FormatUtils.formatCurrency(comparison.currentPrice)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)

                Spacer(modifier = Modifier.height(8.dp))

                // WORK TIME - the real highlight
                if (minutesOld > 0 && minutesNow > 0) {
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFF1A1A00)).padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tempo de trabalho pra comprar:", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${comparison.selectedYear}: ${formatWorkTime(minutesOld)}", color = Color(0xFFA3A3A3), fontSize = 13.sp)
                                Text("Hoje: ${formatWorkTime(minutesNow)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            if (minutesNow > minutesOld) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "${multiplier}x mais tempo trabalhando pelo mesmo produto.",
                                    color = Red600, fontSize = 12.sp, fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // SALARY UNITS - always visible
                if (unitsOld > 0 && unitsNow > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Com 1 salário mínimo: ${comparison.selectedYear}: $unitsOld ${comparison.product.unit} | Hoje: $unitsNow ${comparison.product.unit}",
                        color = Color(0xFF737373), fontSize = 11.sp
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Preço atual: ${FormatUtils.formatCurrency(comparison.currentPrice)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }

            if (monthlyPrice != null && selectedMonth != null) {
                Text("${DataProvider.monthNames[selectedMonth]}/${comparison.selectedYear}: ${FormatUtils.formatCurrency(monthlyPrice)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFBBF24), fontWeight = FontWeight.Medium)
            }

            // Provocative phrase
            Spacer(modifier = Modifier.height(6.dp))
            Text(comparison.product.fraseCustom.ifEmpty { DataProvider.getGenericPhrase(comparison.variationPercent) }, style = MaterialTheme.typography.bodySmall, color = Red600.copy(alpha = 0.85f), fontWeight = FontWeight.Medium)

            // Action row
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { showMotivo = !showMotivo }.padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(if (showMotivo) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, modifier = Modifier.size(16.dp), tint = Color(0xFF60A5FA))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Por quê?", style = MaterialTheme.typography.labelLarge, color = Color(0xFF60A5FA))
                }
                Row(modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Ver história completa", style = MaterialTheme.typography.labelSmall, color = Color(0xFF60A5FA))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(14.dp), tint = Color(0xFF60A5FA))
                }
            }

            // Source
            Text("Fonte: ${comparison.product.source}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF505050), modifier = Modifier.padding(top = 2.dp))

            // Expandable section
            AnimatedVisibility(visible = showMotivo, enter = expandVertically(), exit = shrinkVertically()) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (comparison.product.hipocrisia.isNotEmpty()) {
                        Text(comparison.product.hipocrisia, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text("${comparison.selectedYear}: ${comparison.selectedMotivo}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)

                    if (comparison.selectedQuemDecidiu.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("QUEM DECIDIU:", style = MaterialTheme.typography.labelMedium, color = Red600, fontWeight = FontWeight.Black)
                        Text(comparison.selectedQuemDecidiu, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }

                    if (comparison.product.contextoCompleto.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("A HISTÓRIA COMPLETA:", style = MaterialTheme.typography.labelMedium, color = Red600, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(comparison.product.contextoCompleto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)
                    }

                    // What you can do
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Red600.copy(alpha = 0.1f)).padding(10.dp)) {
                        Column {
                            Text("O QUE VOCÊ PODE FAZER:", style = MaterialTheme.typography.labelMedium, color = Red600, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Esse preço depende de câmbio, exportação, estoques e decisões de governo. Nenhum desses fatores está na sua mão. O que resta: saber quem decidiu e cobrar na eleição.", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD4D4D4))
                        }
                    }
                }
            }
        }
    }
}

private fun shareProductCard(context: Context, comparison: PriceComparison) {
    val currentWage = DataProvider.minimumWage.find { it.year == DataProvider.CURRENT_YEAR }?.value ?: 1518.0
    val minutesNow = if (currentWage > 0 && comparison.currentPrice > 0) (comparison.currentPrice / (currentWage / 220.0) * 60).toInt() else 0
    val timeNow = formatWorkTime(minutesNow)

    val text = buildString {
        appendLine(comparison.product.name)
        appendLine("Em ${comparison.selectedYear}: ${FormatUtils.formatCurrency(comparison.selectedPrice)}")
        appendLine("Hoje: ${FormatUtils.formatCurrency(comparison.currentPrice)}")
        appendLine("Tempo de trabalho pra comprar: $timeNow")
        if (comparison.product.fraseCustom.isNotEmpty()) appendLine(comparison.product.fraseCustom)
        appendLine("Fonte: ${comparison.product.source}")
        appendLine()
        append("trouxaapp.com")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Compartilhar"))
}
