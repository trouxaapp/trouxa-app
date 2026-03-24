package com.trouxaapp.app.ui.screens.hoje

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.data.local.HojeDataProvider
import com.trouxaapp.app.data.model.ImpactCard
import com.trouxaapp.app.ui.theme.Red600
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactDetailScreen(
    impactId: Int,
    onBack: () -> Unit,
    onPresidentClick: (String) -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val dayOfYear = if (impactId > 0) impactId else Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    val card = HojeDataProvider.getImpactCardForDay(dayOfYear)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IMPACTO DO DIA", color = Red600, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { shareCardAsImage(context, "IMPACTO DO DIA", card.text, card.source) }) {
                        Icon(Icons.Default.Share, "Compartilhar", tint = Color(0xFF737373))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main impact text
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(card.text, color = Color.White, fontSize = 18.sp, lineHeight = 26.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Fonte: ${card.source}", color = Color(0xFF737373), fontSize = 12.sp)
                    }
                }
            }

            // O QUE ACONTECEU
            item {
                SectionCard("O QUE ACONTECEU") {
                    val explicacao = card.explicacaoCompleta.ifEmpty {
                        generateExplicacao(card)
                    }
                    Text(explicacao, color = Color(0xFFD4D4D4), fontSize = 15.sp, lineHeight = 22.sp)
                }
            }

            // QUEM GOVERNOU NESSE PERIODO
            item {
                SectionCard("QUEM GOVERNOU NESSE PERIODO") {
                    val presidents = if (card.presidentesRelacionados.isNotEmpty()) {
                        DataProvider.presidents.filter { it.initials in card.presidentesRelacionados }
                    } else {
                        DataProvider.presidents.takeLast(3)
                    }
                    presidents.forEach { pres ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onPresidentClick(pres.initials) },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF262626))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(pres.initials, color = Red600, fontWeight = FontWeight.Black, fontSize = 20.sp,
                                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF333333)).padding(8.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${pres.name} (${pres.party})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("${pres.startYear}-${pres.endYear ?: "atual"}", color = Color(0xFF737373), fontSize = 12.sp)
                                }
                                Text("Ver perfil", color = Color(0xFF60A5FA), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // O QUE VOCE PODE FAZER
            item {
                SectionCard("O QUE VOCE PODE FAZER") {
                    val actions = card.oQueFazer.ifEmpty {
                        listOf(
                            com.trouxaapp.app.data.model.ActionItem("Acompanhe as leis que afetam preços", "entenda", ""),
                            com.trouxaapp.app.data.model.ActionItem("Veja quanto seu deputado gasta", "seu_deputado", ""),
                            com.trouxaapp.app.data.model.ActionItem("Compare os governos", "comparativo", "")
                        )
                    }
                    actions.forEach { action ->
                        TextButton(
                            onClick = { onNavigate(action.destino) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${action.texto} →", color = Color(0xFF60A5FA), fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }

            // FONTES
            item {
                SectionCard("FONTES") {
                    val fontes = card.fontesUrls.ifEmpty {
                        card.source.split(",").associate { it.trim() to "" }
                    }
                    fontes.forEach { (nome, url) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                .then(if (url.isNotEmpty()) Modifier.clickable {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                } else Modifier),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(nome, color = if (url.isNotEmpty()) Color(0xFF60A5FA) else Color(0xFFA3A3A3), fontSize = 14.sp, modifier = Modifier.weight(1f))
                            if (url.isNotEmpty()) {
                                Icon(Icons.Default.OpenInNew, null, tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color(0xFF737373), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

private fun generateExplicacao(card: ImpactCard): String {
    val text = card.text
    return when {
        text.contains("cesta") || text.contains("salário") ->
            "O poder de compra do brasileiro vem caindo consistentemente. " +
            "Mesmo com reajustes no salário mínimo, o preço dos alimentos subiu mais rápido. " +
            "A cestá básica em São Paulo custa R\$ 900, o que consome 59% de um salário mínimo. " +
            "Em 2010, o melhor momento, o salário comprava 2,12 cestas. Hoje compra 1,69. " +
            "Você trabalha a mesma quantidade de horas e leva menos comida pra casa."

        text.contains("Congresso") || text.contains("deputado") ->
            "O Congresso Nacional custa R\$ 14,9 bilhões por ano ao contribuinte. " +
            "Sao 513 deputados e 81 senadores, cada um com salário de R\$ 46 mil, " +
            "cotá parlamentar de até R\$ 57 mil, verba de gabinete de R\$ 165 mil " +
            "e auxílio moradia de R\$ 4.253. O custo total por parlamentar e de " +
            "cerca de R\$ 3,5 milhões por ano. Você ganha R\$ 18 mil por ano."

        text.contains("petróleo") || text.contains("gasolina") ->
            "O Brasil e o 7o maior produtor de petróleo do mundo, extraindo " +
            "3,7 milhões de barris por dia. Mesmo assim, o preço da gasolina " +
            "segue o PPI (Preço de Paridade de Importação), criado em 2016 " +
            "por Pedro Parente na Petrobras. Isso significa que você paga " +
            "como se o petróleo fosse importado, mesmo sendo extraido aqui."

        text.contains("obra") ->
            "O TCU identificou 11.469 obras públicas paradas em todo o Brasil. " +
            "Dessas, 70% são de saúde e educação. O valor investido nessas obras " +
            "que não foram concluídas soma R\$ 15,9 bilhões. Dinheiro de imposto " +
            "que virou concreto abandonado. Com esse valor seria possível construir " +
            "mais de 1.500 escolas ou 800 hospitais de pequeno porte."

        text.contains("imposto") ->
            "A carga tributaria brasileira e de 33,7% do PIB. De cada R\$ 3 que " +
            "o país produz, R\$ 1 vai pro governo. O problema não e so o quanto " +
            "se paga, mas o que se recebe: fila de 2 anos no SUS, escola publica " +
            "sucateada, 90% dos homicídios sem solução, 35 milhões sem água tratada. " +
            "A Dinamarca cobra 46% mas entrega saúde, educação e transporte de 1o mundo."

        else ->
            "Esse dado vem de fontes oficiais do governo brasileiro. " +
            "Cada numero citado pode ser verificado nos links de fonte abaixo. " +
            "O app mostra todos os governos, sem lado politico. " +
            "Os numeros falam por si."
    }
}
