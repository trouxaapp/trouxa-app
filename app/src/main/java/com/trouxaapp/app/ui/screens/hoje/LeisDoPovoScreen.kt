package com.trouxaapp.app.ui.screens.hoje

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.data.local.LeisDoPovoProvider
import com.trouxaapp.app.ui.theme.Red600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeisDoPovoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("trouxa_votos", android.content.Context.MODE_PRIVATE) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val ideias = remember {
        LeisDoPovoProvider.ideias.let { list ->
            if (selectedCategory != null) list.filter { it.categoria == selectedCategory } else list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("VOZ DO CIDADÃO", color = Red600, fontWeight = FontWeight.Black)
                        Text("71.000 ideias enviadas. 23 aprovadas. 0,03%.", style = MaterialTheme.typography.labelSmall, color = Color(0xFFA3A3A3))
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Aqui quem decide é você. Apoie as ideias que importam.", color = Color(0xFFD4D4D4), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                // Category filter
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(null to "Todas", "segurança" to "Segurança", "economia" to "Economia", "saúde" to "Saúde", "educação" to "Educação", "direitos" to "Direitos").forEach { (cat, label) ->
                        val sel = selectedCategory == cat
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (sel) Red600 else Color(0xFF262626)).clickable { selectedCategory = cat }.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(label, color = if (sel) Color.White else Color(0xFFA3A3A3), fontSize = 12.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }

            val filtered = if (selectedCategory != null) LeisDoPovoProvider.ideias.filter { it.categoria == selectedCategory } else LeisDoPovoProvider.ideias
            items(filtered, key = { it.id }) { ideia ->
                var expanded by remember { mutableStateOf(false) }
                val votedKey = "voto_${ideia.id}"
                var voted by remember { mutableStateOf(prefs.getBoolean(votedKey, false)) }
                var localApoios by remember { mutableIntStateOf(prefs.getInt("apoios_${ideia.id}", 0)) }

                Card(
                    modifier = Modifier.fillMaxWidth().animateContentSize().clickable { expanded = !expanded },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header with support count
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Box(modifier = Modifier.background(Red600.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text(ideia.categoria.uppercase(), color = Red600, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("${ideia.apoiosECidadania + localApoios} apoios", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(ideia.titulo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(ideia.resumo, color = Color(0xFFD4D4D4), fontSize = 13.sp, lineHeight = 18.sp)

                        // Pros/Cons preview
                        Spacer(modifier = Modifier.height(6.dp))
                        Row {
                            Text("A favor: ${ideia.prosArgs.size}", color = Color(0xFF22C55E), fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Contra: ${ideia.contrasArgs.size}", color = Red600, fontSize = 11.sp)
                        }

                        // Expanded content
                        if (expanded) {
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("COMO TE AFETA:", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(ideia.comoTeAfeta, color = Color(0xFFD4D4D4), fontSize = 13.sp, lineHeight = 18.sp)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("A FAVOR:", color = Color(0xFF22C55E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            ideia.prosArgs.forEach { Text("  + $it", color = Color(0xFFD4D4D4), fontSize = 12.sp) }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("CONTRA (honestidade):", color = Red600, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            ideia.contrasArgs.forEach { Text("  - $it", color = Color(0xFFD4D4D4), fontSize = 12.sp) }

                            if (ideia.comparacaoInternacional.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("NO MUNDO:", color = Color(0xFF60A5FA), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(ideia.comparacaoInternacional, color = Color(0xFFD4D4D4), fontSize = 12.sp, lineHeight = 18.sp)
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No e-Cidadania: ${ideia.apoiosECidadania} apoios. Status: ${ideia.statusOriginal}.", color = Color(0xFF737373), fontSize = 11.sp)
                        }

                        // Action buttons
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    if (!voted) {
                                        voted = true
                                        localApoios++
                                        prefs.edit().putBoolean(votedKey, true).putInt("apoios_${ideia.id}", localApoios).apply()
                                    }
                                },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if (voted) Color(0xFF22C55E) else Red600),
                                enabled = !voted
                            ) {
                                Text(if (voted) "Apoiado ✓" else "APOIAR", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ideia.linkECidadania)))
                                },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF404040))
                            ) {
                                Text("e-Cidadania →", color = Color(0xFFA3A3A3), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
