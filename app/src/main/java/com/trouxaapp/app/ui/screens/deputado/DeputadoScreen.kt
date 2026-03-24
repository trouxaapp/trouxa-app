package com.trouxaapp.app.ui.screens.deputado

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.trouxaapp.app.data.remote.DeputadoResumo
import com.trouxaapp.app.ui.theme.Red600

private val UFS = listOf(
    "AC","AL","AM","AP","BA","CE","DF","ES","GO","MA","MG","MS","MT",
    "PA","PB","PE","PI","PR","RJ","RN","RO","RR","RS","SC","SE","SP","TO"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeputadoScreen(
    onDeputadoClick: (Int) -> Unit = {},
    viewModel: DeputadoViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("SEU DEPUTADO", color = Red600, fontWeight = FontWeight.Black)
                        Text(
                            if (state.uf.isNotEmpty()) "Estado: ${state.uf}" else "Escolha seu estado",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFA3A3A3)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (state.needsUf) {
            UfSelectorContent(
                modifier = Modifier.padding(paddingValues),
                onUfSelected = { viewModel.selectUf(it) }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Change UF button
                item {
                    var showDropdown by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { showDropdown = true },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Mudar estado (${state.uf})", color = Color(0xFFA3A3A3))
                        }
                        DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
                            UFS.forEach { uf ->
                                DropdownMenuItem(
                                    text = { Text(uf) },
                                    onClick = { viewModel.selectUf(uf); showDropdown = false }
                                )
                            }
                        }
                    }
                }

                // Senadores info
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("SENADORES", color = Color(0xFF737373), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "Cada estado tem 3 senadores. Custo médio: R\$ 5,2 milhões/ano cada. São 81 no total. Integração com API do Senado em breve.",
                                color = Color(0xFFA3A3A3),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Estado resumo card
                if (state.deputados.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("${state.uf}: ${state.deputados.size} deputados federais", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                val custoEstimado = state.deputados.size * 3.5
                                Text("Custo estimado: R\$ ${String.format("%.0f", custoEstimado)} milhões/ano", color = Color(0xFFFBBF24), fontSize = 12.sp)
                                Text("Você paga isso. Eles trabalham pra você. Será?", color = Color(0xFF737373), fontSize = 11.sp)
                            }
                        }
                    }
                }

                if (state.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Red600)
                        }
                    }
                }

                state.error?.let { err ->
                    item {
                        Text(err, color = Red600, modifier = Modifier.padding(16.dp))
                    }
                }

                items(state.deputados, key = { it.id }) { dep ->
                    DeputadoCard(dep) { onDeputadoClick(dep.id) }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun UfSelectorContent(modifier: Modifier, onUfSelected: (String) -> Unit) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "De qual estado você e?",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Vamos mostrar quem te representa e quanto custa.",
            color = Color(0xFFA3A3A3),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Grid of UF buttons
        val rows = UFS.chunked(5)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                row.forEach { uf ->
                    Button(
                        onClick = { onUfSelected(uf) },
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(uf, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DeputadoCard(dep: DeputadoResumo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = dep.urlFoto,
                contentDescription = dep.nome,
                modifier = Modifier.size(56.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(dep.nome, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Red600.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(dep.siglaPartido, color = Red600, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(dep.siglaUf, color = Color(0xFF737373), fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Custo médio: R\$ 3,5 mi/ano. Toque pra ver o real.",
                    color = Color(0xFFFBBF24),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeputadoProfileScreen(
    deputadoId: Int,
    onBack: () -> Unit = {},
    viewModel: DeputadoViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    val profileState by viewModel.profileState.collectAsState()
    val context = LocalContext.current

    // Find deputado and load profile
    val deputado = listState.deputados.find { it.id == deputadoId }
    if (deputado != null && profileState.deputado?.id != deputadoId) {
        viewModel.loadProfile(deputado)
    }

    val dep = profileState.deputado ?: deputado

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(dep?.nome ?: "Deputado", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (profileState.isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Red600)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                dep?.let { d ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = d.urlFoto,
                                    contentDescription = d.nome,
                                    modifier = Modifier.size(72.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(d.nome, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("${d.siglaPartido} - ${d.siglaUf}", color = Red600, fontSize = 14.sp)
                                    if (profileState.email.isNotEmpty()) {
                                        Text(profileState.email, color = Color(0xFF737373), fontSize = 12.sp)
                                    }
                                    if (profileState.telefone.isNotEmpty()) {
                                        Text(profileState.telefone, color = Color(0xFF737373), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Gastos
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("GASTOS COM DINHEIRO PUBLICO", color = Color(0xFF737373), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 2.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Seu deputado gastou R\$ ${String.format("%,.2f", profileState.totalGasto)} em 2025/2026",
                                color = Red600,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            profileState.despesasPorTipo.forEach { (tipo, valor) ->
                                Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(tipo.take(35), color = Color(0xFFD4D4D4), fontSize = 13.sp, modifier = Modifier.weight(1f))
                                    Text("R\$ ${String.format("%,.0f", valor)}", color = Color(0xFFFBBF24), fontSize = 13.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Fonte: Câmara dos Deputados", color = Color(0xFF505050), fontSize = 11.sp)
                        }
                    }
                }

                // Presenca
                item {
                    val presColor = when {
                        profileState.presencaPercent >= 80 -> Color(0xFF22C55E)
                        profileState.presencaPercent >= 60 -> Color(0xFFFBBF24)
                        else -> Red600
                    }
                    val presTag = when {
                        profileState.presencaPercent >= 80 -> "PRESENTE"
                        profileState.presencaPercent < 60 -> "FALTÃO"
                        else -> ""
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("PRESENÇA", color = Color(0xFF737373), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 2.sp)
                                if (presTag.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(Modifier.background(presColor, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text(presTag, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Participou de ${profileState.eventosPresente} de ${profileState.totalEventos} eventos (${profileState.presencaPercent}%)",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Votos recentes
                if (profileState.votosRecentes.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("COMO VOTOU", color = Color(0xFF737373), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 2.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                profileState.votosRecentes.forEach { voto ->
                                    Row(Modifier.padding(vertical = 4.dp)) {
                                        Box(
                                            Modifier.background(
                                                if (voto.voto.contains("Sim", ignoreCase = true)) Color(0xFF22C55E) else Red600,
                                                RoundedCornerShape(4.dp)
                                            ).padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(voto.voto, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(voto.descricao, color = Color(0xFFD4D4D4), fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Smart cobrar section
                dep?.let { d ->
                    item {
                        val ps = profileState
                        // Generate context-aware cobrar
                        val motivo: String
                        val tweetText: String
                        when {
                            ps.presencaPercent < 70 -> {
                                motivo = "Faltou em ${100 - ps.presencaPercent}% das sessões. Gastou R\$ ${String.format("%,.0f", ps.totalGasto)} do contribuinte."
                                tweetText = "Deputado(a) ${d.nome}, você faltou em ${100 - ps.presencaPercent}% das sessões em 2025. Gastou R\$ ${String.format("%,.0f", ps.totalGasto)} do nosso dinheiro. O que justifica? #TrouxaApp"
                            }
                            ps.totalGasto > 500000 -> {
                                motivo = "Gastou R\$ ${String.format("%,.0f", ps.totalGasto)} em 2025. Acima da média."
                                tweetText = "Deputado(a) ${d.nome}, você gastou R\$ ${String.format("%,.0f", ps.totalGasto)} em 2025. Pra onde foi? Detalhe por favor. #TrouxaApp"
                            }
                            ps.votosRecentes.isEmpty() -> {
                                motivo = "Sem registro de votações recentes. Custo: R\$ 3,5 milhões/ano."
                                tweetText = "Deputado(a) ${d.nome}, o que você fez em 2025 que justifica R\$ 3,5 milhões do contribuinte? #TrouxaApp"
                            }
                            else -> {
                                motivo = "Custo anual: R\$ 3,5 milhões. Presença: ${ps.presencaPercent}%."
                                tweetText = "Deputado(a) ${d.nome}, como cidadão(a) de ${d.siglaUf}, quero saber: o que você fez pelo meu estado em 2025? #TrouxaApp"
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0A0A))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("POR QUÊ COBRAR:", color = Color(0xFF737373), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(motivo, color = Color.White, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(12.dp))

                                // Tweet button
                                Button(
                                    onClick = {
                                        val encoded = Uri.encode(tweetText)
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text=$encoded")))
                                    },
                                    modifier = Modifier.fillMaxWidth().height(44.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Red600)
                                ) {
                                    Text("COBRAR NO X", color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                // Email button
                                if (ps.email.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${ps.email}")).apply {
                                                putExtra(Intent.EXTRA_SUBJECT, "Questionamento do cidadão")
                                                putExtra(Intent.EXTRA_TEXT, tweetText.replace("#TrouxaApp", ""))
                                            }
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF404040))
                                    ) {
                                        Text("Enviar email", color = Color(0xFFA3A3A3), fontSize = 13.sp)
                                    }
                                }

                                // Phone button
                                if (ps.telefone.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedButton(
                                        onClick = {
                                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${ps.telefone}")))
                                        },
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF404040))
                                    ) {
                                        Text("Ligar pro gabinete: ${ps.telefone}", color = Color(0xFFA3A3A3), fontSize = 13.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Ele provavelmente vai ignorar. Mas se 10 mil pessoas cobrarem o MESMO dado no MESMO dia, ele não ignora.",
                                    color = Color(0xFF505050), fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Back
                item {
                    TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                        Text("Voltar", color = Color(0xFF737373))
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }

        profileState.error?.let {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(it, color = Red600)
            }
        }
    }
}

@Composable
private fun TextButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick, modifier = modifier) { content() }
}
