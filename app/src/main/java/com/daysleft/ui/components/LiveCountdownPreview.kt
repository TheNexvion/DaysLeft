package com.daysleft.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.daysleft.ui.theme.AppTheme
import com.daysleft.util.DateUtils
import com.daysleft.util.EventStatus
import java.time.LocalDate

@Composable
fun LiveCountdownPreview(
    title: String,
    date: LocalDate?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = date != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        if (date != null) {
            val status = DateUtils.eventStatus(date)
            val countdownText = DateUtils.formatCountdown(date)
            val formattedDate = DateUtils.formatDate(date)
            val statusColors = AppTheme.statusColors

            Column(modifier = modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(2.dp)
                    )
                    Text(
                        text = "LIVE PREVIEW",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppTheme.shapes.Card,
                    colors = CardDefaults.cardColors(
                        containerColor = when (status) {
                            EventStatus.URGENT -> statusColors.urgentContainer
                            EventStatus.TODAY -> statusColors.todayContainer
                            EventStatus.UPCOMING -> MaterialTheme.colorScheme.surfaceContainer
                            EventStatus.PASSED -> statusColors.passedContainer
                        }
                    ),
                    border = BorderStroke(
                        1.dp,
                        when (status) {
                            EventStatus.URGENT -> statusColors.urgentBorder
                            EventStatus.TODAY -> statusColors.todayBorder
                            EventStatus.UPCOMING -> MaterialTheme.colorScheme.outlineVariant
                            EventStatus.PASSED -> statusColors.passedBorder
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (title.isBlank()) "Event Title" else title.trim(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (title.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = countdownText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = when (status) {
                                    EventStatus.URGENT -> statusColors.urgent
                                    EventStatus.TODAY -> statusColors.today
                                    EventStatus.UPCOMING -> statusColors.upcoming
                                    EventStatus.PASSED -> statusColors.passed
                                }
                            )

                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
