package com.trouxaapp.app.ui.screens.hoje

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.util.EngagementManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakScreen(
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val engagement = remember { EngagementManager(context) }
    val streak = engagement.streak
    val daysUsed = engagement.daysUsed
    val streakData = engagement.getStreakData()

    val unlocks = listOf(
        Triple(1, "Feed completo e Histórico", true),
        Triple(2, "\"Por que?\" dos preços", daysUsed >= 2),
        Triple(3, "\"Quem decidiu\" cada preço", daysUsed >= 3),
        Triple(5, "Perfil completo de deputados", daysUsed >= 5),
        Triple(7, "Notificações inteligentes", daysUsed >= 7),
        Triple(14, "Cobrar deputado", daysUsed >= 14)
    )

    val ranking = when {
        streak >= 30 -> 5
        streak >= 14 -> 10
        streak >= 7 -> 20
        streak >= 3 -> 40
        else -> 60
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SUA JORNADA", color = Red600, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White) }
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
            // Streak counter
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$streak", color = Red600, fontWeight = FontWeight.Black, fontSize = 72.sp)
                        Text(
                            if (streak == 1) "dia de consciência" else "dias consecutivos",
                            color = Color(0xFFA3A3A3), fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(streakData.message, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                    }
                }
            }

            // O QUE VOCE JA DESBLOQUEOU
            item {
                SectionCard("O QUE VOCE JA DESBLOQUEOU") {
                    unlocks.forEach { (day, feature, unlocked) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (unlocked) Icons.Default.Check else Icons.Default.Lock,
                                null,
                                tint = if (unlocked) Color(0xFF22C55E) else Color(0xFF737373),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(feature, color = if (unlocked) Color.White else Color(0xFF737373), fontSize = 14.sp)
                                Text("Dia $day", color = Color(0xFF505050), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // PROXIMO DESBLOQUEIO
            val next = unlocks.firstOrNull { !it.third }
            if (next != null) {
                item {
                    SectionCard("PRÓXIMO DESBLOQUEIO") {
                        val daysLeft = next.first - daysUsed
                        Text(
                            "Em $daysLeft ${if (daysLeft == 1) "dia" else "dias"} você desbloqueia:",
                            color = Color(0xFFA3A3A3), fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(next.second, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            // RANKING
            item {
                SectionCard("SEU NIVEL") {
                    Text(
                        "Você sabe mais que ${100 - ranking}% dos brasileiros sobre política.",
                        color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Estimativa baseada em pesquisas de conhecimento politico (Datafolha, 2024)",
                        color = Color(0xFF505050), fontSize = 11.sp
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
