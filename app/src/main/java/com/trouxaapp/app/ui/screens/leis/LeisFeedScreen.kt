package com.trouxaapp.app.ui.screens.leis

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.trouxaapp.app.data.local.LeiEntity
import com.trouxaapp.app.ui.components.AtualizadoEm
import com.trouxaapp.app.ui.components.EmptyState
import com.trouxaapp.app.ui.components.TemaChip
import com.trouxaapp.app.ui.components.TemperaturaBadge
import com.trouxaapp.app.ui.components.formatIsoDate
import com.trouxaapp.app.ui.theme.Amber500
import com.trouxaapp.app.ui.theme.Background
import com.trouxaapp.app.ui.theme.CardBorder
import com.trouxaapp.app.ui.theme.Green500
import com.trouxaapp.app.ui.theme.OnBackground
import com.trouxaapp.app.ui.theme.OnSurface
import com.trouxaapp.app.ui.theme.OnSurfaceFaint
import com.trouxaapp.app.ui.theme.OnSurfaceVariant
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.util.LeiExplicada
import com.trouxaapp.app.ui.theme.Surface
import com.trouxaapp.app.ui.theme.SurfaceVariant

/**
 * Start tab: feed of bills pulled from the Camara dos Deputados API.
 * Everything rendered here comes from LeiEntity; no facts originate
 * in this file.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeisFeedScreen(
    onLeiClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: LeisFeedViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Brand header + settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 4.dp, top = 12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TROUXA",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Red600
                )
                Text(
                    text = "projetos de lei, sem juridiquês",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Configurações",
                    tint = OnSurfaceVariant
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            AtualizadoEm(
                timestamp = state.ultimaSincronizacao,
                prefixo = "Dados da Câmara dos Deputados"
            )
            Text(
                text = "Projetos apresentados nos últimos 30 dias",
                fontSize = 11.sp,
                color = OnSurfaceFaint,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        // Ordering
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            TrouxaFilterChip(
                selected = state.ordenacao == OrdenacaoLeis.EM_ALTA,
                onClick = { viewModel.setOrdenacao(OrdenacaoLeis.EM_ALTA) },
                label = "Em movimento"
            )
            TrouxaFilterChip(
                selected = state.ordenacao == OrdenacaoLeis.RECENTES,
                onClick = { viewModel.setOrdenacao(OrdenacaoLeis.RECENTES) },
                label = "Recentes"
            )
        }

        // Tema filter, tap again to clear
        val temas = state.temasDisponiveis.filter { it.isNotBlank() }
        if (temas.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                temas.forEach { tema ->
                    TrouxaFilterChip(
                        selected = state.temaFiltro == tema,
                        onClick = {
                            viewModel.setTemaFiltro(if (state.temaFiltro == tema) null else tema)
                        },
                        label = tema
                    )
                }
            }
        }

        state.erroSync?.let { msg ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceVariant)
                    .padding(start = 12.dp, top = 4.dp, bottom = 4.dp, end = 4.dp)
            ) {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = Amber500,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = viewModel::forceSync) {
                    Text(
                        text = "Tentar de novo",
                        style = MaterialTheme.typography.labelLarge,
                        color = Amber500
                    )
                }
            }
        }

        PullToRefreshBox(
            isRefreshing = state.isSyncing,
            onRefresh = viewModel::forceSync,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (state.leis.isEmpty()) {
                    item {
                        if (state.isSyncing) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillParentMaxSize()
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = Red600)
                                    Text(
                                        text = "Buscando projetos na Câmara…",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant,
                                        modifier = Modifier.padding(top = 12.dp)
                                    )
                                }
                            }
                        } else {
                            EmptyState(
                                titulo = "Nada por aqui ainda",
                                texto = "Os projetos vêm direto da API da Câmara. Confere a conexão e puxa pra baixo pra tentar de novo.",
                                modifier = Modifier.fillParentMaxHeight()
                            )
                        }
                    }
                } else {
                    items(state.leis, key = { it.plId }) { lei ->
                        LeiCard(lei = lei, onClick = { onLeiClick(lei.plId) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TrouxaFilterChip(selected: Boolean, onClick: () -> Unit, label: String) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Surface,
            labelColor = OnSurfaceVariant,
            selectedContainerColor = SurfaceVariant,
            selectedLabelColor = OnBackground
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = CardBorder,
            selectedBorderColor = Red600
        )
    )
}

@Composable
private fun LeiCard(lei: LeiEntity, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${lei.siglaTipo} ${lei.numero}/${lei.ano}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = OnSurface
                )
            }
            if (lei.tema.isNotBlank()) TemaChip(lei.tema, Modifier.weight(1f, fill = false))
            TemperaturaBadge(lei.temperatura)
        }

        Spacer(Modifier.height(10.dp))

        // tituloPopular is derived from the real ementa; fall back to the raw ementa
        Text(
            text = lei.tituloPopular.ifBlank { lei.ementa },
            style = MaterialTheme.typography.titleMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        val explicacao = remember(lei.siglaTipo, lei.ementa) {
            LeiExplicada.explicar(lei.siglaTipo, lei.ementa)
        }
        if (explicacao.valor.isNotBlank()) {
            Text(
                text = explicacao.valor.replace("R$ ", "R$ "),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Red600,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
        if (explicacao.emUmaFrase.isNotBlank()) {
            Text(
                text = explicacao.emUmaFrase,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        if (lei.autorNome.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (lei.autorFoto.isNotBlank()) {
                    AsyncImage(
                        model = lei.autorFoto,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Column {
                    Text(
                        text = buildString {
                            append(lei.autorNome)
                            if (lei.autorPartido.isNotBlank() && lei.autorUf.isNotBlank()) {
                                append(" (${lei.autorPartido}-${lei.autorUf})")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Show the author type when it is not a regular deputy (senator, committee, etc)
                    if (lei.autorTipo.isNotBlank() &&
                        !lei.autorTipo.contains("deputad", ignoreCase = true)
                    ) {
                        Text(
                            text = lei.autorTipo,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceFaint
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // New bills often have no "situacao" yet while the Camara already
        // logged a movement; saying "no movement" there contradicted the
        // detail screen, so the last movement is shown instead.
        val situacao = when {
            lei.ultimaSituacao.isNotBlank() -> "Situação: ${lei.ultimaSituacao}"
            lei.ultimoStatusDescricao.isNotBlank() -> "Última movimentação: ${lei.ultimoStatusDescricao}"
            else -> "Sem movimentação registrada ainda"
        }
        val dataStatus = formatIsoDate(lei.ultimoStatusData)
        Text(
            text = if (dataStatus.isNotBlank()) "$situacao · $dataStatus" else situacao,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (lei.curadoriaResumo.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Green500.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ANÁLISE DISPONÍVEL",
                    color = Green500,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        AtualizadoEm(timestamp = lei.tramitacaoAtualizadaEm, prefixo = "Câmara")
    }
}
