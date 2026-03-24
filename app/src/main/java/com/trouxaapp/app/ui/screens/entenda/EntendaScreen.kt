package com.trouxaapp.app.ui.screens.entenda

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.ui.theme.Green500
import com.trouxaapp.app.ui.theme.Red600

data class Conceito(val titulo: String, val explicacao: String)

private val conceitos = listOf(
    // Original 10
    Conceito("O que é IPCA?",
        "O numero que o governo usa pra dizer quanto a inflação subiu. Problema: ele pondera com eletrônicos (que caem) e serviços. Por isso o IPCA diz 5% mas no mercado você sente 30%. Quem calcula: IBGE. Na prática: quando o governo diz que a inflação está controlada, olhe pro preço do arroz."),
    Conceito("O que é Selic?",
        "A taxa de juros que o Banco Central define. Quando sobe: financiamento fica mais caro, crédito encarece, economia desacelera. Quando desce: dólar pode subir, tudo importado fica caro. Quem decide: COPOM (8 diretores do BC, indicados pelo presidente). Na prática: afeta seu cartão de crédito, financiamento e até o preço da comida."),
    Conceito("O que é PPI?",
        "Preço de Paridade de Importação. Criado por Pedro Parente na Petrobras em 2016 (governo Temer). Significa: você paga gasolina como se fosse importada, mesmo o petróleo sendo extraído aqui. Em dólar. Nenhum governo depois desfez."),
    Conceito("O que é CEAP?",
        "Cota para Exercício da Atividade Parlamentar. O \"cartão corporativo\" do deputado. Até R\$ 57 mil por mês. Pra: passagem aérea, alimentação, combustível, hospedagem, divulgação. Pode gastar com \"divulgação\" e ninguém sabe o quê divulgou."),
    Conceito("O que é cestá básica?",
        "Lista de 13 alimentos essenciais calculada pelo DIEESE todo mês. É o mínimo pra uma pessoa adulta comer. Quando a cestá custa 59% do salário mínimo (como em 2026), significa que mais da metade do que você ganha vai SÓ pra comer. O básico."),
    Conceito("O que é o Teto de Gastos?",
        "Regra que congelou investimento do governo em saúde e educação por 20 anos. Criada em 2016 (governo Temer, PEC 95). A população cresce, o investimento por pessoa DIMINUI. Substituída pelo novo arcabouço fiscal em 2023 (governo Lula). Mas o estrago de 7 anos já foi feito."),
    Conceito("O que é Orçamento Secreto?",
        "R\$ 53 bilhões distribuídos pra deputados e senadores sem transparência (2020-2022). Declarado inconstitucional pelo STF em dezembro de 2022. Mesmo assim, emendas paralelas (Emendas Pix) continuam contornando a proibição. Ninguém foi preso."),
    Conceito("O que é arcabouço fiscal?",
        "A regra que substituiu o Teto de Gastos em 2023. Limita o crescimento dos gastos do governo a 70% do crescimento da receita. Na teoria, evita descontrole. Na prática, a meta de déficit zero já foi flexibilizada no primeiro ano."),
    Conceito("O que é bandeira tarifária?",
        "Sistema criado em 2015 que cobra EXTRA na conta de luz quando falta chuva. Verde: sem extra. Amarela: R\$ 1,88/100kWh. Vermelha: até R\$ 7,87/100kWh. Em 2021, criaram a bandeira de \"escassez hídrica\": R\$ 14,20/100kWh. Quem criou: ANEEL. Você paga."),
    Conceito("O que é o salário necessário do DIEESE?",
        "Todo mês, o DIEESE calcula quanto uma família de 4 pessoas precisaria pra viver com dignidade. Em 2026: R$ 7.500. O salário mínimo é R\$ 1.518. Você recebe 20% do necessário. É a maior prova de que o salário mínimo não é suficiente."),
    // Legal terms
    Conceito("O que é PL?",
        "Projeto de Lei. Qualquer deputado pode criar. Precisa de maioria simples pra aprovar. A maioria morre na gaveta. O presidente da Câmara decide o que vai a voto. Na prática: deputados criam PLs pra parecer produtivos, mas poucos viram lei."),
    Conceito("O que é PEC?",
        "Proposta de Emenda Constitucional. Muda a Constituição. Precisa de 3/5 dos votos (308 deputados), em duas rodadas. É a arma mais pesada do Congresso. Exemplos: PEC 95 (teto de gastos), Reforma da Previdência, Reforma Tributária."),
    Conceito("O que é MP?",
        "Medida Provisória. O presidente cria sozinho e já vale na hora. O Congresso tem 120 dias pra aprovar ou caduca. Na prática, o presidente legisla sozinho. Se caduca, o que já aconteceu com base nela fica válido. É um poder enorme."),
    Conceito("O que é imposto sobre imposto?",
        "Gasolina: 44% é imposto. Luz: 30% é imposto. Carro: 30% é imposto. Cerveja: 56% é imposto. Você paga imposto no combustível pra ir trabalhar ganhar o salário que já teve imposto descontado pra comprar comida que tem imposto no preço."),
    Conceito("O que é a Reforma Tributária?",
        "Simplifica impostos mas pode encarecer serviços. IBS e CBS substituem PIS, COFINS, ICMS, ISS e IPI. Na teoria é melhor: menos burocracia. Na prática, serviços como internet, streaming e academia podem ficar mais caros. Comida básica fica isenta."),
    // NEW 15 concepts
    Conceito("O que é COPOM?",
        "Comitê de Política Monetária do Banco Central. São 9 diretores que se reúnem a cada 45 dias pra definir a taxa Selic. Quando sobem a Selic, seu financiamento fica mais caro. Quando descem, o dólar pode subir. Indicados pelo presidente, aprovados pelo Senado."),
    Conceito("O que é IGP-M?",
        "Índice que reajusta seu aluguel todo ano. Calculado pela FGV. Inclui preços de matérias-primas e dólar, então costuma subir mais que o IPCA. Em 2021 chegou a 37%. Seu aluguel subiu 37% num ano. Muitos contratos já trocaram pro IPCA, que é menor."),
    Conceito("Como funciona o Imposto de Renda?",
        "Você paga IR sobre o que ganha acima de R\$ 2.259/mês. As faixas não são corrigidas pela inflação desde 2015. Isso significa que todo ano mais gente paga IR sem ter ficado mais rica. É um aumento disfarçado de imposto. A tabela deveria ser corrigida em pelo menos 160%."),
    Conceito("O que é FGTS?",
        "Fundo de Garantia do Tempo de Serviço. Seu patrão deposita 8% do seu salário todo mês. Mas o FGTS rende 3% ao ano + TR, enquanto a inflação passa de 5%. Seu dinheiro perde valor parado lá. O governo usa o FGTS pra financiar habitação e saneamento. Com o SEU dinheiro, rendendo menos que a inflação."),
    Conceito("O que é a dívida pública?",
        "O governo gasta mais do que arrecada e pega emprestado. A dívida pública brasileira é de R\$ 8,8 trilhões (78% do PIB). Só de juros, o governo paga R\$ 800 bilhões por ano. Isso é MAIS do que gasta com saúde (R\$ 200 bi) e educação (R\$ 150 bi) SOMADOS."),
    Conceito("O que é PIB?",
        "Produto Interno Bruto. É tudo que o país produziu em um ano: bens, serviços, agricultura, indústria. Em 2025, o PIB do Brasil foi de R\$ 11 trilhões. Parece muito, mas dividido por 215 milhões de pessoas dá R\$ 51 mil per capita. A Noruega: R\$ 500 mil per capita."),
    Conceito("Por que o dólar afeta tudo?",
        "Gasolina, trigo, eletrônicos, remédios, fertilizantes: tudo cotado em dólar. Quando o dólar sobe, esses produtos ficam mais caros em real. Quem controla: o mercado, mas o Banco Central intervém. Desde 2020, o dólar não voltou abaixo de R\$ 5. Tudo ficou permanentemente mais caro."),
    Conceito("O que é privatização?",
        "Vender empresa pública pra iniciativa privada. Exemplos: Vale (1997, vendida por R\$ 3,3 bi, hoje vale R\$ 250 bi), Eletrobras (2022, R\$ 33 bi). Argumento a favor: eficiência. Argumento contra: patrimônio público vendido abaixo do valor. Na prática: depende. Telecomunicações melhoraram. Energia ficou mais cara."),
    Conceito("O que é a LAI?",
        "Lei de Acesso à Informação (Lei 12.527/2011). Qualquer cidadão pode pedir informação a qualquer órgão público. Prazo: 20 dias pra responder. Como usar: acesse falabr.cgu.gov.br, crie conta e faça seu pedido. É grátis e funciona. Notá de efetividade: 7/10."),
    Conceito("O que é o Portal da Transparência?",
        "Site do governo federal onde você vê pra onde vai o dinheiro público: salários de servidores, gastos com cartão corporativo, contratos, emendas parlamentares. Endereço: portaldatransparencia.gov.br. Funciona, mas a linguagem é técnica. O Trouxa traduz pra você."),
    Conceito("O que é o TCU?",
        "Tribunal de Contas da União. Fiscaliza como o governo gasta o dinheiro público. É o TCU que encontrou as 11.469 obras paradas. Tem poder de bloquear contas e multar gestores. Problema: é lento e as punições raramente são severas. Mas os relatórios são ouro pra quem quer dados."),
    Conceito("O que é o STF?",
        "Supremo Tribunal Federal. São 11 ministros que interpretam a Constituição. Indicados pelo presidente, aprovados pelo Senado, mandato vitalício. Decidem se leis são constitucionais, julgam autoridades com foro privilegiado. Custo: R\$ 600 milhões/ano só pro STF."),
    Conceito("O que é CPI?",
        "Comissão Parlamentar de Inquérito. Deputados ou senadores investigam um tema específico. Pode convocar testemunhas e quebrar sigilo bancário. Na prática: produz relatórios, mas não condena ninguém. A CPI da COVID recomendou 80 indiciamentos. Quantos foram presos? Zero."),
    Conceito("O que é impeachment?",
        "Processo pra tirar o presidente do cargo por crime de responsabilidade. Precisa de 2/3 da Câmara (342 votos) pra abrir e 2/3 do Senado (54 votos) pra condenar. Aconteceu 2 vezes: Collor (1992) e Dilma (2016). Na prática, é mais político do que jurídico. Depende da vontade do Congresso.")
)

