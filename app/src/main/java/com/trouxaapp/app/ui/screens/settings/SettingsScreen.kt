package com.trouxaapp.app.ui.screens.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trouxaapp.app.data.local.SyncLogEntity
import com.trouxaapp.app.ui.components.SectionLabel
import com.trouxaapp.app.ui.components.formatRelative
import com.trouxaapp.app.ui.theme.Background
import com.trouxaapp.app.ui.theme.CardBorder
import com.trouxaapp.app.ui.theme.Green500
import com.trouxaapp.app.ui.theme.OnBackground
import com.trouxaapp.app.ui.theme.OnSurface
import com.trouxaapp.app.ui.theme.OnSurfaceFaint
import com.trouxaapp.app.ui.theme.OnSurfaceVariant
import com.trouxaapp.app.ui.theme.Red400
import com.trouxaapp.app.ui.theme.Surface
import com.trouxaapp.app.ui.theme.SurfaceVariant

// The 27 federal units. Fixed list, not data.
private val UFS = listOf(
    "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO",
    "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI",
    "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"
)

private const val GITHUB_URL = "https://github.com/trouxaapp/trouxa-app"

// Worker ids come from SyncLogEntity.worker; unknown ids fall back to the raw id
private fun workerLabel(worker: String): String = when (worker) {
    "law" -> "Projetos da Câmara"
    "indicador" -> "Indicadores do BCB"
    "feed" -> "Curadoria"
    else -> worker
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSobreClick: () -> Unit,
    onPrivacidadeClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val syncLogs by viewModel.syncLogs.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showClearDialog by remember { mutableStateOf(false) }

    // Result is intentionally ignored: the pref stays as the user set it,
    // the system simply will not deliver notifications without the grant.
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    fun requestNotifPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Configurações", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = OnBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = OnBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ---- SEU ESTADO ----
            SectionLabel("Seu estado")
            SettingsCard {
                UfDropdown(
                    selecionada = state.userUf,
                    onSelect = { viewModel.setUf(it) }
                )
                Text(
                    "Usado só pra listar os deputados do seu estado. Fica no aparelho.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // ---- NOTIFICACOES ----
            SectionLabel("Notificações")
            SettingsCard {
                SwitchRow(
                    titulo = "Acontecimentos",
                    caption = "Projeto novo em movimento na Câmara ou mudança na Selic e no dólar. " +
                        "Máximo um aviso a cada 6 horas, nunca de madrugada.",
                    checked = state.notifEventos,
                    onToggle = { on ->
                        viewModel.setNotifEventos(on)
                        if (on) requestNotifPermissionIfNeeded()
                    }
                )
                HorizontalDivider(color = CardBorder, modifier = Modifier.padding(vertical = 12.dp))
                SwitchRow(
                    titulo = "Resumo diário",
                    caption = "Um resumo por dia com dados oficiais e suas datas. " +
                        "Se não houver dado novo, não chega nada.",
                    checked = state.notifResumoDiario,
                    onToggle = { on ->
                        viewModel.setNotifResumoDiario(on)
                        if (on) requestNotifPermissionIfNeeded()
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            // ---- SAUDE DOS DADOS ----
            SectionLabel("Saúde dos dados")
            SettingsCard {
                if (syncLogs.isEmpty()) {
                    Text(
                        "Sem registro de sincronização até agora.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                } else {
                    syncLogs.forEachIndexed { index, log ->
                        if (index > 0) {
                            HorizontalDivider(
                                color = CardBorder,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                        SyncLogRow(log)
                    }
                }
                Button(
                    onClick = { viewModel.forceSyncNow() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sincronizar agora")
                }
            }

            Spacer(Modifier.height(24.dp))

            // ---- PRIVACIDADE ----
            SectionLabel("Privacidade")
            SettingsCard {
                NavRow(
                    titulo = "Política de privacidade",
                    caption = "",
                    onClick = onPrivacidadeClick
                )
                HorizontalDivider(color = CardBorder, modifier = Modifier.padding(vertical = 4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showClearDialog = true }
                        .padding(vertical = 10.dp, horizontal = 4.dp)
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Apagar dados locais",
                            style = MaterialTheme.typography.titleMedium,
                            color = Red400
                        )
                        Text(
                            "Apaga o cache e as preferências deste aparelho.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = Red400,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ---- SOBRE ----
            SectionLabel("Sobre")
            SettingsCard {
                NavRow(
                    titulo = "Sobre o app",
                    caption = "",
                    onClick = onSobreClick
                )
                HorizontalDivider(color = CardBorder, modifier = Modifier.padding(vertical = 4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            runCatching {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL))
                                )
                            }
                        }
                        .padding(vertical = 10.dp, horizontal = 4.dp)
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Código aberto",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface
                        )
                        Text(
                            "github.com/trouxaapp/trouxa-app",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = OnSurfaceFaint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                AppVersionCaption()
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = Surface,
            titleContentColor = OnSurface,
            textContentColor = OnSurfaceVariant,
            title = { Text("Apagar dados locais?") },
            text = {
                Text(
                    "Remove o cache de projetos, indicadores e deputados e limpa as " +
                        "preferências deste aparelho. Na próxima sincronização o app " +
                        "baixa os dados oficiais de novo."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearLocalData()
                    showClearDialog = false
                }) {
                    Text("Apagar", color = Red400)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancelar", color = OnSurfaceVariant)
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
private fun UfDropdown(selecionada: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceVariant)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Text(
                text = if (selecionada.isBlank()) "Não informar" else selecionada,
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Escolher estado",
                tint = OnSurfaceVariant
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Não informar") },
                onClick = {
                    onSelect("")
                    expanded = false
                }
            )
            UFS.forEach { uf ->
                DropdownMenuItem(
                    text = { Text(uf) },
                    onClick = {
                        onSelect(uf)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SwitchRow(
    titulo: String,
    caption: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = OnSurface)
            Text(
                caption,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun SyncLogRow(log: SyncLogEntity) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (log.success) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
            contentDescription = if (log.success) "Sincronização concluída" else "Erro na sincronização",
            tint = if (log.success) Green500 else Red400,
            modifier = Modifier.size(18.dp)
        )
        Column(
            Modifier
                .padding(start = 10.dp)
                .weight(1f)
        ) {
            Text(
                workerLabel(log.worker),
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface
            )
            Text(
                "${formatRelative(log.timestamp)} · itens: ${log.itemsChanged}",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
            // Real error text from the worker, shown as-is. No sugarcoating.
            if (log.error.isNotBlank()) {
                Text(
                    log.error,
                    style = MaterialTheme.typography.bodySmall,
                    color = Red400,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun NavRow(titulo: String, caption: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = OnSurface)
            if (caption.isNotBlank()) {
                Text(
                    caption,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = OnSurfaceFaint
        )
    }
}

@Composable
private fun AppVersionCaption() {
    val context = LocalContext.current
    val versionName = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull().orEmpty()
    }
    Text(
        text = if (versionName.isBlank()) "Versão não disponível" else "Versão $versionName",
        style = MaterialTheme.typography.bodySmall,
        color = OnSurfaceFaint,
        modifier = Modifier.padding(top = 12.dp)
    )
}
