package com.trouxaapp.app.ui.screens.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.ui.theme.OnSurfaceFaint
import com.trouxaapp.app.ui.theme.OnSurfaceVariant
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.ui.theme.SurfaceVariant
import kotlinx.coroutines.launch

private val UFS = listOf(
    "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO",
    "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI",
    "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"
)

private const val PAGE_COUNT = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onFinish: (uf: String, notifOptIn: Boolean) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })
    val scope = rememberCoroutineScope()

    var uf by rememberSaveable { mutableStateOf("") }
    var notifLigado by rememberSaveable { mutableStateOf(false) }

    // The launcher callback always finishes with optIn = true: the user said
    // yes to the feature; the OS grant only decides if notifications render.
    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        onFinish(uf, true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(PAGE_COUNT - 1) }
                },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Pular", color = OnSurfaceVariant, style = MaterialTheme.typography.labelLarge)
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> BrandPage()
                1 -> PromessaPage()
                else -> ComecarPage(
                    uf = uf,
                    onUfChange = { uf = it },
                    notifLigado = notifLigado,
                    onNotifChange = { notifLigado = it },
                    onComecar = {
                        if (notifLigado && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            onFinish(uf, notifLigado)
                        }
                    }
                )
            }
        }

        PageDots(
            total = PAGE_COUNT,
            atual = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp, top = 8.dp)
        )
    }
}

@Composable
private fun BrandPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        Text(
            "te fizeram de",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
        )
        Text(
            "TROUXA",
            color = Red600,
            fontWeight = FontWeight.Black,
            fontSize = 42.sp,
            lineHeight = 48.sp,
            letterSpacing = 2.sp
        )
        Text(
            "O que o Congresso está fazendo, traduzido pra gente normal.",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp)
        )
        Text(
            "Dados oficiais da Câmara dos Deputados e do Banco Central.",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceFaint,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun PromessaPage() {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        Text(
            "Nosso combinado com você",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        PromessaItem(
            Icons.Default.FactCheck,
            "Nada inventado: todo dado tem fonte e data, sempre visíveis."
        )
        PromessaItem(
            Icons.Default.SearchOff,
            "Quando não temos o dado, você vê 'sem dado' no lugar dele."
        )
        PromessaItem(
            Icons.Default.Balance,
            "Sem lado político. Mostramos fatos de todos os partidos e governos."
        )
        PromessaItem(
            Icons.Default.Code,
            "Código aberto: qualquer pessoa pode auditar como o app funciona."
        )
    }
}

@Composable
private fun PromessaItem(icone: ImageVector, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Icon(
            icone,
            contentDescription = null,
            tint = Red600,
            modifier = Modifier.size(24.dp)
        )
        Text(
            texto,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComecarPage(
    uf: String,
    onUfChange: (String) -> Unit,
    notifLigado: Boolean,
    onNotifChange: (Boolean) -> Unit,
    onComecar: () -> Unit
) {
    var dropdownAberto by rememberSaveable { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp)
    ) {
        Text(
            "Pra começar",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        ExposedDropdownMenuBox(
            expanded = dropdownAberto,
            onExpandedChange = { dropdownAberto = it }
        ) {
            OutlinedTextField(
                value = uf,
                onValueChange = {},
                readOnly = true,
                label = { Text("Seu estado (opcional)") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownAberto)
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = dropdownAberto,
                onDismissRequest = { dropdownAberto = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Não informar") },
                    onClick = {
                        onUfChange("")
                        dropdownAberto = false
                    }
                )
                UFS.forEach { sigla ->
                    DropdownMenuItem(
                        text = { Text(sigla) },
                        onClick = {
                            onUfChange(sigla)
                            dropdownAberto = false
                        }
                    )
                }
            }
        }
        Text(
            "Só pra listar os deputados do seu estado. Fica no aparelho.",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceFaint,
            modifier = Modifier.padding(top = 6.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
        ) {
            Text(
                "Avisar quando um projeto entrar em movimento",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = notifLigado,
                onCheckedChange = onNotifChange,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Button(
            onClick = onComecar,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .height(52.dp)
        ) {
            Text("Começar", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun PageDots(total: Int, atual: Int, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        repeat(total) { index ->
            val selecionado = index == atual
            val largura by animateDpAsState(
                targetValue = if (selecionado) 20.dp else 8.dp,
                animationSpec = tween(durationMillis = 200),
                label = "dotWidth"
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(largura)
                    .clip(CircleShape)
                    .background(if (selecionado) Red600 else SurfaceVariant)
            )
        }
    }
}
