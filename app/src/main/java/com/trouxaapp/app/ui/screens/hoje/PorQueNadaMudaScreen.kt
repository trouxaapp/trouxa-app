package com.trouxaapp.app.ui.screens.hoje

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.ui.theme.Red600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PorQueNadaMudaScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("POR QUE NADA MUDA?", color = Red600, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White) } },
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
            item {
                Text(
                    "Você sente que não importa o que faça, nada muda. Você está certo. E não é porque você é fraco. É porque o sistema foi DESENHADO pra resistir mudança.",
                    color = Color.White, fontSize = 16.sp, lineHeight = 24.sp
                )
            }

            item { SystemSection("1. QUEM FAZ AS LEIS É QUEM SE BENEFICIA DELAS",
                "Deputados votam o próprio salário. Deputados votam as próprias emendas. Deputados votam a própria aposentadoria. Pedir pro Congresso reduzir privilégios é pedir pro lobo cuidar das ovelhas.") }

            item { SystemSection("2. QUEM JULGA É QUEM SE BENEFICIA",
                "O Judiciário custa R\$ 103 bilhões/ano. Juízes ganham R\$ 35 mil/mês com penduricalhos. Quem fiscaliza o Judiciário? O próprio Judiciário (CNJ). É como pedir pro aluno corrigir a própria prova.") }

            item { SystemSection("3. QUEM INFORMA É QUEM SE BENEFICIA",
                "6 famílias controlam 70% da mídia brasileira. Essas famílias têm concessão pública de TV e rádio. Quem renova a concessão? O governo. Então a mídia não pode criticar muito quem renova a concessão.") }

            item { SystemSection("4. QUEM FINANCIA CAMPANHA MANDA NO VOTO",
                "Empresas doam pra campanha. Deputado eleito vota a favor dessas empresas. Ciclo: dinheiro, eleição, voto, mais dinheiro. Candidato sem financiamento não tem chance.") }

            item { SystemSection("5. A LINGUAGEM É UMA BARREIRA DE PROPÓSITO",
                "Leis são escritas em juridiquês. Orçamento é escrito em economês. Se o cidadão não entende, não fiscaliza. Se não fiscaliza, o sistema opera em paz.") }

            item {
                HorizontalDivider(color = Color(0xFF333333))
            }

            item {
                Text("MAS ENTÃO NÃO TEM JEITO?", color = Color(0xFF22C55E), fontSize = 18.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tem. Mas é lento. E não é o que vendem pra você.", color = Color.White, fontSize = 15.sp, lineHeight = 22.sp)
            }

            item { SolutionSection("PRESSÃO POPULAR SUSTENTADA",
                "Diretas Já (1984): 2 anos de manifestação. Impeachment Collor (1992): 4 meses de pressão. Protestos 2013: 2 milhões nas ruas. NÃO foi um post no Twitter. Foi CORPO NA RUA por MESES. E mesmo assim, o sistema se adaptou e voltou.",
                Color(0xFFFBBF24)) }

            item { SolutionSection("FERRAMENTAS DE TRANSPARÊNCIA",
                "Serenata de Amor: robô que fiscalizou CEAP. Devolveu R\$ 50k. Sleeping Giants BR: cortou publicidade de fake news. R\$ 203 milhões. Lava Jato: com todas as críticas, recuperou R\$ 4 bilhões. NÃO mudou o sistema. Mas abriu FISSURAS.",
                Color(0xFF60A5FA)) }

            item { SolutionSection("ORGANIZAÇÃO LOCAL",
                "Associação de bairro que cobra a prefeitura TODO MÊS. Conselho municipal que participa das audiências públicas. Grupo de WhatsApp de vizinhos que documenta problemas. Menos sexy que viralizar. Mas é o que REALMENTE pressiona vereador e prefeito.",
                Color(0xFF22C55E)) }

            item { SolutionSection("VOTO INFORMADO",
                "Votar NÃO resolve sozinho. Votar SABENDO quem gastou quanto, quem faltou, quem votou o quê: isso muda. O app existe pra isso. Pra que em outubro de 2026 você saiba exatamente quem merece e quem não merece seu voto. Não por opinião. Por dado.",
                Red600) }

            item {
                HorizontalDivider(color = Color(0xFF333333))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("O QUE O APP FAZ NESSA EQUAÇÃO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "O app é a INFORMAÇÃO. Não é a solução. É a munição.\n\n" +
                            "Sozinho não muda nada. Mas nas mãos de 100 mil pessoas informadas, que sabem quanto custa cada deputado, que cobram todo mês, que tiram foto do buraco e publicam com dados: isso cria pressão que o sistema NÃO consegue ignorar.\n\n" +
                            "A sensação de impotência vem de agir sozinho. 50 mil pessoas cobrando o MESMO deputado pelo MESMO dado no MESMO dia é diferente de 1 pessoa reclamando no WhatsApp.\n\n" +
                            "NÃO É REVOLUÇÃO. É EROSÃO.\nÁgua mole em pedra dura, tanto bate até que fura.\nMas a água precisa bater TODO DIA. Não uma vez por eleição.",
                            color = Color(0xFFD4D4D4), fontSize = 14.sp, lineHeight = 22.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun SystemSection(title: String, text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0A0A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Red600, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text, color = Color(0xFFD4D4D4), fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun SolutionSection(title: String, text: String, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1A0A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = accentColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text, color = Color(0xFFD4D4D4), fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}
