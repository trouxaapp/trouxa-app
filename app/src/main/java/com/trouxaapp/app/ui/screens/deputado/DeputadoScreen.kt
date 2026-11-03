package com.trouxaapp.app.ui.screens.deputado

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.trouxaapp.app.data.local.DeputadoEntity
import com.trouxaapp.app.data.local.DespesaEntity
import com.trouxaapp.app.ui.components.AtualizadoEm
import com.trouxaapp.app.ui.components.EmptyState
import com.trouxaapp.app.ui.components.FonteLinha
import com.trouxaapp.app.ui.components.SectionLabel
import com.trouxaapp.app.ui.theme.CardBorder
import com.trouxaapp.app.ui.theme.OnSurfaceFaint
import com.trouxaapp.app.ui.theme.OnSurfaceVariant
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.util.FormatUtils

private val UFS = listOf(
    "AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO", "MA", "MG", "MS", "MT",
    "PA", "PB", "PE", "PI", "PR", "RJ", "RN", "RO", "RR", "RS", "SC", "SE", "SP", "TO"
)

private fun formatMesAno(mes: Int, ano: Int): String = "%02d/%d".format(mes, ano)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeputadosScreen(
    onDeputadoClick: (Int) -> Unit,
    viewModel: DeputadoViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()
    var busca by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Deputados") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text(
                    "Deputados federais em exercício. Fonte: API oficial da Câmara dos Deputados.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
                Text(
                    "Senadores ainda não aparecem aqui: o app cobre a Câmara por enquanto.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceFaint,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                ) {
                    UfSelector(
                        selectedUf = state.selectedUf,
                        onUfSelected = { viewModel.setUf(it) }
                    )
                    OutlinedTextField(
                        value = busca,
                        onValueChange = { busca = it },
                        placeholder = { Text("Buscar por nome", color = OnSurfaceFaint) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant)
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Red600,
                            unfocusedBorderColor = CardBorder,
                            cursorColor = Red600
                        )
                    )
                }
            }

            if (state.selectedUf.isBlank()) {
                EmptyState(
                    titulo = "Escolha seu estado",
                    texto = "Selecione uma UF para ver os deputados federais que representam o seu estado."
                )
            } else {
                val alvo = FormatUtils.semAcento(busca.trim())
                val filtrados = state.deputados.filter {
                    alvo.isEmpty() || FormatUtils.semAcento(it.nome).contains(alvo)
                }
                PullToRefreshBox(
                    isRefreshing = state.isLoading,
                    onRefresh = { viewModel.refreshList() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.error?.let { err ->
                            item { ErroComRetry(err) { viewModel.retryList() } }
                        }
                        if (!state.isLoading && state.error == null && state.deputados.isEmpty()) {
                            item {
                                EmptyState(
                                    titulo = "Sem dado no momento",
                                    texto = "A API da Câmara não retornou deputados para ${state.selectedUf}. Tente atualizar mais tarde."
                                )
                            }
                        }
                        if (state.deputados.isNotEmpty() && filtrados.isEmpty()) {
                            item {
                                EmptyState(
                                    titulo = "Nenhum resultado",
                                    texto = "Nenhum deputado com esse nome em ${state.selectedUf}."
                                )
                            }
                        }
                        items(filtrados, key = { it.id }) { dep ->
                            DeputadoCard(dep) { onDeputadoClick(dep.id) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UfSelector(selectedUf: String, onUfSelected: (String) -> Unit) {
    var aberto by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { aberto = true },
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Text(
                selectedUf.ifBlank { "UF" },
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Escolher estado",
                tint = OnSurfaceVariant
            )
        }
        DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            UFS.forEach { uf ->
                DropdownMenuItem(
                    text = { Text(uf) },
                    onClick = {
                        aberto = false
                        onUfSelected(uf)
                    }
                )
            }
        }
    }
}

@Composable
private fun DeputadoCard(dep: DeputadoEntity, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            AsyncImage(
                model = dep.urlFoto,
                contentDescription = "Foto de ${dep.nome}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(44.dp).clip(CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    dep.nome,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${dep.siglaPartido}-${dep.siglaUf}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeputadoProfileScreen(
    deputadoId: Int,
    onBack: () -> Unit,
    viewModel: DeputadoViewModel = hiltViewModel()
) {
    val state by viewModel.profileState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(deputadoId) { viewModel.loadProfile(deputadoId) }

    val dep = state.deputado

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        dep?.nome?.ifBlank { "Deputado" } ?: "Deputado",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // Share is factual only: name, party, month and total as returned by the API
                    if (dep != null && state.anoMaisRecente > 0) {
                        IconButton(onClick = {
                            val texto = "Gastos de ${dep.nome} (${dep.siglaPartido}-${dep.siglaUf}) " +
                                "na cota parlamentar, " +
                                "${formatMesAno(state.mesMaisRecente, state.anoMaisRecente)}: " +
                                FormatUtils.formatCurrency(state.totalGastoUltimoMes) +
                                ". Fonte: dadosabertos.camara.leg.br"
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, texto)
                            }
                            runCatching {
                                context.startActivity(Intent.createChooser(send, "Compartilhar"))
                            }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Compartilhar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when {
            state.isLoading && dep == null -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize().padding(padding)
                ) {
                    CircularProgressIndicator(color = Red600)
                }
            }
            dep == null -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize().padding(padding)
                ) {
                    ErroComRetry(
                        state.error ?: "Sem dado no momento."
                    ) { viewModel.retryProfile() }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    state.error?.let { err ->
                        item { ErroComRetry(err) { viewModel.retryProfile() } }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                        ) {
                            AsyncImage(
                                model = dep.urlFoto,
                                contentDescription = "Foto de ${dep.nome}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(72.dp).clip(CircleShape)
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    dep.nome.ifBlank { "Nome não informado" },
                                    style = MaterialTheme.typography.titleLarge
                                )
                                val partidoUf = listOf(dep.siglaPartido, dep.siglaUf)
                                    .filter { it.isNotBlank() }
                                    .joinToString("-")
                                if (partidoUf.isNotBlank()) {
                                    Text(
                                        partidoUf,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Red600
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Column(Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
                            SectionLabel("Contato oficial (Câmara)")
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, CardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(4.dp)) {
                                    if (state.email.isNotBlank()) {
                                        ContactRow(
                                            icon = Icons.Default.Email,
                                            label = "E-mail oficial",
                                            value = state.email
                                        ) {
                                            runCatching {
                                                context.startActivity(
                                                    Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${state.email}"))
                                                )
                                            }
                                        }
                                    }
                                    if (state.telefoneGabinete.isNotBlank()) {
                                        ContactRow(
                                            icon = Icons.Default.Phone,
                                            label = "Telefone do gabinete",
                                            value = state.telefoneGabinete
                                        ) {
                                            runCatching {
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_DIAL,
                                                        Uri.parse("tel:" + Uri.encode(state.telefoneGabinete))
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    if (state.email.isBlank() && state.telefoneGabinete.isBlank()) {
                                        Text(
                                            "Sem dado no momento.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = OnSurfaceVariant,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Column(Modifier.fillMaxWidth()) {
                            SectionLabel("Gastos da cota parlamentar (CEAP)")
                            if (state.anoMaisRecente > 0) {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(1.dp, CardBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(
                                            "Total de ${formatMesAno(state.mesMaisRecente, state.anoMaisRecente)}, mês mais recente com registro",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnSurfaceVariant
                                        )
                                        Text(
                                            FormatUtils.formatCurrency(state.totalGastoUltimoMes),
                                            style = MaterialTheme.typography.headlineMedium,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            } else if (!state.isLoading) {
                                Text(
                                    "A Câmara não devolveu nenhum gasto deste ano nem do anterior. " +
                                        "Às vezes a API oficial responde vazia por algumas horas; confira na fonte abaixo.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    items(state.despesas, key = { it.id }) { despesa ->
                        DespesaRow(despesa)
                    }

                    item {
                        Column(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                            AtualizadoEm(state.lastUpdate, "Dados da Câmara")
                            FonteLinha(
                                fonte = "Câmara dos Deputados",
                                url = "https://www.camara.leg.br/deputados/$deputadoId"
                            )
                            Text(
                                "A cota parlamentar (CEAP) é verba pública que custeia a atividade do mandato. Fonte: API oficial da Câmara.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceFaint,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun DespesaRow(despesa: DespesaEntity) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            Text(
                despesa.tipoDespesa.ifBlank { "Tipo não informado" },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                FormatUtils.formatCurrency(despesa.valor),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            "${despesa.fornecedor.ifBlank { "Fornecedor não informado" }} · ${formatMesAno(despesa.mes, despesa.ano)}",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceFaint,
            modifier = Modifier.padding(top = 2.dp)
        )
        HorizontalDivider(color = CardBorder, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun ErroComRetry(mensagem: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        Text(
            mensagem,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Red600)
        ) {
            Text("Tentar de novo")
        }
    }
}
