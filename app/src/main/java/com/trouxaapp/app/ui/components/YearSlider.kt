package com.trouxaapp.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trouxaapp.app.data.local.DataProvider
import com.trouxaapp.app.ui.theme.Red600
import com.trouxaapp.app.ui.theme.SurfaceVariant
import kotlin.math.roundToInt

private val labelYears = setOf(2000, 2004, 2008, 2012, 2016, 2020, 2024, 2026)

@Composable
fun YearSlider(
    selectedYear: Int,
    onYearChanged: (Int) -> Unit,
    onYearTapped: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var sliderValue by remember { mutableFloatStateOf(selectedYear.toFloat()) }
    var isDragging by remember { mutableStateOf(false) }
    var lastHapticYear by remember { mutableIntStateOf(selectedYear) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(selectedYear) {
        if (!isDragging) sliderValue = selectedYear.toFloat()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                "Linha do tempo",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                sliderValue.roundToInt().toString() + " ▾",
                style = MaterialTheme.typography.headlineMedium,
                color = Red600,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onYearTapped() }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        // Slider - larger touch target via interactionSource
        Slider(
            value = sliderValue,
            onValueChange = { value ->
                isDragging = true
                sliderValue = value
                val year = value.roundToInt()
                if (year != lastHapticYear) {
                    lastHapticYear = year
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onYearChanged(year)
            },
            onValueChangeFinished = { isDragging = false },
            valueRange = DataProvider.START_YEAR.toFloat()..DataProvider.CURRENT_YEAR.toFloat(),
            steps = DataProvider.CURRENT_YEAR - DataProvider.START_YEAR - 1,
            colors = SliderDefaults.colors(
                thumbColor = Red600, activeTrackColor = Red600, activeTickColor = Red600,
                inactiveTrackColor = SurfaceVariant, inactiveTickColor = SurfaceVariant
            ),
            interactionSource = remember { MutableInteractionSource() },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Year labels - minimum 12sp
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val displayYear = sliderValue.roundToInt()
            labelYears.forEach { year ->
                val isSelected = year == displayYear
                Text(
                    text = if (isSelected) year.toString() else "'${year.toString().takeLast(2)}",
                    fontSize = if (isSelected) 14.sp else 12.sp,
                    color = if (isSelected) Red600 else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                )
            }
        }
    }
}
