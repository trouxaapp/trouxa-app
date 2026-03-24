package com.trouxaapp.app.ui.screens.cidadao

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.data.model.CidadaoAction
import com.trouxaapp.app.ui.theme.Red600

private val DarkRed = Color(0xFF991B1B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CidadaoScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Seus direitos. Na teoria.", style = MaterialTheme.typography.titleLarge)
                        Text("Ordenados por eficácia real.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
                .verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            DataProvider.cidadaoActions.forEach { action ->
                ActionCard(action) {
                    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(action.url))) }
                    catch (_: Exception) { }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Footer
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    DataProvider.cidadaoFooterText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ActionCard(action: CidadaoAction, onClick: () -> Unit) {
    val notaColor = when {
        action.nota >= 7 -> Color(0xFF22C55E)
        action.nota >= 4 -> Color(0xFFFBBF24)
        action.nota >= 1 -> Red600
        else -> DarkRed
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Notá badge
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(notaColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${action.nota}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(action.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("/10", style = MaterialTheme.typography.labelSmall, color = notaColor)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(action.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Na prática:", style = MaterialTheme.typography.labelMedium, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
            Text(action.realidade, style = MaterialTheme.typography.bodySmall, color = Color(0xFFFDE68A))
            Spacer(modifier = Modifier.height(6.dp))
            Text(action.url, style = MaterialTheme.typography.bodySmall, color = Color(0xFF60A5FA), textDecoration = TextDecoration.Underline)
            Spacer(modifier = Modifier.height(4.dp))
            Text(action.frase, style = MaterialTheme.typography.bodySmall, color = Red600, fontWeight = FontWeight.Medium)
        }
    }
}
