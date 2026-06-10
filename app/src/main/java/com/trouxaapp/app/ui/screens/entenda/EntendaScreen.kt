package com.trouxaapp.app.ui.screens.entenda

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.ui.components.EmptyState
import com.trouxaapp.app.ui.components.FonteLinha
import com.trouxaapp.app.ui.components.SectionLabel
import com.trouxaapp.app.ui.theme.CardBorder
import com.trouxaapp.app.ui.theme.OnSurfaceFaint
import com.trouxaapp.app.ui.theme.OnSurfaceVariant
import com.trouxaapp.app.ui.theme.Red600

private data class Verbete(
    val termo: String,
    val definicao: String,
    val fonteNome: String,
    val fonteUrl: String
)

private const val URL_PROCESSO_LEGISLATIVO = "https://www.camara.leg.br/entenda-o-processo-legislativo/"

// Timeless institutional definitions only. Nothing here carries a current
// value, a statistic or anything that ages. Where a term has a live value
// (Selic, IPCA, PTAX, DIEESE), the value itself lives behind the source link.
private val glossario = listOf(
    Verbete(
        termo = "PL (Projeto de Lei)",
        definicao = "Proposta para criar uma lei nova ou mudar uma lei que já existe. " +
            "Pode ser apresentada por deputados, senadores, pelo presidente da República e por outros legitimados. " +
            "Precisa ser aprovada na Câmara e no Senado e depois segue para sanção ou veto do presidente.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "PEC (Proposta de Emenda à Constituição)",
        definicao = "Proposta para alterar o próprio texto da Constituição, por isso o caminho é mais exigente: " +
            "precisa ser votada em dois turnos na Câmara e em dois turnos no Senado, com pelo menos três quintos dos votos em cada um. " +
            "Não passa por sanção do presidente. Depois de aprovada, é promulgada pelo próprio Congresso.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "PLP (Projeto de Lei Complementar)",
        definicao = "Projeto que trata de assuntos que a Constituição reservou para esse tipo de lei, " +
            "como certas regras de finanças públicas. " +
            "Para ser aprovado, exige maioria absoluta: mais da metade de todos os membros da Casa, e não só dos presentes na sessão.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "MPV (Medida Provisória)",
        definicao = "Norma editada pelo presidente da República que já vale como lei a partir da publicação, sem passar antes pelo Congresso. " +
            "Só pode ser usada em casos de relevância e urgência. " +
            "O Congresso tem um prazo definido na Constituição para analisá-la. Se o prazo termina sem aprovação, ela perde a validade.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "PDL (Projeto de Decreto Legislativo)",
        definicao = "Projeto que cuida de assuntos de competência exclusiva do Congresso, " +
            "como aprovar acordos internacionais ou suspender atos do Poder Executivo que ultrapassem os limites dados pela lei. " +
            "Não vai para sanção do presidente: o próprio Congresso promulga.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "Ementa",
        definicao = "O resumo oficial que abre toda proposta e diz, em poucas linhas, o que ela pretende fazer. " +
            "É o texto que aparece nos sistemas de busca da Câmara e do Senado. " +
            "É um bom ponto de partida, mas não substitui a leitura do texto completo.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "Tramitação",
        definicao = "O caminho que uma proposta percorre dentro do Congresso: distribuição para comissões, pareceres, votações " +
            "e idas e vindas entre Câmara e Senado. " +
            "Cada passo fica registrado nos sistemas oficiais, com data e descrição. É essa movimentação que o app acompanha.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "Regime de urgência",
        definicao = "Mecanismo que acelera a tramitação de uma proposta. " +
            "Na prática, ela pode pular etapas, como a discussão mais demorada nas comissões, e ir mais rápido para votação no Plenário. " +
            "A urgência costuma ser aprovada pelos próprios parlamentares por meio de requerimento, e em alguns casos pode ser pedida pelo presidente da República.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "Relator",
        definicao = "Parlamentar escolhido para estudar uma proposta em nome de uma comissão ou do Plenário. " +
            "Ele apresenta um parecer recomendando a aprovação, a rejeição ou mudanças no texto. " +
            "O parecer do relator costuma pesar bastante no resultado da votação.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "CCJ (Comissão de Constituição e Justiça)",
        definicao = "Comissão que analisa se uma proposta respeita a Constituição e as regras de técnica legislativa. " +
            "Quase toda proposta passa por ela em algum momento. " +
            "Se a CCJ entende que o texto é inconstitucional, ele pode ser arquivado.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "Plenário",
        definicao = "A instância em que todos os parlamentares da Casa votam juntos, e não só os membros de uma comissão. " +
            "É onde acontecem as votações finais mais importantes. " +
            "Quando se diz que uma proposta foi a Plenário, significa que ela chegou a essa etapa de decisão.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "Sanção e veto",
        definicao = "Depois de aprovado pelo Congresso, o projeto de lei vai para o presidente da República. " +
            "Ele pode sancionar, e aí o projeto vira lei, ou vetar, no todo ou em parte, explicando o motivo. " +
            "O veto volta para o Congresso, que decide se mantém ou derruba a decisão do presidente.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "Quórum",
        definicao = "Número mínimo de parlamentares que precisam estar presentes ou votar para que uma decisão valha. " +
            "Cada tipo de proposta tem o seu: algumas exigem só maioria simples dos presentes, outras exigem maioria absoluta ou mais. " +
            "Sem quórum, a sessão nem delibera.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "Emenda parlamentar",
        definicao = "Tem dois usos comuns. Em um projeto em tramitação, emenda é uma proposta de mudança no texto feita por um parlamentar. " +
            "No Orçamento, emenda parlamentar é o instrumento pelo qual deputados e senadores indicam para onde parte dos recursos públicos deve ir, " +
            "como obras e ações nos estados que representam.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = URL_PROCESSO_LEGISLATIVO
    ),
    Verbete(
        termo = "Cota parlamentar (CEAP)",
        definicao = "Cota para o Exercício da Atividade Parlamentar. " +
            "É a verba mensal que cada deputado pode usar para custear despesas do mandato, como passagens aéreas, combustível, escritório e divulgação. " +
            "Todos os gastos são públicos e podem ser consultados no portal da Câmara, nota por nota.",
        fonteNome = "Câmara dos Deputados",
        fonteUrl = "https://www.camara.leg.br/cota-parlamentar/"
    ),
    Verbete(
        termo = "Selic",
        definicao = "A taxa básica de juros da economia brasileira. " +
            "É definida periodicamente pelo Copom, o comitê do Banco Central responsável pela política monetária, " +
            "e serve de referência para os demais juros do país, como empréstimos e financiamentos. " +
            "O valor vigente está sempre no site do Banco Central.",
        fonteNome = "Banco Central",
        fonteUrl = "https://www.bcb.gov.br/controleinflacao/taxaselic"
    ),
    Verbete(
        termo = "IPCA",
        definicao = "Índice Nacional de Preços ao Consumidor Amplo. " +
            "É o índice oficial de inflação do Brasil, calculado todo mês pelo IBGE a partir dos preços de uma cesta de produtos e serviços consumidos pelas famílias. " +
            "Quando se fala que a inflação subiu ou caiu, em geral a referência é o IPCA.",
        fonteNome = "IBGE",
        fonteUrl = "https://www.ibge.gov.br/explica/inflacao.php"
    ),
    Verbete(
        termo = "PTAX",
        definicao = "Taxa de câmbio de referência calculada e divulgada pelo Banco Central a partir das cotações do dólar negociadas no mercado. " +
            "É muito usada como base em contratos e operações financeiras. " +
            "Não é o preço que você paga na casa de câmbio, que inclui custos e margens.",
        fonteNome = "Banco Central",
        fonteUrl = "https://www.bcb.gov.br/estabilidadefinanceira/historicocotacoes"
    ),
    Verbete(
        termo = "Salário mínimo necessário (DIEESE)",
        definicao = "Estimativa calculada e publicada todo mês pelo DIEESE de quanto o salário mínimo precisaria ser " +
            "para sustentar uma família com alimentação, moradia, saúde, educação, transporte e os demais itens previstos na Constituição. " +
            "O cálculo parte do custo da cesta básica de alimentos. O valor do mês está sempre no site do DIEESE.",
        fonteNome = "DIEESE",
        fonteUrl = "https://www.dieese.org.br/analisecestabasica/salarioMinimo.html"
    )
)

