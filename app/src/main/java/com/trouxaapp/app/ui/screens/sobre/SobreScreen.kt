package com.trouxaapp.app.ui.screens.sobre

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.ui.components.FonteLinha
import com.trouxaapp.app.ui.components.SectionLabel
import com.trouxaapp.app.ui.theme.Background
import com.trouxaapp.app.ui.theme.CardBorder
import com.trouxaapp.app.ui.theme.OnSurfaceVariant
import com.trouxaapp.app.ui.theme.Red600

// The project is maintained anonymously on purpose: no names here,
// only the contact address and the public sources.
private const val EMAIL_CORRECAO = "trouxaapp@proton.me"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SobreScreen(onBack: () -> Unit, onPrivacidadeClick: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SOBRE",
                        style = MaterialTheme.typography.titleMedium,
                        letterSpacing = 2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Brand block
            Text(
                "TROUXA APP",
                style = MaterialTheme.typography.headlineLarge,
                color = Red600,
                letterSpacing = 2.sp
            )
            Text(
                "Acompanhamento do Congresso em linguagem simples, com dados oficiais.",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.height(28.dp))
            SectionLabel("De onde vêm os dados")
            FonteLinha(
                fonte = "Câmara dos Deputados",
                url = "https://dadosabertos.camara.leg.br"
            )
            FonteLinha(
                fonte = "Banco Central",
                url = "https://dadosabertos.bcb.gov.br"
            )
            FonteLinha(
                fonte = "Curadoria editorial com fontes obrigatórias",
                url = "https://github.com/trouxaapp/trouxa-app"
            )

            Spacer(Modifier.height(28.dp))
            SectionLabel("Compromissos")
            CompromissoItem("Nada inventado")
            CompromissoItem("Fonte e data visíveis em todo dado")
            CompromissoItem("Sem lado político")
            CompromissoItem("Notificações só com dado real")
            CompromissoItem("Código aberto")

            Spacer(Modifier.height(28.dp))
            SectionLabel("Achou um erro?")
            Text(
                "Erro factual é coisa séria por aqui. Manda o print e a correção:",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
            OutlinedButton(
                onClick = {
                    val uri = Uri.parse(
                        "mailto:$EMAIL_CORRECAO?subject=" + Uri.encode("Correção de dado")
                    )
                    runCatching { context.startActivity(Intent(Intent.ACTION_SENDTO, uri)) }
                },
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Icon(
                    Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(EMAIL_CORRECAO)
            }

            Spacer(Modifier.height(28.dp))
            HorizontalDivider(color = CardBorder)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onPrivacidadeClick)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    "Política de privacidade",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = OnSurfaceVariant
                )
            }
            HorizontalDivider(color = CardBorder)

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CompromissoItem(texto: String) {
    Row(modifier = Modifier.padding(vertical = 3.dp)) {
        Text("•", color = Red600, style = MaterialTheme.typography.bodyMedium)
        Text(
            texto,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}
