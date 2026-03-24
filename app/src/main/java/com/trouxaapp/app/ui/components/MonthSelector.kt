package com.trouxaapp.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.ui.theme.SurfaceVariant

@Composable
fun MonthSelector(
    visible: Boolean,
    year: Int,
    hasMonthlyData: Boolean,
    selectedMonth: Int?,
    onMonthSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            if (!hasMonthlyData) {
                Text(
                    text = "Dados mensais disponíveis a partir de ${DataProvider.MONTHLY_START_YEAR}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                val maxMonth = if (year == DataProvider.CURRENT_YEAR) DataProvider.CURRENT_MONTH else 12
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // "Anual" chip
                    MonthChip(
                        text = "Ano",
                        selected = selectedMonth == null,
                        onClick = { onMonthSelected(null) }
                    )

                    (1..maxMonth).forEach { month ->
                        MonthChip(
                            text = DataProvider.monthNames[month],
                            selected = selectedMonth == month,
                            onClick = { onMonthSelected(month) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Red600 else SurfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
