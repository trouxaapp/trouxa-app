package com.trouxaapp.app.ui.screens.sobre

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.ui.components.SectionLabel
import com.trouxaapp.app.ui.theme.Background
import com.trouxaapp.app.ui.theme.OnSurface
import com.trouxaapp.app.ui.theme.OnSurfaceFaint

// Full policy text lives here as constants so the screen works offline.
// The same text is published as PRIVACY.md in the public repo; keep both in sync.

private const val ULTIMA_ATUALIZACAO = "Última atualização: 10 de junho de 2026"

private const val INTRO =
    "O Trouxa App foi feito para funcionar sem coletar nada sobre você. " +
        "Esta política explica, em linguagem direta, o que fica no aparelho, " +
        "o que trafega pela rede e quais são os seus direitos pela Lei Geral " +
        "de Proteção de Dados Pessoais (LGPD, Lei 13.709/2018)."

private data class SecaoPolitica(val titulo: String, val corpo: String)

private val SECOES = listOf(
    SecaoPolitica(
        "O que fica salvo no seu aparelho",
        "O app guarda apenas três coisas, todas no armazenamento local do celular:\n\n" +
            "• o estado (UF) que você escolhe, opcional, usado só para filtrar deputados;\n" +
            "• suas preferências de notificação;\n" +
            "• um cache de dados públicos: projetos de lei, indicadores econômicos e " +
            "despesas parlamentares, tudo informação pública oficial.\n\n" +
            "Nada disso sai do aparelho."
    ),
    SecaoPolitica(
        "O que o app não faz",
        "• não pede conta nem cadastro;\n" +
            "• não usa analytics nem rastreadores;\n" +
            "• não exibe anúncios;\n" +
            "• não embute SDKs de terceiros que coletam dados;\n" +
            "• não envia nenhum identificador pessoal para servidor algum."
    ),
    SecaoPolitica(
        "Conexões de rede",
        "Para buscar dados públicos, o app se conecta apenas a estes endereços:\n\n" +
            "• dadosabertos.camara.leg.br (Câmara dos Deputados);\n" +
            "• api.bcb.gov.br (Banco Central);\n" +
            "• raw.githubusercontent.com (conteúdo curado do projeto).\n\n" +
            "Todas as requisições são somente de leitura de dados públicos. " +
            "Nenhum dado pessoal é enviado nessas conexões."
    ),
    SecaoPolitica(
        "Notificações",
        "As notificações são geradas localmente, no próprio aparelho, a partir dos " +
            "dados públicos já baixados. Não existe servidor de notificações do app."
    ),
    SecaoPolitica(
        "Como apagar seus dados",
        "Você pode apagar tudo a qualquer momento em Ajustes > Apagar dados locais, " +
            "ou simplesmente desinstalando o app. Como nada é enviado para fora do " +
            "aparelho, não existe cópia remota para apagar."
    ),
    SecaoPolitica(
        "Seus direitos pela LGPD",
        "A LGPD (Lei 13.709/2018) garante a você direitos como confirmação de " +
            "tratamento, acesso, correção e eliminação de dados pessoais. Como o app " +
            "não coleta dados pessoais, a maior parte desses direitos se resolve no " +
            "próprio aparelho, sob seu controle. Ainda assim, qualquer dúvida ou " +
            "pedido sobre privacidade pode ser enviado para trouxaapp@proton.me."
    ),
    SecaoPolitica(
        "Mudanças nesta política",
        "Se esta política mudar, a nova versão será publicada no repositório público " +
            "do projeto e dentro do app, sempre com a data de atualização visível."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacidadeScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "PRIVACIDADE",
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
            Text(
                "Política de privacidade",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                ULTIMA_ATUALIZACAO,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceFaint,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                INTRO,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface,
                modifier = Modifier.padding(top = 16.dp)
            )

            SECOES.forEach { secao ->
                Spacer(Modifier.height(28.dp))
                SectionLabel(secao.titulo)
                Text(
                    secao.corpo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
