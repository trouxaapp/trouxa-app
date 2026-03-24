package com.trouxaapp.app.ui.screens.hoje

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.data.local.HojeDataProvider
import com.trouxaapp.app.ui.theme.Red600
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactDetailScreen(
    factId: Int,
    onBack: () -> Unit
) {
    val dayOfYear = if (factId > 0) factId else Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    val fact = HojeDataProvider.getVoceSabiaForWeek(dayOfYear)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VOCÊ SABIA?", color = Red600, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White) }
                },
                actions = {
                    IconButton(onClick = { shareCardAsImage(context, "VOCÊ SABIA?", fact.textoResumo, fact.source) }) {
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
            // O FATO
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Text(
                        fact.textoResumo,
                        modifier = Modifier.padding(20.dp),
                        color = Color.White,
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // CONTEXTO
            item {
                SectionCard("O CONTEXTO") {
                    val completo = fact.textoCompleto.ifEmpty {
                        generateFactContext(fact.textoResumo)
                    }
                    Text(completo, color = Color(0xFFD4D4D4), fontSize = 15.sp, lineHeight = 22.sp)
                }
            }

            // POR QUE NINGUEM FALA DISSO
            item {
                SectionCard("POR QUE NINGUEM FALA DISSO") {
                    val reason = fact.porQueNinguémFala.ifEmpty {
                        "Esses dados são públicos e acessíveis. Mas não aparecem no jornal porque " +
                        "nao geram audiência. Numeros isolados não chocam. Comparações sim. " +
                        "Por isso você nunca viu esse dado lado a lado com o seu salário."
                    }
                    Text(reason, color = Color(0xFFD4D4D4), fontSize = 15.sp, lineHeight = 22.sp)
                }
            }

            // FONTES
            item {
                SectionCard("FONTES") {
                    val fontes = fact.fontes.ifEmpty {
                        fact.source.split(",").associate { it.trim() to "" }
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
                            if (url.isNotEmpty()) Icon(Icons.Default.OpenInNew, null, tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

private fun generateFactContext(text: String): String {
    return when {
        text.contains("iniciativa popular") ->
            "A Constituição de 1988 permite que cidadãos proponham leis. Mas exige 1,5 milhao de assinaturas " +
            "de pelo menos 5 estados. Em 35 anos, so 4 conseguiram: Lei da Ficha Limpa, Lei contra Compra de Votos, " +
            "Lei contra Crimes Hediondos e parte do Fundo Nacional de Habitacao. Mesmo essas precisaram de apoio " +
            "de grandes organizacoes como a CNBB e a OAB. Na prática, o cidadão comum não tem como propor uma lei."

        text.contains("obras") || text.contains("paradas") ->
            "O Tribunal de Contas da Uniao (TCU) faz levantamento periodico de obras públicas. O ultimo, em 2025, " +
            "encontrou 11.469 obras paradas ou atrasadas. Dessas, 70% são de saúde e educação: hospitais " +
            "inacabados, escolas sem teto, postos de saúde abandonados. O valor total investido nessas obras " +
            "que não foram concluídas e de R\$ 15,9 bilhões. Dinheiro de imposto virou concreto abandonado."

        text.contains("homicidio") || text.contains("assassinato") ->
            "O Forum Brasileiro de Seguranca Publica calcula que menos de 10% dos homicídios são solucionados " +
            "no Brasil. Isso significa que 9 em cada 10 assassinos não são identificados. O Brasil gasta " +
            "bilhões em segurança publica, mas a maior parte vai pra equipamento e salário. Investigacao " +
            "criminal recebe uma fração. O resultado: impunidade quase garantida."

        else ->
            "Esse dado vem de fontes oficiais e e verificável. A maioria dos brasileiros desconhece " +
            "porque a informação existe de forma fragmentada, técnica e sem contexto. " +
            "Quando você juntá os numeros, o retrato aparece. E não e bonito."
    }
}
