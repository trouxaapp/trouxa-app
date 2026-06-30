package com.trouxaapp.app.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.ui.theme.Amber500
import com.trouxaapp.app.ui.theme.Blue400
import com.trouxaapp.app.ui.theme.OnSurfaceFaint
import com.trouxaapp.app.ui.theme.OnSurfaceVariant
import com.trouxaapp.app.ui.theme.SurfaceVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Shared UI vocabulary. The data-honesty pieces live here so every screen
 * renders sources and timestamps the same way.
 */

/** "Fonte: Camara dos Deputados · dado de 09/06/2026", tappable, opens the source. */
@Composable
fun FonteLinha(
    fonte: String,
    url: String,
    dataReferencia: String = "",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(enabled = url.isNotBlank()) {
                runCatching {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            }
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        Text(
            text = buildString {
                append("Fonte: $fonte")
                if (dataReferencia.isNotBlank()) append(" · dado de $dataReferencia")
            },
            color = Blue400,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            modifier = Modifier.weight(1f, fill = false)
        )
        if (url.isNotBlank()) {
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "Abrir fonte",
                tint = Blue400,
                modifier = Modifier.padding(start = 4.dp).align(Alignment.CenterVertically)
            )
        }
    }
}

/** "Dados da Câmara · atualizados há 12 min". Honest freshness, never "AO VIVO". */
@Composable
fun AtualizadoEm(timestamp: Long, prefixo: String, modifier: Modifier = Modifier) {
    Text(
        text = if (timestamp <= 0) "$prefixo · sem registro de atualização"
        else "$prefixo · ${formatRelative(timestamp)}",
        color = OnSurfaceFaint,
        fontSize = 11.sp,
        modifier = modifier
    )
}

fun formatRelative(timestamp: Long, agora: Long = System.currentTimeMillis()): String {
    val diff = agora - timestamp
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "atualizado agora há pouco"
        diff < TimeUnit.HOURS.toMillis(1) -> "atualizado há ${TimeUnit.MILLISECONDS.toMinutes(diff)} min"
        diff < TimeUnit.HOURS.toMillis(48) -> "atualizado há ${TimeUnit.MILLISECONDS.toHours(diff)} h"
        else -> "atualizado em " + SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(timestamp))
    }
}

/** Formats ISO dates (2026-06-09 or 2026-06-09T14:30) as dd/MM/yyyy [HH:mm]. */
fun formatIsoDate(iso: String): String {
    if (iso.isBlank()) return ""
    return try {
        if (iso.length >= 16 && iso.contains('T')) {
            val d = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).parse(iso.take(16))
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(d!!)
        } else {
            val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(iso.take(10))
            SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(d!!)
        }
    } catch (_: Exception) {
        iso
    }
}

@Composable
fun TemaChip(tema: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(SurfaceVariant)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            tema,
            color = OnSurfaceVariant,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Activity badge. Only renders for genuinely active bills (>= 40) and the
 * score is explained on tap via the motivos dialog in the detail screen.
 */
@Composable
fun TemperaturaBadge(valor: Int, modifier: Modifier = Modifier) {
    if (valor < 40) return
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Amber500.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            "EM MOVIMENTO",
            color = Amber500,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun EmptyState(titulo: String, texto: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth().padding(32.dp)
    ) {
        Text(titulo, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Text(
            texto,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun SectionLabel(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto.uppercase(Locale("pt", "BR")),
        color = OnSurfaceFaint,
        fontSize = 11.sp,
        letterSpacing = 1.5.sp,
        modifier = modifier.padding(bottom = 8.dp)
    )
}
