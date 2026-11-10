package com.trouxaapp.app.ui.screens.hoje

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.data.painel.CamaraHoje
import com.trouxaapp.app.data.painel.CreditoAno
import com.trouxaapp.app.data.painel.Eleicao
import com.trouxaapp.app.data.painel.Indicador
import com.trouxaapp.app.data.painel.ItemPreco
import com.trouxaapp.app.data.painel.Noticia
import com.trouxaapp.app.data.painel.PainelParsers
import com.trouxaapp.app.data.painel.Precos
import com.trouxaapp.app.data.painel.SenadoDia
import com.trouxaapp.app.data.painel.Votacao
import com.trouxaapp.app.ui.components.FonteLinha
import com.trouxaapp.app.ui.components.Veredito
import com.trouxaapp.app.ui.components.formatIsoDate
import com.trouxaapp.app.ui.theme.Blue400
import com.trouxaapp.app.ui.theme.CardBorder
import com.trouxaapp.app.ui.theme.Green500
import com.trouxaapp.app.ui.theme.OnBackground
import com.trouxaapp.app.ui.theme.OnSurface
import com.trouxaapp.app.ui.theme.OnSurfaceFaint
import com.trouxaapp.app.ui.theme.OnSurfaceVariant
import com.trouxaapp.app.ui.theme.Red400
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.ui.theme.Surface
import com.trouxaapp.app.ui.theme.SurfaceVariant
import com.trouxaapp.app.util.Alma
import com.trouxaapp.app.util.LeiExplicada
import java.text.SimpleDateFormat
import java.util.Locale

/*
 * Building blocks of the daily panel. Colors carry direction only (up,
 * down, unchanged), never a verdict: a rising dollar and a rising stock
 * index get the same arrow.
 */

@Composable
internal fun PainelCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
internal fun Nota(texto: String, modifier: Modifier = Modifier) {
    Text(texto, style = MaterialTheme.typography.bodySmall, color = OnSurfaceFaint, modifier = modifier)
}

