package com.trouxaapp.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.util.EngagementManager

private val UFS = listOf(
    "AC","AL","AM","AP","BA","CE","DF","ES","GO","MA","MG","MS","MT",
    "PA","PB","PE","PI","PR","RJ","RN","RO","RR","RS","SC","SE","SP","TO"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onAboutClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val engagement = remember { EngagementManager(context) }

    var uf by remember { mutableStateOf(engagement.userUf) }
    var salary by remember { mutableStateOf(if (engagement.userSalary > 0) String.format("%.0f", engagement.userSalary) else "") }
    var showUfDropdown by remember { mutableStateOf(false) }
    var cafe by remember { mutableStateOf(engagement.notifCafe) }
    var almoco by remember { mutableStateOf(engagement.notifAlmoco) }
    var trabalho by remember { mutableStateOf(engagement.notifTrabalho) }
    var noite by remember { mutableStateOf(engagement.notifNoite) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CONFIGURAÇÕES", color = Color.White, fontWeight = FontWeight.Bold) },
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
            // SEU ESTADO
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Seu estado", color = Color(0xFF737373), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        androidx.compose.foundation.layout.Box {
                            OutlinedButton(
                                onClick = { showUfDropdown = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (uf.isEmpty()) "Selecionar estado" else uf, color = Color.White)
                            }
                            DropdownMenu(expanded = showUfDropdown, onDismissRequest = { showUfDropdown = false }) {
                                UFS.forEach { u ->
                                    DropdownMenuItem(
                                        text = { Text(u) },
                                        onClick = {
                                            uf = u
                                            engagement.userUf = u
                                            showUfDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SEU SALARIO
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Seu salário (opcional)", color = Color(0xFF737373), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = salary,
                            onValueChange = {
                                salary = it.filter { c -> c.isDigit() || c == '.' }
                                salary.toDoubleOrNull()?.let { v -> engagement.userSalary = v }
                            },
                            label = { Text("R\$ mensal") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Red600,
                                unfocusedBorderColor = Color(0xFF404040),
                                cursorColor = Red600
                            )
                        )
                    }
                }
            }

            // NOTIFICACOES
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Notificações", color = Color(0xFF737373), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        NotifRow("Café (7h)", cafe) { cafe = it; engagement.notifCafe = it }
                        HorizontalDivider(color = Color(0xFF262626), modifier = Modifier.padding(vertical = 6.dp))
                        NotifRow("Almoço (12h)", almoco) { almoco = it; engagement.notifAlmoco = it }
                        HorizontalDivider(color = Color(0xFF262626), modifier = Modifier.padding(vertical = 6.dp))
                        NotifRow("Trabalho (18h)", trabalho) { trabalho = it; engagement.notifTrabalho = it }
                        HorizontalDivider(color = Color(0xFF262626), modifier = Modifier.padding(vertical = 6.dp))
                        NotifRow("Noite (20:30)", noite) { noite = it; engagement.notifNoite = it }
                    }
                }
            }

            // ABOUT
            item {
                Button(
                    onClick = onAboutClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Text("Sobre o app", color = Color.White)
                }
            }

            // VERSION
            item {
                Text("Versão 3.0.0", color = Color(0xFF505050), fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun NotifRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 14.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Red600,
                uncheckedTrackColor = Color(0xFF333333)
            )
        )
    }
}
