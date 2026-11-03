package com.trouxaapp.app.ui.screens.leis

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.trouxaapp.app.data.local.LeiEntity
import com.trouxaapp.app.data.remote.Tramitacao
import com.trouxaapp.app.data.repository.LeisRepository
import com.trouxaapp.app.ui.components.AtualizadoEm
import com.trouxaapp.app.ui.components.FonteLinha
import com.trouxaapp.app.ui.components.SectionLabel
import com.trouxaapp.app.ui.components.TemaChip
import com.trouxaapp.app.ui.components.TemperaturaBadge
import com.trouxaapp.app.ui.components.formatIsoDate
import com.trouxaapp.app.ui.theme.Background
import com.trouxaapp.app.ui.theme.CardBorder
import com.trouxaapp.app.ui.theme.Green400
import com.trouxaapp.app.ui.theme.OnBackground
import com.trouxaapp.app.ui.theme.OnSurface
import com.trouxaapp.app.ui.theme.OnSurfaceFaint
import com.trouxaapp.app.ui.theme.OnSurfaceVariant
import com.trouxaapp.app.ui.theme.Red400
import com.trouxaapp.app.util.LawClassifier
import com.trouxaapp.app.util.ShareCard
import kotlinx.coroutines.launch

/**
 * Full bill page. Everything rendered here is either raw Camara data,
 * a documented derivation (titulo, temperatura) or sourced curation.
 * Missing data is stated as missing, never substituted.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeiDetailScreen(
    onBack: () -> Unit,
    viewModel: LeiDetailViewModel = hiltViewModel()
) {
    val leiState by viewModel.lei.collectAsStateWithLifecycle()
    val refreshing by viewModel.refreshing.collectAsStateWithLifecycle()

    val lei = leiState
    if (lei == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }

    val motivos = remember(lei.temperaturaMotivosJson) {
        parseList<String>(gson, lei.temperaturaMotivosJson)
    }
    val autores = remember(lei.autoresJson) {
        parseList<LeisRepository.AutorInfo>(gson, lei.autoresJson)
    }
    val tramitacoes = remember(lei.tramitacoesJson) {
        parseList<Tramitacao>(gson, lei.tramitacoesJson)
            .sortedByDescending { it.dataHora ?: "" }
    }
    val pros = remember(lei.curadoriaProsJson) {
        parseList<String>(gson, lei.curadoriaProsJson)
    }
    val contras = remember(lei.curadoriaContrasJson) {
        parseList<String>(gson, lei.curadoriaContrasJson)
    }
    val fontes = remember(lei.curadoriaFontesJson) {
        parseList<LeisRepository.FonteCuradoria>(gson, lei.curadoriaFontesJson)
    }
    val tipoExplicacao = remember(lei.siglaTipo, lei.ementa) {
        LawClassifier.classify(lei.siglaTipo, lei.ementa).tipoExplicacao
    }

    val identificacao = "${lei.siglaTipo} ${lei.numero}/${lei.ano}"
    val fichaUrl = "https://www.camara.leg.br/proposicoesWeb/fichadetramitacao?idProposicao=${lei.plId}"
    val titulo = lei.tituloPopular.ifBlank { lei.ementa }
    // Email bodies want a short label, not a whole ementa paragraph
    val tituloCurto = if (titulo.length > 120) titulo.take(119).trimEnd() + "…" else titulo

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text(identificacao, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val situacao = lei.ultimaSituacao
                            .ifBlank { lei.ultimoStatusDescricao }
                            .ifBlank { "sem situação registrada" }
                        val legenda = buildString {
                            append(titulo)
                            append("\nSituação: ")
                            append(situacao)
                            if (lei.ultimoStatusData.isNotBlank()) {
                                append(" (${formatIsoDate(lei.ultimoStatusData)})")
                            }
                            append("\nFicha oficial: $fichaUrl")
                            append("\nvia Trouxa App")
                        }
                        scope.launch {
                            ShareCard.compartilhar(
                                context,
                                ShareCard.Conteudo(
                                    rotulo = lei.tema.ifBlank { "Projeto na Câmara" },
                                    numero = identificacao,
                                    titulo = titulo,
                                    texto = textoDoCard(autores, situacao),
                                    fonte = "Câmara dos Deputados, dados abertos",
                                    fonteUrl = fichaUrl,
                                    dataReferencia = formatIsoDate(
                                        lei.ultimoStatusData.ifBlank { lei.dataApresentacao }
                                    )
                                ),
                                legenda = legenda
                            )
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartilhar como imagem")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = OnBackground,
                    navigationIconContentColor = OnBackground,
                    actionIconContentColor = OnBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {

            // Title + classification chips
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(titulo, style = MaterialTheme.typography.headlineMedium, color = OnBackground)
                if (lei.tema.isNotBlank() || lei.temperatura >= 40) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (lei.tema.isNotBlank()) TemaChip(lei.tema)
                        TemperaturaBadge(lei.temperatura)
                    }
                }
            }

            // Why the activity badge exists, fact by fact
            if (motivos.isNotEmpty()) {
                Column {
                    SectionLabel("Por que está em movimento")
                    BulletList(motivos, bulletColor = OnSurfaceVariant)
                    Caption(
                        "Critérios objetivos calculados a partir da tramitação oficial.",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            if (lei.ementa.isNotBlank()) {
                Column {
                    SectionLabel("O que o projeto diz")
                    Text(lei.ementa, style = MaterialTheme.typography.bodyLarge, color = OnSurface)
                    if (lei.ementaDetalhada.isNotBlank()) {
                        Text(
                            lei.ementaDetalhada,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            if (lei.justificativa.isNotBlank()) {
                Column {
                    SectionLabel("Por que existe, segundo o autor")
                    Text(lei.justificativa, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                    Caption(
                        "Texto do próprio autor, enviado à Câmara.",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            if (autores.isNotEmpty()) {
                Column {
                    SectionLabel("Quem assina")
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        autores.forEach { autor -> AutorRow(autor) }
                    }
                }
            }

            // One-tap pressure. Every message is pre-written, polite and
            // editable: it asks for a position on a public bill, nothing else.
            Column {
                SectionLabel("Cobrar quem decide")
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (lei.autorEmail.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                val assunto = "Sobre o $identificacao"
                                val corpo = "Deputado(a) ${lei.autorNome},\n\n" +
                                    "Sou eleitor(a) e estou acompanhando o $identificacao: $tituloCurto.\n\n" +
                                    "Queria entender a sua posição sobre esse projeto e o que ele " +
                                    "muda na prática pra quem paga a conta.\n\n" +
                                    "Aguardo retorno."
                                val mailto = "mailto:${Uri.encode(lei.autorEmail)}" +
                                    "?subject=${Uri.encode(assunto)}&body=${Uri.encode(corpo)}"
                                runCatching {
                                    context.startActivity(
                                        Intent(Intent.ACTION_SENDTO).apply { data = Uri.parse(mailto) }
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Outlined.Email,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Cobrar por e-mail")
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            // Plain-text name only, never a guessed handle:
                            // tagging the wrong account is worse than no tag.
                            val pergunta = if (lei.autorEmail.isNotBlank()) {
                                "Deputado(a) ${lei.autorNome}, qual a sua posição sobre o " +
                                    "$identificacao? Estou acompanhando. $fichaUrl"
                            } else {
                                "A quem decide: qual a posição sobre o $identificacao? $fichaUrl"
                            }
                            runCatching {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(
                                            "https://twitter.com/intent/tweet?text=${Uri.encode(pergunta)}"
                                        )
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.AlternateEmail,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Perguntar no X")
                    }
                    OutlinedButton(
                        onClick = {
                            val texto = "$identificacao: $titulo. Ficha oficial: $fichaUrl"
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, texto)
                            }
                            runCatching { context.startActivity(Intent.createChooser(send, null)) }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Mandar pra alguém")
                    }
                }
                Caption(
                    "Mensagens prontas e educadas. Dá pra revisar tudo antes de enviar.",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Current status, always rendered: missing movement is information too
            Column {
                SectionLabel("Onde está agora")
                StatusAtual(lei, refreshing)
                if (tramitacoes.isNotEmpty()) {
                    Text(
                        "Últimas movimentações registradas",
                        color = OnSurfaceFaint,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                    tramitacoes.forEach { tram -> TramitacaoRow(tram) }
                }
            }

            if (lei.curadoriaResumo.isNotBlank()) {
                Column {
                    SectionLabel("Análise da curadoria")
                    Text(lei.curadoriaResumo, style = MaterialTheme.typography.bodyLarge, color = OnSurface)
                    if (pros.isNotEmpty()) {
                        Text(
                            "Pontos a favor",
                            style = MaterialTheme.typography.labelLarge,
                            color = OnSurface,
                            modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
                        )
                        BulletList(pros, bulletColor = Green400)
                    }
                    if (contras.isNotEmpty()) {
                        Text(
                            "Pontos contra",
                            style = MaterialTheme.typography.labelLarge,
                            color = OnSurface,
                            modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
                        )
                        BulletList(contras, bulletColor = Red400)
                    }
                    if (lei.curadoriaContexto.isNotBlank()) {
                        Text(
                            lei.curadoriaContexto,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(top = 14.dp)
                        )
                    }
                    if (fontes.isNotEmpty()) {
                        Spacer(Modifier.height(14.dp))
                        SectionLabel("Fontes")
                        fontes.forEach { fonte ->
                            FonteLinha(
                                fonte = fonte.titulo?.takeIf { it.isNotBlank() } ?: "Fonte",
                                url = fonte.url ?: ""
                            )
                        }
                    }
                    Caption(
                        "Análise editorial humana, sempre com fontes. Achou um erro? Fala com a gente em Ajustes > Sobre.",
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            } else {
                Caption(
                    "Esse projeto ainda não tem análise da curadoria. Tudo nessa tela vem direto dos dados oficiais da Câmara."
                )
            }

            if (tipoExplicacao.isNotBlank()) {
                Column {
                    SectionLabel("Como funciona")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            tipoExplicacao,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            // Actions: always point at the official record, never at us
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fichaUrl)))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Ver ficha oficial na Câmara")
                }
                if (lei.urlInteiroTeor.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            runCatching {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(lei.urlInteiroTeor))
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Ler texto completo (inteiro teor)")
                    }
                }
            }
        }
    }
}

// Who signed and where it stands, in plain words. Names come straight from
// the Camara author list; nothing is inferred about intent.
private fun textoDoCard(autores: List<LeisRepository.AutorInfo>, situacao: String): String {
    val assinatura = autores.firstOrNull()?.let { a ->
        val sigla = listOf(a.partido, a.uf).filter { it.isNotBlank() }.joinToString("-")
        val nome = if (sigla.isNotBlank()) "${a.nome} ($sigla)" else a.nome
        when (val outros = autores.size - 1) {
            0 -> "Quem assina: $nome."
            1 -> "Quem assina: $nome e mais 1."
            else -> "Quem assina: $nome e mais $outros."
        }
    }.orEmpty()
    return listOf(assinatura, "Onde está: ${situacao.trimEnd('.')}.")
        .filter { it.isNotBlank() }
        .joinToString("\n")
}

@Composable
private fun StatusAtual(lei: LeiEntity, refreshing: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (lei.ultimaSituacao.isBlank() && lei.ultimoStatusDescricao.isBlank()) {
            Text(
                "Sem movimentação registrada.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
        } else {
            if (lei.ultimaSituacao.isNotBlank()) {
                Text(
                    lei.ultimaSituacao,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            }
            if (lei.ultimoStatusDescricao.isNotBlank()) {
                Text(
                    lei.ultimoStatusDescricao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
            if (lei.ultimoStatusData.isNotBlank()) {
                Text(
                    formatIsoDate(lei.ultimoStatusData),
                    color = OnSurfaceFaint,
                    fontSize = 11.sp
                )
            }
        }
        AtualizadoEm(lei.tramitacaoAtualizadaEm, "Dados da Câmara")
        if (refreshing) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = OnSurfaceFaint
                )
                Spacer(Modifier.width(8.dp))
                Text("verificando agora…", color = OnSurfaceFaint, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun TramitacaoRow(tram: Tramitacao) {
    val data = formatIsoDate(tram.dataHora ?: "")
    val descricao = tram.descricaoTramitacao ?: ""
    if (data.isBlank() && descricao.isBlank()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(CardBorder)
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            if (data.isNotBlank()) {
                Text(data, color = OnSurfaceFaint, fontSize = 11.sp)
            }
            if (descricao.isNotBlank()) {
                Text(descricao, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
            }
        }
    }
}

@Composable
private fun AutorRow(autor: LeisRepository.AutorInfo) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (autor.foto.isNotBlank()) {
            AsyncImage(
                model = autor.foto,
                contentDescription = "Foto de ${autor.nome}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CardBorder)
            )
            Spacer(Modifier.width(12.dp))
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    autor.nome,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                if (autor.partido.isNotBlank()) {
                    val sigla = if (autor.uf.isNotBlank()) "${autor.partido}-${autor.uf}" else autor.partido
                    Text(
                        "($sigla)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
            if (autor.tipo.isNotBlank() && autor.tipo != "Deputado(a)" && autor.tipo != "Deputado") {
                Text(autor.tipo, color = OnSurfaceFaint, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun BulletList(items: List<String>, bulletColor: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { item ->
            Row {
                Text("•", style = MaterialTheme.typography.bodyMedium, color = bulletColor)
                Spacer(Modifier.width(8.dp))
                Text(item, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
            }
        }
    }
}

@Composable
private fun Caption(texto: String, modifier: Modifier = Modifier) {
    Text(texto, color = OnSurfaceFaint, fontSize = 11.sp, lineHeight = 15.sp, modifier = modifier)
}

// Gson swallows malformed JSON here on purpose: a broken stored blob
// renders as an omitted section, never as a crash or invented content.
private inline fun <reified T> parseList(gson: Gson, json: String): List<T> {
    if (json.isBlank()) return emptyList()
    return try {
        val type = TypeToken.getParameterized(List::class.java, T::class.java).type
        gson.fromJson<List<T>>(json, type) ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }
}