@Composable
internal fun IndicadorPainelCard(ind: Indicador) {
    PainelCard {
        Text(ind.nome, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        Text(
            ind.valor.replace("R$ ", "R$ "),
            style = MaterialTheme.typography.headlineMedium,
            color = OnBackground,
            modifier = Modifier.padding(top = 4.dp)
        )
        if (ind.mudanca.isNotBlank()) {
            Text(
                "${seta(ind.direcao)} ${ind.mudanca}",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        FonteLinha(ind.fonte, ind.fonteUrl, ind.dataReferencia, Modifier.padding(top = 6.dp))
    }
}

private fun seta(direcao: Int) = when {
    direcao > 0 -> "▲"
    direcao < 0 -> "▼"
    else -> "="
}

@Composable
internal fun PrecosCard(p: Precos) {
    var tudo by rememberSaveable { mutableStateOf(false) }
    PainelCard(Modifier.animateContentSize()) {
        Text("Inflação oficial de ${p.mes}", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        Text(
            PainelParsers.pct(p.indiceGeral),
            style = MaterialTheme.typography.headlineMedium,
            color = OnBackground,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            "Média de tudo que o IBGE acompanha. Por item, a conta muda muito:",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurface,
            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
        )
        p.altas.maxByOrNull { it.variacao }?.let { pior ->
            Veredito(
                "O que mais pesou no seu mês foi ${pior.nome.lowercase(Locale("pt", "BR"))}: " +
                    "${PainelParsers.pct(pior.variacao)}. Não é estimativa, é a medição oficial do IBGE.",
                Modifier.padding(bottom = 10.dp)
            )
        }
        Text("Mais subiram no mês", style = MaterialTheme.typography.titleSmall, color = OnBackground)
        val maior = (p.altas.maxOfOrNull { it.variacao } ?: 1.0)
        (if (tudo) p.altas else p.altas.take(5)).forEach { BarraPreco(it, maior, Red600) }
        if (p.quedas.isNotEmpty()) {
            Text(
                "Mais caíram",
                style = MaterialTheme.typography.titleSmall,
                color = OnBackground,
                modifier = Modifier.padding(top = 12.dp)
            )
            val menor = -(p.quedas.minOfOrNull { it.variacao } ?: -1.0)
            (if (tudo) p.quedas else p.quedas.take(3)).forEach { BarraPreco(it, menor, Green500) }
        }
        Text(
            if (tudo) "mostrar menos" else "ver mais itens",
            style = MaterialTheme.typography.labelLarge,
            color = OnSurfaceVariant,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable { tudo = !tudo }
                .padding(vertical = 4.dp)
        )
        Nota("O IBGE mede quanto subiu, não diz por quê. Causa só entra aqui quando vem de fonte oficial.", Modifier.padding(top = 6.dp))
        FonteLinha(p.fonte, p.fonteUrl, p.mes, Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun BarraPreco(item: ItemPreco, escala: Double, cor: Color) {
    Column(modifier = Modifier.padding(top = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(item.nome, style = MaterialTheme.typography.bodyMedium, color = OnSurface, modifier = Modifier.weight(1f))
            Text(
                (if (item.variacao > 0) "+" else "") + PainelParsers.pct(item.variacao),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                color = OnSurface
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 3.dp)
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(SurfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((kotlin.math.abs(item.variacao) / escala).toFloat().coerceIn(0.02f, 1f))
                    .height(4.dp)
                    .background(cor)
            )
        }
    }
}

@Composable
internal fun CamaraHojeResumo(c: CamaraHoje, projetosNovos: Int) {
    PainelCard {
        Text("Câmara hoje", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        Text("Plenário: ${c.plenario}", style = MaterialTheme.typography.bodyLarge, color = OnBackground, modifier = Modifier.padding(top = 4.dp))
        Text(
            buildString {
                append("${c.reunioes} ${if (c.reunioes == 1) "reunião" else "reuniões"} de comissão")
                if (c.canceladas > 0) append(", ${c.canceladas} cancelada${if (c.canceladas > 1) "s" else ""}")
                if (projetosNovos >= 0) append(". ${projetosNovos} projeto${if (projetosNovos == 1) "" else "s"} apresentado${if (projetosNovos == 1) "" else "s"} hoje")
                append(".")
            },
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
        FonteLinha("Câmara dos Deputados, agenda", "https://www.camara.leg.br/agenda", modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
internal fun VotacaoCard(v: Votacao) {
    val context = LocalContext.current
    var aberto by rememberSaveable(v.descricao + v.data) { mutableStateOf(false) }
    PainelCard(Modifier.animateContentSize()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Etiqueta(
                when (v.aprovada) { true -> "APROVADO"; false -> "REJEITADO"; null -> "SEM RESULTADO" },
                if (v.aprovada == false) Red400 else if (v.aprovada == true) Green500 else OnSurfaceVariant
            )
            Text(formatIsoDate(v.data), style = MaterialTheme.typography.labelSmall, color = OnSurfaceFaint)
        }
        val ementa = v.ementa
        if (!ementa.isNullOrBlank()) {
            val frase = remember(ementa) { LeiExplicada.explicar(v.siglaTipo.orEmpty(), ementa).emUmaFrase }
            Text(
                v.proposicao.orEmpty(),
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                frase.ifBlank { ementa },
                style = MaterialTheme.typography.titleSmall,
                color = OnBackground,
                // Raw ementas can run 10+ lines; same 4-line cap as the site
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Text(
            v.descricao.substringBefore(" Sim:").trim(),
            style = if (ementa.isNullOrBlank()) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            color = if (ementa.isNullOrBlank()) OnBackground else OnSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp)
        )
        if (v.placar.isNotBlank()) {
            Text(v.placar, style = MaterialTheme.typography.titleSmall, fontFamily = FontFamily.Monospace, color = OnSurface, modifier = Modifier.padding(top = 4.dp))
        }
        if (v.orientacoes.isNotEmpty()) {
            Text(
                if (aberto) "Como cada partido orientou:" else "Ver como cada partido orientou",
                style = MaterialTheme.typography.labelLarge,
                color = OnSurfaceVariant,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clickable { aberto = !aberto }
                    .padding(vertical = 4.dp)
            )
            if (aberto) {
                Text(v.orientacoes.joinToString("   "), style = MaterialTheme.typography.bodySmall, color = OnSurface)
            }
        }
        if (v.url.isNotBlank()) {
            Text(
                "Abrir a sessão na Câmara",
                style = MaterialTheme.typography.bodySmall,
                color = Blue400,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clickable { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(v.url))) } }
            )
        }
    }
}

@Composable
private fun Etiqueta(texto: String, cor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(cor.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(texto, color = cor, fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
internal fun SenadoCard(s: SenadoDia) {
    var tudo by rememberSaveable { mutableStateOf(false) }
    PainelCard(Modifier.animateContentSize()) {
        Text("Senado, ${s.sessao.lowercase(Locale("pt", "BR"))} de ${s.data}", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        (if (tudo) s.itens else s.itens.take(3)).forEach { item ->
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(item.identificacao, style = MaterialTheme.typography.titleSmall, color = OnBackground)
                Text(
                    item.ementa,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    maxLines = if (tudo) 6 else 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(item.resultado, style = MaterialTheme.typography.bodyMedium, color = OnSurface, modifier = Modifier.padding(top = 2.dp))
            }
        }
        if (s.itens.size > 3) {
            Text(
                if (tudo) "mostrar menos" else "ver os ${s.itens.size} itens",
                style = MaterialTheme.typography.labelLarge,
                color = OnSurfaceVariant,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { tudo = !tudo }
                    .padding(vertical = 4.dp)
            )
        }
        FonteLinha("Senado Federal, dados abertos", "https://www25.senado.leg.br/web/atividade/plenario", s.data, Modifier.padding(top = 6.dp))
    }
}

@Composable
internal fun CreditosCard(creditos: List<CreditoAno>) {
    PainelCard {
        Text("Crédito extraordinário aberto por medida provisória, por ano", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        Text(
            "Dinheiro liberado fora do Orçamento aprovado, que começa a ser gasto antes de o Congresso votar. A Constituição só permite para despesa urgente e imprevisível.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurface,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )
        // O total do ano mais recente com dado, dito em voz alta
        creditos.maxByOrNull { it.ano }?.let { atual ->
            if (atual.quantidade > 0) {
                Veredito(
                    Alma.vereditoCredito(atual.ano, atual.total, atual.quantidade),
                    Modifier.padding(bottom = 10.dp)
                )
            }
        }
        val maior = creditos.maxOfOrNull { it.total }?.takeIf { it > 0 } ?: 1.0
        creditos.sortedByDescending { it.ano }.forEach { c ->
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${c.ano}" + if (c.presidencia.isNotBlank()) " · ${c.presidencia}" else "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        if (c.quantidade == 0) "nenhuma" else "${PainelParsers.bilhoes(c.total)} em ${c.quantidade} MP${if (c.quantidade > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = OnSurface
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(SurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((c.total / maior).toFloat().coerceIn(0.01f, 1f))
                            .height(6.dp)
                            .background(Red600)
                    )
                }
            }
        }
        Nota(
            "Soma dos valores escritos nas ementas das MPs \"Abre crédito extraordinário\". Valores da época, sem correção pela inflação. O ano corrente ainda está aberto.",
            Modifier.padding(top = 10.dp)
        )
        FonteLinha(
            "Câmara dos Deputados, dados abertos",
            "https://www.camara.leg.br/busca-portal/proposicoes/pesquisa-simplificada",
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
internal fun EleicaoCard(e: Eleicao) {
    PainelCard {
        Text("Resultado oficial: ${e.titulo}", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        e.candidatos.forEach { c ->
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${c.nome} (${c.partido})",
                        style = MaterialTheme.typography.titleSmall,
                        color = OnBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${c.percentual}%", style = MaterialTheme.typography.titleSmall, fontFamily = FontFamily.Monospace, color = OnBackground)
                }
                Text(
                    "%,d votos".format(Locale("pt", "BR"), c.votos) + if (c.eleito) " · eleito" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }
        Nota("Percentual dos votos válidos. Totalização de ${e.dataTotalizacao}.", Modifier.padding(top = 8.dp))
        FonteLinha("Tribunal Superior Eleitoral", e.fonteUrl, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
internal fun NoticiaLinha(n: Noticia) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(n.link))) } }
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Text(
            buildString {
                append(n.fonte)
                if (n.publicadaEm > 0) append(" · ").append(horario(n.publicadaEm))
            },
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceFaint
        )
        Text(n.titulo, style = MaterialTheme.typography.bodyLarge, color = OnBackground, modifier = Modifier.padding(top = 2.dp))
        if (n.resumo.isNotBlank()) {
            Text(
                n.resumo,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

private fun horario(ts: Long): String {
    val hoje = SimpleDateFormat("yyyyMMdd", Locale.US).format(System.currentTimeMillis())
    val dia = SimpleDateFormat("yyyyMMdd", Locale.US).format(ts)
    return if (dia == hoje) "hoje, " + SimpleDateFormat("HH:mm", Locale.US).format(ts)
    else SimpleDateFormat("dd/MM, HH:mm", Locale.US).format(ts)
}
