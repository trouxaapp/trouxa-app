package com.trouxaapp.app.ui.screens.hoje

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.trouxaapp.app.ui.components.Selo
import com.trouxaapp.app.ui.components.Veredito
import com.trouxaapp.app.ui.components.formatIsoDate
import com.trouxaapp.app.ui.theme.Amber500
import com.trouxaapp.app.ui.theme.CardBorder
import com.trouxaapp.app.ui.theme.OnSurface
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
                val painel = state.painel
                val verdade = state.destaques.firstOrNull { it.tipo == "destaque" }
                    ?: state.destaques.firstOrNull()

                // Abre com o soco: a verdade do dia, o carimbo, o "e daí?"
                // e a ofensiva. O resto do app é prova disso.
                if (verdade != null) {
                    item(key = "verdade_do_dia") {
                        VerdadeDoDia(
                            item = verdade,
                            ofensiva = state.ofensiva,
                            vistas = state.verdadesVistas,
                            onLida = viewModel::marcarVerdadeLida
                        )
                    }
                }
                if (state.esculacho.isNotBlank()) {
                    item(key = "esculacho") {
                        Text(
                            "“${state.esculacho}”",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceFaint,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }

                // What happened today comes first; the long-run comparisons go below
                item(key = "label_dinheiro") {
                    SectionLabel("Seu bolso hoje")
                }
                if (painel != null && painel.mercado.isNotEmpty()) {
                    items(painel.mercado, key = { "m_" + it.nome }) { IndicadorPainelCard(it) }
                } else if (state.indicadores.isEmpty()) {
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

                // Step values: when they last moved, and from what
                if (painel != null && painel.parados.isNotEmpty()) {
                    item(key = "label_parados") {
                        SectionLabel("O que mexeu e o que continua igual", Modifier.padding(top = 12.dp))
                    }
                    items(painel.parados, key = { "p_" + it.nome }) { IndicadorPainelCard(it) }
                }

                painel?.precos?.let { precos ->
                    item(key = "label_precos") {
                        SectionLabel("O que ficou mais caro", Modifier.padding(top = 12.dp))
                    }
                    item(key = "precos") { PrecosCard(precos) }
                }

                // Congress: who voted what, what is on today
                item(key = "label_congresso") {
                    SectionLabel("Eles hoje", Modifier.padding(top = 12.dp))
                }
                val camaraHoje = painel?.camaraHoje
                if (camaraHoje != null) {
                    item(key = "camara_hoje") { CamaraHojeResumo(camaraHoje, state.projetosNovosHoje) }
                } else if (state.projetosNovosHoje >= 0) {
                    item(key = "camara_hoje") { CamaraHojeCard(state.projetosNovosHoje) }
                }
                if (painel != null && painel.votacoesCamara.isNotEmpty()) {
                    item(key = "label_votacoes") {
                        Nota("Últimas votações no Plenário da Câmara", Modifier.padding(top = 4.dp))
                    }
                    items(painel.votacoesCamara.take(6), key = { "v_" + it.data + it.descricao.hashCode() }) { VotacaoCard(it) }
                }
                painel?.senado?.let { senado ->
                    item(key = "senado") { SenadoCard(senado) }
                }

                if (painel != null && painel.creditos.isNotEmpty()) {
                    item(key = "label_creditos") {
                        SectionLabel("Dinheiro fora do Orçamento", Modifier.padding(top = 12.dp))
                    }
                    item(key = "creditos") { CreditosCard(painel.creditos) }
                }

                painel?.eleicao?.let { eleicao ->
                    item(key = "label_eleicao") {
                        SectionLabel("Eleição 2026", Modifier.padding(top = 12.dp))
                    }
                    item(key = "eleicao") { EleicaoCard(eleicao) }
                }

                if (painel != null && painel.noticias.isNotEmpty()) {
                    item(key = "label_noticias") {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            SectionLabel("Notícias oficiais do dia")
                            Nota("Agências públicas da Câmara, do Senado e da EBC. Toque pra ler na fonte.")
                        }
                    }
                    items(painel.noticias.take(10), key = { "n_" + it.link }) { NoticiaLinha(it) }
                }

                // The sourced punches: what people share
                item(key = "label_destaques") {
                    SectionLabel("Antes e agora", Modifier.padding(top = 12.dp))
                }
                val outrasVerdades = state.destaques.filter { it.id != verdade?.id }
                if (outrasVerdades.isEmpty()) {
                    item(key = "destaques_vazio") {
                        Text(
                            if (verdade != null) "Só uma verdade por hoje. Já basta."
                            else "Sem destaques curados no momento. Eles chegam por atualização remota, sempre com fonte.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                } else {
                    items(outrasVerdades, key = { it.id }) { destaque ->
                        DestaqueCard(destaque)
                    }
                }

                if (painel != null) {
                    item(key = "painel_atualizado") {
                        AtualizadoEm(painel.atualizadoEm, "Painel", Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}

/**
 * O soco de abertura: a verdade do dia com carimbo, número e veredito.
 *
 * Nada aqui é novo dado: o item vem do feed curado (com fonte, data e link
 * obrigatórios) e o veredito ou é curado junto ou é a leitura de regra do
 * Alma. O rótulo "na prática" existe justamente pra separar as duas coisas.
 */
@Composable
private fun VerdadeDoDia(
    item: FeedItemEntity,
    ofensiva: Int,
    vistas: Int,
    onLida: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(item.id) { onLida() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Red600.copy(alpha = 0.07f))
            .border(1.dp, Red600.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "A VERDADE DE HOJE",
                    color = Red600,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                if (item.selo.isNotBlank()) {
                    Selo(item.selo, Modifier.padding(top = 8.dp, bottom = 4.dp))
                }
                if (item.titulo.isNotBlank()) {
                    Text(
                        item.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (item.numero.isNotBlank()) {
                    Text(
                        semQuebraNoValor(item.numero),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = Red600,
                        modifier = Modifier.padding(top = 4.dp)
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
                            dataReferencia = formatIsoDate(item.dataReferencia),
                            selo = item.selo,
                            veredito = item.veredito
                        ),
                        legenda = listOf(
                            item.titulo,
                            item.veredito.takeIf { it.isNotBlank() },
                            "Fonte: ${item.fonte} (${formatIsoDate(item.dataReferencia)})",
                            item.fonteUrl
                        ).filterNotNull().filter { it.isNotBlank() }.joinToString("\n")
                    )
                }
            }) {
                Icon(Icons.Default.Share, contentDescription = "Compartilhar a verdade", tint = OnSurfaceVariant)
            }
        }

        if (item.veredito.isNotBlank()) {
            Veredito(item.veredito, Modifier.padding(top = 10.dp))
        }

        Text(
            item.texto,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurface,
            modifier = Modifier.padding(top = 8.dp)
        )

        FonteLinha(
            fonte = item.fonte,
            url = item.fonteUrl,
            dataReferencia = formatIsoDate(item.dataReferencia),
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 6.dp)
        ) {
            if (ofensiva > 0) {
                Text(
                    if (ofensiva == 1) "1 dia encarando" else "$ofensiva dias seguidos encarando",
                    color = Amber500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (vistas > 0) {
                Text(
                    "$vistas verdades lidas",
                    color = OnSurfaceFaint,
                    fontSize = 11.sp
                )
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
                    if (item.selo.isNotBlank()) {
                        Selo(item.selo, Modifier.padding(bottom = 6.dp))
                    }
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
                                dataReferencia = formatIsoDate(item.dataReferencia),
                                selo = item.selo,
                                veredito = item.veredito
                            ),
                            legenda = listOf(
                                item.titulo,
                                item.veredito.takeIf { it.isNotBlank() },
                                "Fonte: ${item.fonte} (${formatIsoDate(item.dataReferencia)})",
                                item.fonteUrl
                            ).filterNotNull().filter { it.isNotBlank() }.joinToString("\n")
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
            if (item.veredito.isNotBlank()) {
                Veredito(item.veredito, Modifier.padding(top = 10.dp))
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
