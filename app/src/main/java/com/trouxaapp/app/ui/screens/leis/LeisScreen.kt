package com.trouxaapp.app.ui.screens.leis

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trouxaapp.app.data.local.AnalyzedLeiEntity
import com.trouxaapp.app.data.remote.ProposicaoApi
import com.trouxaapp.app.ui.theme.Green500
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.ui.theme.SurfaceVariant

private val LiveGreen = Color(0xFF22C55E)
private val TrouxaYellow = Color(0xFFFBBF24)

@Composable
fun LeisScreen(
    onBack: () -> Unit = {},
    onCobrar: (Int, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onLeiDetail: (Int) -> Unit = {},
    viewModel: LeisViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("O QUE ESTÃO FAZENDO", style = MaterialTheme.typography.titleLarge, color = Red600, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.width(8.dp))
                LiveDot()
            }
            Text("Leis em andamento traduzidas pro cidadão", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (state.isSyncing) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp, color = TrouxaYellow)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sincronizando com a Câmara...", style = MaterialTheme.typography.labelSmall, color = TrouxaYellow)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filters
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                LeisOrdem.entries.forEach { ordem ->
                    val sel = state.ordem == ordem
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (sel) Red600 else SurfaceVariant)
                            .clickable { viewModel.onOrdemChanged(ordem) }.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(ordem.label, style = MaterialTheme.typography.labelMedium, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal, color = if (sel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                state.isLoading && state.analyzedLeis.isEmpty() && state.rawLeis.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Red600)
                            Text("Consultando Câmara...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                state.error != null && state.analyzedLeis.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.error!!, color = Red600)
                            Button(onClick = { viewModel.retry() }, colors = ButtonDefaults.buttonColors(containerColor = Red600)) { Text("Tentar novamente", color = Color.White) }
                        }
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Show analyzed laws first
                        items(state.analyzedLeis, key = { it.plId }) { lei ->
                            AnalyzedLeiCard(lei) {
                                val url = "https://www.camara.leg.br/proposicoesWeb/fichadetramitacao?idProposicao=${lei.plId}"
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        }
                        // Show raw laws that haven't been analyzed yet
                        val analyzedIds = state.analyzedLeis.map { it.plId }.toSet()
                        val unanalyzed = state.rawLeis.filter { it.id !in analyzedIds }
                        items(unanalyzed, key = { it.id }) { pl ->
                            RawPlCard(pl) {
                                val url = "https://www.camara.leg.br/proposicoesWeb/fichadetramitacao?idProposicao=${pl.id}"
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyzedLeiCard(lei: AnalyzedLeiEntity, onClick: () -> Unit) {
    val nivelColor = when {
        lei.nivelTrouxa >= 7 -> Red600
        lei.nivelTrouxa >= 5 -> TrouxaYellow
        else -> Green500
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with badge and nivel
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(nivelColor).padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Text("${lei.siglaTipo} ${lei.numero}/${lei.ano}", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nível trouxa: ${lei.nivelTrouxa}/10", style = MaterialTheme.typography.labelSmall, color = nivelColor, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text("↗", color = Color(0xFF60A5FA))
            }

            // Nivel trouxa bar
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { lei.nivelTrouxa / 10f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = nivelColor,
                trackColor = Color(0xFF262626)
            )

            // Title and summary
            Spacer(modifier = Modifier.height(8.dp))
            Text(lei.tituloPopular, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(lei.resumo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Como te afeta
            if (lei.comoTeAfeta.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                        .background(if (lei.nivelTrouxa >= 7) Red600.copy(alpha = 0.15f) else Color(0xFF1A1A1A))
                        .padding(10.dp)
                ) {
                    Column {
                        Text("COMO TE AFETA:", style = MaterialTheme.typography.labelSmall, color = nivelColor, fontWeight = FontWeight.Black)
                        Text(lei.comoTeAfeta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            // Author
            if (lei.autorNome.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Autor: ${lei.autorNome}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Justificativa
            Spacer(modifier = Modifier.height(4.dp))
            Text(lei.justificativaNivel, style = MaterialTheme.typography.bodySmall, color = nivelColor, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun RawPlCard(pl: ProposicaoApi, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(SurfaceVariant).padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Text("${pl.siglaTipo} ${pl.numero}/${pl.ano}", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ementá original", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737373))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(pl.ementa.take(200), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LiveDot() {
    val t = rememberInfiniteTransition(label = "ld")
    val a by t.animateFloat(1f, 0.3f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "a")
    Box(modifier = Modifier.size(8.dp).alpha(a).clip(CircleShape).background(LiveGreen))
}
