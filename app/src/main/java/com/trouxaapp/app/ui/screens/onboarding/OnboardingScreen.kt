package com.trouxaapp.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.ui.theme.Red600
import kotlinx.coroutines.delay

private val Bg = Color(0xFF0A0A0A)

private val UFS = listOf(
    "AC","AL","AM","AP","BA","CE","DF","ES","GO","MA","MG","MS","MT",
    "PA","PB","PE","PI","PR","RJ","RN","RO","RR","RS","SC","SE","SP","TO"
)

@Composable
fun OnboardingScreen(onFinish: (String) -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }
    var selectedUf by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Bg)) {
        when (currentPage) {
            0 -> QuantoCustavaPage { currentPage = 1 }
            1 -> NinguémPercebeuPage { currentPage = 2 }
            2 -> AgoraVoceSabePage(
                selectedUf = selectedUf,
                onUfSelected = { selectedUf = it },
                onFinish = { onFinish(selectedUf) }
            )
        }
    }
}

// --- PAGE 1: QUANTO CUSTAVA ---
@Composable
private fun QuantoCustavaPage(onNext: () -> Unit) {
    var showData by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(400)
        showData = true
        delay(1500)
        showButton = true
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedVisibility(visible = showData, enter = fadeIn(tween(600))) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("QUANTO CUSTAVA", color = Color(0xFF737373), fontSize = 14.sp, letterSpacing = 4.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PriceColumn("ARROZ 5KG", "R\$ 4,50", "R\$ 30", "+567%")
                        PriceColumn("CAFE 500G", "R\$ 3", "R\$ 28", "+833%")
                        PriceColumn("GAS 13KG", "R\$ 18", "R\$ 115", "+539%")
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        "Você percebeu quando isso mudou?",
                        color = Color(0xFFA3A3A3),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(visible = showButton, enter = fadeIn(tween(600))) {
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626))
                ) {
                    Text("Nao", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun PriceColumn(product: String, old: String, current: String, percent: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(product, color = Color(0xFF737373), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("2000", color = Color(0xFF505050), fontSize = 11.sp)
        Text(old, color = Color(0xFF737373), fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Hoje", color = Color(0xFF505050), fontSize = 11.sp)
        Text(current, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(percent, color = Red600, fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}

// --- PAGE 2: NINGUEM PERCEBEU ---
@Composable
private fun NinguémPercebeuPage(onNext: () -> Unit) {
    val lines = listOf(
        "Entrou devagarinho.",
        "Você se adaptou a comer menos carne.",
        "A escolher a marca mais barata.",
        "A deixar item na prateleira.",
        "A achar normal pagar R\$ 13 na dúzia de ovo.",
        "",
        "Você se adaptou as ruínas."
    )
    var visibleCount by remember { mutableIntStateOf(0) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        for (i in lines.indices) {
            delay(if (lines[i].isEmpty()) 800 else 2000)
            visibleCount = i + 1
        }
        delay(1500)
        showButton = true
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text("NINGUEM PERCEBEU", color = Color(0xFF737373), fontSize = 12.sp, letterSpacing = 4.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            lines.forEachIndexed { i, line ->
                AnimatedVisibility(visible = i < visibleCount, enter = fadeIn(tween(500))) {
                    if (line.isEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        val isLast = line == "Você se adaptou as ruínas."
                        Text(
                            text = line,
                            fontSize = if (isLast) 24.sp else 18.sp,
                            fontWeight = if (isLast) FontWeight.Black else FontWeight.Normal,
                            color = if (isLast) Red600 else Color(0xFFD4D4D4),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(visible = showButton, enter = fadeIn(tween(600))) {
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Red600)
                ) {
                    Text("Continuar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

// --- PAGE 3: AGORA VOCE VAI SABER ---
@Composable
private fun AgoraVoceSabePage(
    selectedUf: String,
    onUfSelected: (String) -> Unit,
    onFinish: () -> Unit
) {
    var showText by remember { mutableStateOf(false) }
    var showUf by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(400)
        showText = true
        delay(2000)
        showUf = true
        delay(500)
        showButton = true
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = showText, enter = fadeIn(tween(600))) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("AGORA VOCE VAI SABER", color = Color(0xFF737373), fontSize = 12.sp, letterSpacing = 4.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "Esse app mostra o que já e público de um jeito que ninguem consegue ignorar.",
                        color = Color(0xFFD4D4D4),
                        fontSize = 17.sp,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Cada dado tem fonte. Cada governo e mostrado. Sem lado.",
                        color = Color(0xFFD4D4D4),
                        fontSize = 17.sp,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "O nome e Trouxa porque e exatamente isso que estão fazendo com você.",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Até agora.",
                        color = Red600,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(visible = showUf, enter = fadeIn(tween(500))) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "De qual estado você e?",
                        color = Color(0xFFA3A3A3),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                if (selectedUf.isEmpty()) "Selecionar estado" else selectedUf,
                                color = if (selectedUf.isEmpty()) Color(0xFF737373) else Color.White,
                                fontSize = 16.sp
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            UFS.forEach { uf ->
                                DropdownMenuItem(
                                    text = { Text(uf) },
                                    onClick = {
                                        onUfSelected(uf)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(visible = showButton, enter = fadeIn(tween(600))) {
                Button(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Red600)
                ) {
                    Text("Começar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
