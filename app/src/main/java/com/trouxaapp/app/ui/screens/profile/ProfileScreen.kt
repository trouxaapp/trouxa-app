package com.trouxaapp.app.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.ui.theme.Red600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(profileId: String, onBack: () -> Unit) {
    val profile = DataProvider.getProfileById(profileId)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(profile?.nome ?: "Perfil", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (profile == null) {
            Text("Perfil não encontrado.", modifier = Modifier.padding(paddingValues).padding(16.dp))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(profile.nome, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                    Text(profile.cargo, style = MaterialTheme.typography.bodyLarge, color = Red600, fontWeight = FontWeight.Medium)
                    if (profile.partido.isNotEmpty()) Text("${profile.partido} – ${profile.estado}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (profile.custos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(profile.custos, style = MaterialTheme.typography.bodySmall, color = Color(0xFFFBBF24))
                    }
                }
            }

            // Mandatos
            if (profile.mandatos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                SectionCard("MANDATOS", profile.mandatos)
            }

            // Decisões
            if (profile.decisoes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                SectionCard("O QUE FEZ QUE AFETOU SEU BOLSO", profile.decisoes, Red600)
            }

            // Escândalos
            if (profile.escandalos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                SectionCard("PROCESSOS / ESCÂNDALOS", profile.escandalos, Color(0xFFEF4444))
            }

            // Contact / Cobrar
            Spacer(modifier = Modifier.height(16.dp))
            if (profile.twitter.isNotEmpty()) {
                Button(
                    onClick = {
                        val text = Uri.encode("${profile.twitter} O que você tem a dizer sobre: ${profile.decisoes.firstOrNull() ?: ""} #TrouxaApp")
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text=$text")))
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DA1F2))
                ) { Text("Cobrar no X: ${profile.twitter}", color = Color.White, fontWeight = FontWeight.Bold) }
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (profile.email.isNotEmpty()) {
                Button(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${profile.email}?subject=${Uri.encode("Questionamento cidadão")}")))
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Red600)
                ) { Text("Email: ${profile.email}", color = Color.White, fontWeight = FontWeight.Bold) }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionCard(title: String, items: List<String>, titleColor: Color = Color(0xFF60A5FA)) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = titleColor, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(6.dp))
            items.forEach { item ->
                Text("• $item", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 2.dp))
            }
        }
    }
}
