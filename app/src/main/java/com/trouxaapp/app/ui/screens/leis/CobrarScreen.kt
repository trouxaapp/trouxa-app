package com.trouxaapp.app.ui.screens.leis

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trouxaapp.app.data.remote.CamaraApiService
import com.trouxaapp.app.data.remote.DeputadoDetalhe
import com.trouxaapp.app.ui.theme.Red600
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CobrarScreen(
    deputadoId: Int,
    deputadoNome: String,
    partido: String,
    uf: String,
    contexto: String,
    camaraApi: CamaraApiService,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var detalhe by remember { mutableStateOf<DeputadoDetalhe?>(null) }

    LaunchedEffect(deputadoId) {
        if (deputadoId > 0) {
            try {
                val resp = withContext(Dispatchers.IO) { camaraApi.getDeputado(deputadoId) }
                detalhe = resp.dados
            } catch (_: Exception) {}
        }
    }

    val email = detalhe?.ultimoStatus?.email
        ?: detalhe?.ultimoStatus?.gabinete?.email
        ?: "dep.${deputadoNome.lowercase().replace(" ", "")}@camara.leg.br"
    val telefone = detalhe?.ultimoStatus?.gabinete?.telefone ?: "(61) 3215-5555"
    val gabInfo = detalhe?.ultimoStatus?.gabinete?.let {
        "Gabinete: ${it.predio} ${it.sala}, andar ${it.andar}"
    } ?: ""

    val tweetText = "@${deputadoNome.replace(" ", "")} $contexto " +
        "Fonte: camara.leg.br #TrouxaApp #Transparência"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cobrar deputado", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Deputy info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(deputadoNome, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("$partido-$uf", style = MaterialTheme.typography.bodyMedium, color = Red600)
                    if (gabInfo.isNotEmpty()) {
                        Text(gabInfo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("Email: $email", style = MaterialTheme.typography.bodySmall, color = Color(0xFF60A5FA))
                    Text("Telefone: $telefone", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("COBRE SEU DEPUTADO", style = MaterialTheme.typography.headlineMedium, color = Red600, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Cobrar representantes é direito seu. Escolha como:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Twitter/X
            Button(
                onClick = {
                    val encoded = Uri.encode(tweetText)
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text=$encoded")))
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DA1F2))
            ) {
                Text("Cobrar no X / Twitter", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Email
            Button(
                onClick = {
                    val subject = Uri.encode("Questionamento sobre gastos - CEAP")
                    val body = Uri.encode(
                        "Prezado(a) deputado(a) $deputadoNome,\n\n" +
                        "Como cidadão e contribuinte, gostaria de questionar:\n\n" +
                        "$contexto\n\n" +
                        "Aguardo respostá conforme Lei de Acesso à Informação.\n\n" +
                        "Atenciosamente,\n[Seu nome]"
                    )
                    context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email?subject=$subject&body=$body")))
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Red600)
            ) {
                Text("Enviar email", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Phone
            OutlinedButton(
                onClick = {
                    val tel = telefone.replace("(", "").replace(")", "").replace("-", "").replace(" ", "")
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel")))
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ligar pro gabinete: $telefone", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Share
            OutlinedButton(
                onClick = {
                    val text = "Dep. $deputadoNome ($partido-$uf)\n$contexto\nFonte: camara.leg.br\n\n— Trouxa App"
                    context.startActivity(Intent.createChooser(
                        Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text) },
                        "Compartilhar"
                    ))
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Compartilhar o dado", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Legal note
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    "Cobrar representantes eleitos é direito constitucional (Art. 5º CF). " +
                    "Eles trabalham pra você. Seu imposto paga o salário deles. " +
                    "Você pode demiti-los? Sim. Na eleição. 4 de outubro de 2026.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
