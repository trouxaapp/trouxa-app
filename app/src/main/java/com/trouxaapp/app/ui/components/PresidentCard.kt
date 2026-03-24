package com.trouxaapp.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.trouxaapp.app.data.model.President
import com.trouxaapp.app.ui.theme.Green500
import com.trouxaapp.app.ui.theme.Red600

private val DarkRed = Color(0xFF991B1B)

@Composable
fun PresidentCard(
    president: President,
    selectedYear: Int,
    modifier: Modifier = Modifier
) {
    // Collapsed by default - only shows photo + name + party
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Compact header - always visible
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo: try local drawable first, then URL, then initials
                val context = LocalContext.current
                val drawableId = context.resources.getIdentifier(
                    president.drawableRes, "drawable", context.packageName
                )
                if (drawableId != 0) {
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = president.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(2.dp, Red600, CircleShape)
                    )
                } else {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(president.photoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = president.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(2.dp, Red600, CircleShape),
                        loading = { InitialsAvatar(president.initials) },
                        error = { InitialsAvatar(president.initials) }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = president.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = president.party,
                            style = MaterialTheme.typography.bodySmall,
                            color = Red600,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " · ${president.startYear}–${president.endYear ?: "presente"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Year badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Red600)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = selectedYear.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Expandable details - scrollable with max height to prevent overflow
            AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(10.dp))

                    // GABINETE
                    val g = president.gabinete
                    if (g.vice.isNotEmpty()) {
                        Text("QUEM GOVERNOU", style = MaterialTheme.typography.labelLarge, color = Color(0xFF60A5FA), fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        if (g.vice.isNotEmpty()) GabRow("Vice", g.vice)
                        if (g.ministroEconomia.isNotEmpty()) GabRow("Economia", g.ministroEconomia)
                        if (g.presBCB.isNotEmpty()) GabRow("Banco Central", g.presBCB)
                        if (g.presPetrobras.isNotEmpty()) GabRow("Petrobras", g.presPetrobras)
                        if (g.decisoesPrecos.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Decisões que impactaram preços:", style = MaterialTheme.typography.labelMedium, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
                            g.decisoesPrecos.forEach { d ->
                                Text("• $d", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(start = 8.dp, top = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    FactRow("Positivo", president.facts.positivo, Green500)
                    Spacer(modifier = Modifier.height(6.dp))
                    FactRow("Negativo", president.facts.negativo, Red600)
                    Spacer(modifier = Modifier.height(6.dp))
                    FactRow("Contexto", president.facts.contexto, MaterialTheme.colorScheme.onSurfaceVariant)
                    president.facts.pontoCritico?.let {
                        Spacer(modifier = Modifier.height(6.dp))
                        FactRow("Ponto crítico", it, Red600)
                    }

                    // Absurdos
                    if (president.absurdos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "FATOS E ABSURDOS",
                            style = MaterialTheme.typography.labelLarge,
                            color = Red600,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val absurdoContext = LocalContext.current
                        president.absurdos.forEach { absurdo ->
                            val hasLink = absurdo.url.isNotEmpty()
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 3.dp)
                                    .then(if (hasLink) Modifier.clickable {
                                        try { absurdoContext.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(absurdo.url))) }
                                        catch (_: Exception) {}
                                    } else Modifier)
                            ) {
                                Text("•", color = Red600, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp, top = 1.dp))
                                Column {
                                    Text(absurdo.texto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                    Text(
                                        text = "Fonte: ${absurdo.fonte}" + if (hasLink) " ↗" else "",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (hasLink) Color(0xFF60A5FA) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Fontes: ${president.facts.fonte}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun InitialsAvatar(initials: String) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(DarkRed)
            .border(2.dp, Red600, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FactRow(label: String, text: String, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun GabRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 1.dp)) {
        Text("$label: ", style = MaterialTheme.typography.labelSmall, color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold)
        Text(value, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
    }
}
