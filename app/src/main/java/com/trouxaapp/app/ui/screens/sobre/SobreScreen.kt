package com.trouxaapp.app.ui.screens.sobre

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.util.EngagementManager

@Composable
fun SobreScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val linkColor = Color(0xFF60A5FA)

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Main description
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("TROUXA", style = MaterialTheme.typography.headlineLarge, color = Red600, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Esse app mostra o que tá acontecendo com o seu dinheiro.\n" +
                        "Não com opinião. Com dados do IBGE, DIEESE, ANP, Banco Central e Portal da Transparência.\n\n" +
                        "Cada numero tem fonte. Cada governo é mostrado.\n" +
                        "Cada decisão tem nome, cargo e consequência.\n\n" +
                        "Não é de esquerda. Não é de direita.\n" +
                        "É de quem paga a conta.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Why the name
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "O nome é Trouxa porque é exatamente isso que estão fazendo com você.\n" +
                        "Devagarinho. Sem você perceber.\n\n" +
                        "Até agora.",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Open source
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/trouxaapp"))) }
                        catch (_: Exception) {}
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Código aberto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("O código é aberto. Qualquer pessoa pode verificar.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("github.com/trouxaapp", style = MaterialTheme.typography.bodyMedium, color = linkColor, textDecoration = TextDecoration.Underline)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Datá sources
            Text("Fontes de dados", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    listOf(
                        "DIEESE – Cesta Básica" to "dieese.org.br",
                        "IBGE – IPCA / SIDRA" to "sidra.ibge.gov.br",
                        "ANP – Preços de combustíveis" to "precos.anp.gov.br",
                        "ANEEL – Tarifas de energia" to "aneel.gov.br",
                        "Banco Central – Selic, câmbio" to "bcb.gov.br",
                        "Portal da Transparência" to "portaldatransparencia.gov.br",
                        "Câmara dos Deputados" to "camara.leg.br",
                        "TCU – Obras paradas" to "portal.tcu.gov.br",
                        "CONAB – Estoques agrícolas" to "conab.gov.br"
                    ).forEachIndexed { i, (name, url) ->
                        if (i > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 6.dp))
                        Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        Text(url, style = MaterialTheme.typography.bodySmall, color = linkColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notification settings
            Text("Notificações", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                val engagement = remember { EngagementManager(context) }
                var cafe by remember { mutableStateOf(engagement.notifCafe) }
                var almoco by remember { mutableStateOf(engagement.notifAlmoco) }
                var trabalho by remember { mutableStateOf(engagement.notifTrabalho) }
                var noite by remember { mutableStateOf(engagement.notifNoite) }

                Column(modifier = Modifier.padding(16.dp)) {
                    NotifToggle("Café (7h)", "Dado do dia pela manhã", cafe) {
                        cafe = it; engagement.notifCafe = it
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 6.dp))
                    NotifToggle("Almoço (12h)", "Dado na hora do almoço", almoco) {
                        almoco = it; engagement.notifAlmoco = it
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 6.dp))
                    NotifToggle("Trabalho (18h)", "Dado no fim do expediente", trabalho) {
                        trabalho = it; engagement.notifTrabalho = it
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 6.dp))
                    NotifToggle("Noite (20:30)", "Reflexao da noite", noite) {
                        noite = it; engagement.notifNoite = it
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Versão 3.0.0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun NotifToggle(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Red600,
                uncheckedTrackColor = Color(0xFF333333)
            )
        )
    }
}
