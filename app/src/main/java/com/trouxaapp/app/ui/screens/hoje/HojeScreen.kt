package com.trouxaapp.app.ui.screens.hoje

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trouxaapp.app.data.local.FeedItemEntity
import com.trouxaapp.app.data.remote.BcbApiService
import com.trouxaapp.app.ui.components.AtualizadoEm
import com.trouxaapp.app.ui.components.EmptyState
import com.trouxaapp.app.ui.components.FonteLinha
import com.trouxaapp.app.ui.components.SectionLabel
import com.trouxaapp.app.ui.components.formatIsoDate
import com.trouxaapp.app.ui.theme.Amber500
import com.trouxaapp.app.ui.theme.CardBorder
import com.trouxaapp.app.ui.theme.OnSurfaceFaint
import com.trouxaapp.app.ui.theme.OnSurfaceVariant
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.ui.theme.Surface
import com.trouxaapp.app.util.ShareCard
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * The daily numbers tab. Everything rendered here comes from the BCB cache,
 * the Camara cache or the curated remote feed. No value is computed or
 * invented in the UI layer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HojeScreen(
    onSettingsClick: () -> Unit,
    viewModel: HojeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Outer nav Scaffold already consumed the status bar inset
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 4.dp, top = 8.dp)
        ) {
            Text(
                "HOJE",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = Red600,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Configurações",
                    tint = OnSurfaceVariant
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // The sourced punches come first: they are what people share.
                item(key = "label_destaques") {
                    SectionLabel("Destaques da curadoria")
                }
                if (state.destaques.isEmpty()) {
                    item(key = "destaques_vazio") {
                        Text(
                            "Sem destaques curados no momento. Eles chegam por atualização remota, sempre com fonte.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                } else {
                    items(state.destaques, key = { it.id }) { destaque ->
                        DestaqueCard(destaque)
                    }
                }
                item(key = "label_indicadores") {
                    SectionLabel("Indicadores oficiais", Modifier.padding(top = 12.dp))
                }
                if (state.indicadores.isEmpty()) {
                    item(key = "indicadores_vazio") {
                        EmptyState(
                            titulo = "Sem dados do Banco Central ainda",
                            texto = "Puxa pra baixo pra atualizar. Aqui nunca aparece número inventado: ou é dado oficial com data, ou fica vazio."
                        )
                    }
                } else {
                    items(state.indicadores, key = { it.serie }) { indicador ->
                        IndicadorCard(indicador)
                    }
                }

                // Only shown once the local law cache exists; -1 means unknown
                if (state.projetosNovosHoje >= 0) {
                    item(key = "camara_hoje") {
                        Column {
                            SectionLabel("Câmara hoje", Modifier.padding(top = 12.dp))
                            CamaraHojeCard(state.projetosNovosHoje)
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun IndicadorCard(indicador: IndicadorUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                indicador.nome,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )
            Text(
                formatValorIndicador(indicador.serie, indicador.valor),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (indicador.dataReferencia.isNotBlank()) {
                Text(
                    "dado de ${formatIsoDate(indicador.dataReferencia)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceFaint,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            AtualizadoEm(
                timestamp = indicador.atualizadoEm,
                prefixo = "Banco Central",
                modifier = Modifier.padding(top = 6.dp)
            )
            if (indicador.stale) {
                Text(
                    "pode estar desatualizado",
                    style = MaterialTheme.typography.bodySmall,
                    color = Amber500,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun CamaraHojeCard(quantidade: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                quantidade.toString(),
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                if (quantidade == 1) "projeto de lei apresentado hoje"
                else "projetos de lei apresentados hoje",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                "Fonte: API oficial da Câmara dos Deputados",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceFaint,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun DestaqueCard(item: FeedItemEntity) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    if (item.titulo.isNotBlank()) {
                        Text(item.titulo, style = MaterialTheme.typography.titleMedium)
                    }
                    if (item.numero.isNotBlank()) {
                        Text(
                            semQuebraNoValor(item.numero),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = Red600,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
                IconButton(onClick = {
                    scope.launch {
                        ShareCard.compartilhar(
                            context,
                            ShareCard.Conteudo(
                                rotulo = item.titulo,
                                numero = item.numero,
                                titulo = "",
                                texto = item.texto,
                                fonte = item.fonte,
                                fonteUrl = item.fonteUrl,
                                dataReferencia = formatIsoDate(item.dataReferencia)
                            ),
                            legenda = "${item.titulo}\nFonte: ${item.fonte}\n${item.fonteUrl}"
                        )
                    }
                }) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Compartilhar como imagem",
                        tint = OnSurfaceVariant
                    )
                }
            }
            Text(
                item.texto,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            FonteLinha(
                fonte = item.fonte,
                url = item.fonteUrl,
                dataReferencia = formatIsoDate(item.dataReferencia),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// "R$ 1.621 x R$ 7.750" must never wrap between the currency and the value
private fun semQuebraNoValor(texto: String) = texto.replace("R$ ", "R$\u00A0")

// Each BCB series has its own unit; anything unmapped renders as a bare number
private fun formatValorIndicador(serie: Int, valor: Double): String {
    val ptBr = Locale("pt", "BR")
    return when (serie) {
        BcbApiService.DOLAR -> "R$ %,.2f".format(ptBr, valor)
        BcbApiService.SELIC -> "%.2f%% ao ano".format(ptBr, valor)
        BcbApiService.IPCA_12M -> "%.2f%% em 12 meses".format(ptBr, valor)
        BcbApiService.SALARIO_MINIMO -> "R$ %,.2f".format(ptBr, valor)
        else -> "%.2f".format(ptBr, valor)
    }
}