private val tabs = listOf("Dicionário", "Leis", "Direitos", "Sua Vida")

@Composable
fun EntendaScreen(onBack: () -> Unit = {}, onCidadaoClick: () -> Unit = {}) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("ENTENDA", style = MaterialTheme.typography.headlineMedium, color = Red600, fontWeight = FontWeight.Black)
                Text("Sem economês. Como se explicasse pro seu amigo.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = Red600,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Red600
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                color = if (selectedTab == index) Red600 else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> DicionarioTab()
                1 -> LeisTab()
                2 -> DireitosTab(onCidadaoClick)
                3 -> SuaVidaTab()
            }
        }
    }
}

@Composable
private fun DicionarioTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
    ) {
        items(conceitos) { conceito ->
            ConceitoCard(conceito)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LeisTab() {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
    ) {
        item {
            Text("LEIS QUE TE FAZEM DE TROUXA", style = MaterialTheme.typography.titleMedium, color = Red600, fontWeight = FontWeight.Black)
            Text("Já aprovadas. Já te afetando.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))
        }
        items(DataProvider.leisAprovadas) { lei ->
            LeiAprovadaCard(lei)
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(16.dp))
            Text("QUEM MANDA EM VOCÊ", style = MaterialTheme.typography.titleMedium, color = Red600, fontWeight = FontWeight.Black)
            Text("E quanto custam pro seu bolso.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))
        }
        items(DataProvider.governantesAtuais) { gov ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(gov.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(gov.cargo, style = MaterialTheme.typography.bodySmall, color = Red600)
                    Text(gov.partido, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(gov.custoAnual, style = MaterialTheme.typography.bodySmall, color = Color(0xFFFBBF24))
                    if (gov.twitter.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        androidx.compose.material3.OutlinedButton(
                            onClick = {
                                val text = Uri.encode("${gov.twitter} Quanto você custou pro contribuinte esse mês? #TrouxaApp")
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text=$text")))
                            },
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Red600)
                        ) { Text("Cobrar: ${gov.twitter}", color = Red600, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun DireitosTab(onCidadaoClick: () -> Unit) {
    data class Direito(val nome: String, val lei: String, val naPratica: String, val nota: Int)
    val direitos = listOf(
        Direito("Saúde pública (SUS)", "Art. 196 CF: Saúde é direito de todos e dever do Estado.", "Fila de 2 anos pra cirurgia. 1,3 milhão aguardando. Orçamento per capita: R\$ 4 mil/ano (UK: R\$ 18 mil).", 3),
        Direito("Educação pública", "Art. 205 CF: Educação é direito de todos.", "3o pior entre 42 países na OCDE. Investimento de US\$ 3.668/aluno (OCDE: US\$ 11.914). Meta de 10% do PIB, resultado: 5%.", 4),
        Direito("Acesso à informação (LAI)", "Lei 12.527/2011: Qualquer pessoa pode pedir informação ao governo.", "Funciona pra pedidos simples (75% atendidos). Mas quando o governo não quer responder, 70% dos recursos são negados. Depende do que você quer saber.", 6),
        Direito("Defesa do consumidor (Procon)", "CDC, Lei 8.078/1990: Direitos do consumidor.", "Funciona pra casos simples (troca, devolução). Casos complexos vão pro Judiciário. SP resolve 29% dos casos.", 5),
        Direito("Denúncia ao Ministério Público", "Art. 129 CF: MP pode investigar qualquer autoridade.", "Funciona, mas é lento. Denúncias anônimas são aceitas. Resultados demoram anos.", 5),
        Direito("Segurança pública", "Art. 144 CF: Segurança pública é dever do Estado.", "90% dos homicídios sem solução. Taxa de 22 por 100 mil habitantes. Você paga caro e não recebe.", 2),
        Direito("Saneamento básico", "Lei 14.026/2020: Meta de universalizar até 2033.", "35 milhões sem água tratada. 100 milhões sem esgoto. Meta de 2033 não vai ser cumprida.", 3),
        Direito("Participação popular", "Art. 14 CF: Plebiscito, referendo, iniciativa popular.", "Último plebiscito: 2005. 4 leis de iniciativa popular em 35 anos. 71 mil ideias no Senado, 23 viraram lei.", 1),
        Direito("Transparência pública", "LC 131/2009: Gastos públicos devem ser divulgados em tempo real.", "Portal da Transparência funciona. Mas a linguagem é técnica. Poucos cidadãos conseguem usar.", 6),
        Direito("Fiscalização (consumidor.gov.br)", "Plataforma de resolução de conflitos com empresas.", "86% de resolução. Melhor que Procon. Empresas respondem porque a nota é pública.", 8)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
    ) {
        item {
            Text("SEUS DIREITOS (e se funcionam)", style = MaterialTheme.typography.titleMedium, color = Red600, fontWeight = FontWeight.Black)
            Text("Avaliados pela eficácia real, não pela teoria.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))
        }
        items(direitos) { dir ->
            var expanded by remember { mutableStateOf(false) }
            val notaColor = when {
                dir.nota >= 7 -> Color(0xFF22C55E)
                dir.nota >= 4 -> Color(0xFFFBBF24)
                else -> Red600
            }
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).animateContentSize().clickable { expanded = !expanded },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(dir.nome, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("${dir.nota}/10", color = notaColor, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { dir.nota / 10f },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = notaColor, trackColor = Color(0xFF262626)
                    )
                    AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text("O QUE DIZ A LEI:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold)
                            Text(dir.lei, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("NA PRÁTICA:", style = MaterialTheme.typography.labelSmall, color = notaColor, fontWeight = FontWeight.Bold)
                            Text(dir.naPratica, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onCidadaoClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text("Ver todos os canais detalhados", color = Red600, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ConceitoCard(conceito: Conceito) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize().clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(conceito.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = Red600, modifier = Modifier.size(24.dp))
            }
            AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
                Text(conceito.explicacao, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 8.dp), lineHeight = 20.sp)
            }
        }
    }
}

@Composable
private fun LeiAprovadaCard(lei: com.trouxaapp.app.data.model.LeiAprovada) {
    var expanded by remember { mutableStateOf(false) }
    val nivelColor = when {
        lei.nivelTrouxa >= 8 -> Red600
        lei.nivelTrouxa >= 5 -> Color(0xFFFBBF24)
        else -> Green500
    }
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize().clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(nivelColor, shape = CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(lei.nomePopular, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("${lei.nivelTrouxa}/10", style = MaterialTheme.typography.labelMedium, color = nivelColor, fontWeight = FontWeight.Black)
            }
            Text("${lei.nome} (${lei.ano})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { lei.nivelTrouxa / 10f },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = nivelColor, trackColor = Color(0xFF262626)
            )
            AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text("O QUE É:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF60A5FA), fontWeight = FontWeight.Black)
                    Text(lei.oQueE, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("QUEM FEZ:", style = MaterialTheme.typography.labelMedium, color = Red600, fontWeight = FontWeight.Black)
                    lei.quemFez.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface) }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("O QUE MUDOU NA SUA VIDA:", style = MaterialTheme.typography.labelMedium, color = Color(0xFFFBBF24), fontWeight = FontWeight.Black)
                    Text(lei.oQueMudou, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ELES AINDA ESTÃO LÁ?", style = MaterialTheme.typography.labelMedium, color = Red600, fontWeight = FontWeight.Black)
                    lei.aindaEstaLa.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface) }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Fonte: ${lei.fonte}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// === TAB 4: SUA VIDA ===
@Composable
private fun SuaVidaTab() {
    data class Tema(val titulo: String, val itens: List<Conceito>)
    val temas = listOf(
        Tema("SEU CARRO", listOf(
            Conceito("IPVA: pra que serve?",
                "Imposto sobre veículo, pago todo ano. 50% vai pro estado, 50% pro município. Deveria ir pra manutenção de vias e sinalização. Na prática: sua rua tem cratera. Você paga R\$ 2.000 de IPVA e o asfalto tem buraco. Japão não cobra IPVA. Fonte: Secretaria da Fazenda."),
            Conceito("Indústria da multa",
                "Radares e fiscalização viraram fonte de receita. SP arrecada bilhões em multas por ano. Investimento em segurança viária é uma fração disso. A cidade ganha mais te multando do que te protegendo. Fonte: DETRAN, Secretaria de Transportes."),
            Conceito("Imposto no carro novo",
                "IPI + ICMS + PIS/COFINS = até 30% do preço é imposto. Um carro de R\$ 80.000: R\$ 24.000 é imposto. Nos EUA, o mesmo carro custa 30-40% menos. Por que o brasileiro paga mais por um carro pior? Fonte: ANFAVEA, Receita Federal."),
            Conceito("Combustível do seu carro",
                "44% do que você paga na bomba é imposto. O resto é custo de produção, distribuição e margem. O PPI atrelou o preço ao dólar desde 2016. Quando o dólar sobe, gasolina sobe. Mesmo o petróleo sendo nosso. Fonte: ANP.")
        )),
        Tema("SUA CASA", listOf(
            Conceito("IPTU: pra onde vai?",
                "Imposto sobre propriedade, 100% pro município. Deveria ir pra saneamento, iluminação, asfalto. Na prática: sua rua alaga. Você paga IPTU e a calçada tá quebrada. Consulte o orçamento do seu município no Portal da Transparência."),
            Conceito("Aluguel vs salário",
                "Em 2021, o IGP-M (índice de reajuste do aluguel) foi 37%. Seu salário subiu 5%. Seu proprietário ficou mais rico. Você ficou mais apertado. Muitos contratos já trocaram pro IPCA (menor). Se o seu ainda é IGP-M, negocie. Fonte: FGV, IBGE."),
            Conceito("Financiamento: quanto você paga de juros",
                "Apartamento de R\$ 300.000 financiado em 30 anos com taxa de 10% ao ano. Total pago: ~R\$ 950.000. Você compra 1 apartamento e paga 3. Nos EUA a taxa é ~6%. Na Europa, ~3%. Fonte: BCB, Caixa."),
            Conceito("Contas da casa: quanto é imposto",
                "Luz: ~30% é imposto/taxa. Água: ~20% é taxa. Gás: PPI + impostos. Internet: ~40% é imposto. Somando tudo, você paga centenas de reais por mês de imposto só nas contas de casa. Sem contar o que já pagou de IR e INSS. Fonte: ANEEL, IBPT.")
        )),
        Tema("SEU EMPREGO", listOf(
            Conceito("CLT vs PJ: o que você perde",
                "CLT: FGTS, 13o, férias, INSS, estabilidade parcial. PJ: nada disso, mas paga menos imposto. Empresa te contrata como PJ pra economizar 40%. Você ganha 20% a mais no curto prazo. Mas perde 13o, férias, FGTS e aposentadoria no longo. Fonte: CLT."),
            Conceito("FGTS: seu dinheiro perdendo valor",
                "FGTS rende 3% ao ano + TR. Inflação média: 5% ao ano. Seu dinheiro no FGTS PERDE VALOR todo ano. Em 10 anos, você perdeu ~20% do poder de compra. E não pode sacar quando quiser. O governo usa pra financiar habitação. Com o SEU dinheiro. Fonte: Caixa, BCB."),
            Conceito("INSS: quanto paga vs quanto recebe",
                "Você contribui a vida inteira. Depois da reforma de 2019: homem aposenta com 65 anos + 20 anos de contribuição. Cálculo: média de 100% das contribuições (antes era as 80% melhores). Na prática: você trabalha mais e recebe menos. Simule em meu.inss.gov.br. Fonte: INSS."),
            Conceito("Quanto do seu salário vai pro governo",
                "Salário bruto R\$ 3.000: INSS (R\$ 360) + IR (R\$ 95) = líquido R\$ 2.545. Mas impostos indiretos (~27%) tiram mais R\$ 687. Sobra real: R\$ 1.858. O governo fica com R\$ 1.142 do seu trabalho. E o retorno? Fila no SUS, escola precária, rua esburacada. Fonte: IBPT."),
            Conceito("Reforma trabalhista: o que mudou",
                "Trabalho intermitente: podem te chamar por hora sem garantia de renda. Acordo individual: patrão negocia direto sem sindicato. Terceirização total: terceirizado ganha 25% menos em média. Sindicatos perderam 90% da receita. Nenhum governo propôs reverter. Fonte: Lei 13.467/2017, DIEESE.")
        )),
        Tema("NO MUNDO", listOf(
            Conceito("Transporte: como Tóquio resolveu",
                "Cidade de 14 milhões. Investimento massivo em metrô/trem por 40 anos. Resultado: 88% usa transporte público. Trem pontual ao SEGUNDO. SP tem 100km de metrô. Tóquio tem 300km. Com metade da área. Solução existe. Falta investimento contínuo."),
            Conceito("Saúde: como o Reino Unido faz",
                "NHS (SUS deles). Gratuito e universal pra 67 milhões de pessoas. Diferença: investem US\$ 5.000 per capita/ano. Brasil investe US\$ 700. O SUS funciona melhor do que deveria dado o investimento. O problema não é o modelo. É o desvio. Fonte: OMS."),
            Conceito("Transparência: como a Estônia fez",
                "País pós-soviético com corrupção. Solução: governo 100% digital. Vota, paga imposto, abre empresa, tudo pelo celular. Tudo rastreável. Resultado: um dos governos mais transparentes do mundo. Em 20 anos de investimento."),
            Conceito("Segurança: como Medellín mudou",
                "Cidade mais violenta do mundo nos anos 90. Solução: investimento em educação, transporte (metrocable), espaços públicos. 20 anos contínuos. Homicídios caíram 95%. Não foi com mais polícia. Foi com mais escola e metrô. Fonte: Banco Mundial."),
            Conceito("Educação: como a Finlândia lidera",
                "Nos anos 70, educação medíocre. Solução: professor virou profissão de prestígio (salário alto, mestrado obrigatório). 40 anos de investimento contínuo. Top 5 do mundo. Na Finlândia, ser professor é tão difícil quanto ser médico. Fonte: OCDE.")
        ))
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
    ) {
        temas.forEach { tema ->
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(tema.titulo, style = MaterialTheme.typography.titleMedium, color = Red600, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(tema.itens) { conceito ->
                ConceitoCard(conceito)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}
