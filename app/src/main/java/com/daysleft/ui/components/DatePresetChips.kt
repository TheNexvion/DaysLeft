package com.daysleft.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.daysleft.ui.theme.AppTheme
import com.daysleft.util.DateUtils
import java.time.LocalDate

data class DatePreset(
    val label: String,
    val date: LocalDate
)

@Composable
fun DatePresetChips(
    selectedDate: LocalDate?,
    onSelectDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val today = remember { LocalDate.now() }

    val presets = remember(today) {
        listOf(
            DatePreset("Tomorrow", DateUtils.presetTomorrow(today)),
            DatePreset("This Weekend", DateUtils.presetThisWeekend(today)),
            DatePreset("In 1 Week", DateUtils.presetInOneWeek(today)),
            DatePreset("In 1 Month", DateUtils.presetInOneMonth(today))
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        presets.forEach { preset ->
            val isSelected = selectedDate == preset.date

            FilterChip(
                selected = isSelected,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelectDate(preset.date)
                },
                label = {
                    Text(
                        text = preset.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                shape = RoundedCornerShape(percent = 50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                    borderWidth = 1.dp,
                    enabled = true,
                    selected = isSelected
                ),
                modifier = Modifier.height(36.dp)
            )
        }
    }
}