@Composable
fun EntendaScreen() {
    var busca by rememberSaveable { mutableStateOf("") }

    val filtrados = remember(busca) {
        if (busca.isBlank()) glossario
        else glossario.filter {
            it.termo.contains(busca, ignoreCase = true) ||
                it.definicao.contains(busca, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp)
        ) {
            Text(
                text = "ENTENDA",
                style = MaterialTheme.typography.headlineMedium,
                letterSpacing = 2.sp
            )
            Text(
                text = "O básico pra acompanhar o Congresso sem juridiquês. Definições baseadas nas fontes oficiais.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        OutlinedTextField(
            value = busca,
            onValueChange = { busca = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Buscar termo", color = OnSurfaceFaint) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = OnSurfaceVariant)
            },
            trailingIcon = {
                if (busca.isNotBlank()) {
                    IconButton(onClick = { busca = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Limpar busca", tint = OnSurfaceVariant)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Red600,
                unfocusedBorderColor = CardBorder,
                cursorColor = Red600
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filtrados.isEmpty()) {
                item(key = "vazio") {
                    EmptyState(
                        titulo = "Nenhum termo encontrado",
                        texto = "Tente escrever de outro jeito ou limpe a busca."
                    )
                }
            }
            items(filtrados, key = { it.termo }) { verbete ->
                VerbeteCard(verbete)
            }
            item(key = "fontes_do_app") {
                FontesDoAppCard()
            }
        }
    }
}

@Composable
private fun VerbeteCard(verbete: Verbete) {
    // rememberSaveable inside a keyed lazy item survives scrolling away and back
    var expandido by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .clickable { expandido = !expandido }
            .animateContentSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = verbete.termo,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expandido) "Recolher" else "Expandir",
                tint = OnSurfaceVariant
            )
        }
        if (expandido) {
            Text(
                text = verbete.definicao,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
            FonteLinha(
                fonte = verbete.fonteNome,
                url = verbete.fonteUrl,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun FontesDoAppCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        SectionLabel("De onde vêm os dados do app")
        FonteLinha(
            fonte = "Câmara dos Deputados",
            url = "https://dadosabertos.camara.leg.br"
        )
        FonteLinha(
            fonte = "Banco Central",
            url = "https://dadosabertos.bcb.gov.br"
        )
        FonteLinha(
            fonte = "Curadoria com fontes",
            url = "https://github.com/trouxaapp/trouxa-app"
        )
    }
}
