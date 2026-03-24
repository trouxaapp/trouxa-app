package com.trouxaapp.app.ui.screens.hoje

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.data.model.StreakData
import com.trouxaapp.app.ui.theme.Red600
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HojeScreen(
    onSettingsClick: () -> Unit = {},
    onImpactClick: (Int) -> Unit = {},
    onBudgetClick: () -> Unit = {},
    onFactClick: (Int) -> Unit = {},
    onStreakClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onRepresentantesClick: () -> Unit = {},
    onLeisDoPovoClick: () -> Unit = {},
    onPorQueNadaMudaClick: () -> Unit = {},
    viewModel: HojeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("te fizeram de", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737373))
                        Text(
                            text = "TROUXA",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Red600,
                            fontWeight = FontWeight.Black
                        )
                        var phraseIndex by remember { mutableIntStateOf(0) }
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(5000)
                                phraseIndex = (phraseIndex + 1) % DataProvider.provocativePhrases.size
                            }
                        }
                        AnimatedContent(
                            targetState = phraseIndex,
                            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                            label = "phraseRotation"
                        ) { idx ->
                            Text(
                                text = DataProvider.provocativePhrases[idx],
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFA3A3A3),
                                maxLines = 1
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, "Configuracoes", tint = Color(0xFF737373))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // INDICATORS BAR
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).background(Color(0xFF22C55E), CircleShape))
                        IndicatorChip("R\$ ${"%.2f".format(state.dolar)}", "dólar")
                        Text("|", color = Color(0xFF333333), fontSize = 10.sp)
                        IndicatorChip("${"%.2f".format(state.selic)}%", "Selic")
                        Text("|", color = Color(0xFF333333), fontSize = 10.sp)
                        IndicatorChip("${"%.1f".format(state.ipca)}%", "IPCA")
                    }
                }
            }

            // Unlock message
            state.unlockMessage?.let { msg ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E1A))
                    ) {
                        Text(
                            text = msg,
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF22C55E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // CARD 1: IMPACTO DO DIA
            state.impactCard?.let { card ->
                item {
                    FeedCard(
                        title = "IMPACTO DO DIA",
                        text = card.text,
                        source = card.source,
                        context = context,
                        onClick = { onImpactClick(card.dayOfYear) },
                        actionLabel = "Entender por que"
                    )
                }
            }

            // CARD 2: LEIS QUE AFETAM SEU BOLSO
            if (state.leisResumo.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "LEIS QUE AFETAM SEU BOLSO",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF737373),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            state.leisResumo.forEach { lei ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(Red600, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(lei.tag, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(lei.autor, color = Color(0xFF737373), fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    lei.tituloPopular,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    lei.resumo,
                                    color = Color(0xFFD4D4D4),
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }

            // CARD 3: SEU BOLSO AGORA (clickable)
            item {
                SeuBolsoCard(state, viewModel, onDetailClick = onBudgetClick)
            }

            // CARD 4: QUEM PAGA A CONTA
            state.gastoPublico?.let { gasto ->
                item {
                    FeedCard(
                        title = "DINHEIRO PUBLICO HOJE",
                        text = gasto.texto,
                        subtitle = gasto.complemento,
                        source = gasto.source,
                        context = context,
                        onClick = { onBudgetClick() },
                        actionLabel = "Ver pra onde foi"
                    )
                }
            }

            // CARD 5: PREÇO QUE MUDOU
            state.precoDestaque?.let { product ->
                item {
                    val preco2020 = product.prices[2020]?.price ?: 0.0
                    val precoAtual = product.prices[DataProvider.CURRENT_YEAR]?.price ?: 0.0
                    FeedCard(
                        title = "PREÇO QUE MUDOU",
                        text = "${product.name}\n2020: R\$ ${formatPrice(preco2020)} -> 2026: R\$ ${formatPrice(precoAtual)} | +${state.precoDestaqueVariation.toInt()}%",
                        subtitle = product.fraseCustom,
                        source = product.source,
                        context = context,
                        onClick = { onProductClick(product.id) },
                        actionLabel = "Ver timeline completa"
                    )
                }
            }

            // CARD 6: VOCE SABIA?
            state.voceSabia?.let { fact ->
                item {
                    val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
                    FeedCard(
                        title = "VOCÊ SABIA?",
                        text = fact.text,
                        source = fact.source,
                        context = context,
                        onClick = { onFactClick(dayOfYear) },
                        actionLabel = "Saber mais"
                    )
                }
            }

            // CARD 7: ABSURDO DA SEMANA
            if (state.absurdoDaSemana.isNotEmpty()) {
                item {
                    FeedCard(
                        title = "ABSURDO DA SEMANA",
                        text = state.absurdoDaSemana,
                        source = state.absurdoFonte,
                        context = context,
                        onClick = {},
                        actionLabel = "Isso é real"
                    )
                }
            }

            // CARD 8: STREAK (clickable)
            item {
                StreakCard(state.streakData, onClick = onStreakClick)
            }

            // CARD 9: LEIS DO POVO (CTA)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onLeisDoPovoClick() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A0A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("VOZ DO CIDADÃO", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 2.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("71.000 ideias de lei enviadas ao Senado. 23 viraram projeto. 0,03%.", color = Color.White, fontSize = 14.sp)
                        Text("Aqui quem decide é você.", color = Color(0xFFA3A3A3), fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Ver e apoiar ideias →", color = Color(0xFFFBBF24), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // CARD 10: POR QUE NADA MUDA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onPorQueNadaMudaClick() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0A0A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Você sente que não importa o que faça?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("O sistema foi feito pra resistir mudança. Entenda como ele se protege.", color = Color(0xFFA3A3A3), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Por que nada muda? →", color = Red600, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // CARD 11: SEU DEPUTADO (CTA)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onRepresentantesClick() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Você sabe quem te representa?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Descubra quanto seu deputado gasta do seu dinheiro.", color = Color(0xFFA3A3A3), fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Ver meus representantes →", color = Color(0xFF60A5FA), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun FeedCard(
    title: String,
    text: String,
    source: String,
    context: Context,
    subtitle: String = "",
    onClick: () -> Unit = {},
    actionLabel: String = "Entender por que"
) {
    val redBorder = Red600
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .drawBehind {
                drawRect(
                    color = redBorder,
                    topLeft = Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(4.dp.toPx(), size.height)
                )
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF737373),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text,
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, color = Color(0xFFA3A3A3), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Fonte: $source", color = Color(0xFF737373), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$actionLabel →", color = Color(0xFF60A5FA), fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(onClick = onClick))
                IconButton(
                    onClick = { shareCardAsImage(context, title, text, source) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Share, "Compartilhar", tint = Color(0xFF737373), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun SeuBolsoCard(state: HojeUiState, viewModel: HojeViewModel, onDetailClick: () -> Unit = {}) {
    var showDialog by remember { mutableStateOf(false) }
    var salaryInput by remember { mutableStateOf("") }

    val salary = if (state.userSalary > 0) state.userSalary else state.salarioMinimo
    val sobra = salary - state.cestaBasica
    val label = if (state.userSalary > 0) "Seu salário" else "Salario mínimo"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(
                    color = Red600,
                    topLeft = Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(4.dp.toPx(), size.height)
                )
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)) {
            Text(
                "SEU BOLSO AGORA",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF737373),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("$label:", color = Color(0xFFA3A3A3), fontSize = 13.sp)
                    Text("R\$ ${formatPrice(salary)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Cesta básica SP:", color = Color(0xFFA3A3A3), fontSize = 13.sp)
                    Text("R\$ ${formatPrice(state.cestaBasica)}", color = Red600, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Sobra: R\$ ${formatPrice(sobra)}",
                color = if (sobra > 0) Color(0xFF22C55E) else Red600,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                "Pra aluguel, luz, agua, gas, transporte, saúde. O mês inteiro.",
                color = Color(0xFFA3A3A3),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth().height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626))
            ) {
                Text(
                    if (state.userSalary > 0) "Alterar meu salário" else "Usar meu salário",
                    color = Color.White,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Ver detalhamento completo →",
                color = Color(0xFF60A5FA),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onDetailClick)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Qual seu salario?", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = salaryInput,
                    onValueChange = { salaryInput = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Salario mensal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Red600,
                        unfocusedBorderColor = Color(0xFF404040),
                        focusedLabelColor = Red600,
                        cursorColor = Red600
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    salaryInput.toDoubleOrNull()?.let { viewModel.setUserSalary(it) }
                    showDialog = false
                }) { Text("Salvar", color = Red600) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar", color = Color(0xFF737373)) }
            },
            containerColor = Color(0xFF1A1A1A)
        )
    }
}

@Composable
private fun StreakCard(streakData: StreakData, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "${streakData.days}",
                color = Red600,
                fontWeight = FontWeight.Black,
                fontSize = 48.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                if (streakData.days == 1) "dia de consciência" else "dias de consciência",
                color = Color(0xFFA3A3A3),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                streakData.message,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatPrice(value: Double): String {
    return if (value == value.toLong().toDouble()) {
        String.format("%.0f", value)
    } else {
        String.format("%.2f", value)
    }
}

internal fun shareCardAsImage(context: Context, title: String, text: String, source: String) {
    val width = 1080
    val height = 1080
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Background
    canvas.drawColor(0xFF0A0A0A.toInt())

    val paintLogo = Paint().apply {
        color = 0xFFDC2626.toInt()
        textSize = 48f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }
    canvas.drawText("TROUXA", 60f, 100f, paintLogo)

    val paintTitle = Paint().apply {
        color = 0xFF737373.toInt()
        textSize = 32f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
        letterSpacing = 0.15f
    }
    canvas.drawText(title, 60f, 180f, paintTitle)

    val paintText = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        textSize = 44f
        isAntiAlias = true
    }

    // Word wrap text
    val maxWidth = width - 120f
    val lines = wrapText(text, paintText, maxWidth)
    var y = 280f
    for (line in lines) {
        canvas.drawText(line, 60f, y, paintText)
        y += 60f
    }

    val paintSource = Paint().apply {
        color = 0xFF737373.toInt()
        textSize = 28f
        isAntiAlias = true
    }
    canvas.drawText("Fonte: $source", 60f, height - 120f, paintSource)

    val paintUrl = Paint().apply {
        color = 0xFF404040.toInt()
        textSize = 24f
        isAntiAlias = true
    }
    canvas.drawText("trouxaapp.com", (width - 240f), (height - 60f), paintUrl)

    // Save and share
    try {
        val file = File(context.cacheDir, "trouxa_share.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartilhar"))
    } catch (_: Exception) { }
}

internal fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
    val words = text.split(" ")
    val lines = mutableListOf<String>()
    var current = ""
    for (word in words) {
        val test = if (current.isEmpty()) word else "$current $word"
        if (paint.measureText(test) <= maxWidth) {
            current = test
        } else {
            if (current.isNotEmpty()) lines.add(current)
            current = word
        }
    }
    if (current.isNotEmpty()) lines.add(current)
    return lines
}

@Composable
private fun IndicatorChip(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color(0xFF737373), fontSize = 9.sp)
    }
}
