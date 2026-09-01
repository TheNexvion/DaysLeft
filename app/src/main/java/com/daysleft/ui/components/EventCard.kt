package com.daysleft.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daysleft.data.local.Event
import com.daysleft.ui.theme.AppTheme
import com.daysleft.util.DateUtils
import com.daysleft.util.EventStatus
import kotlin.math.abs

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = DateUtils.eventStatus(event.date)
    val daysUntil = DateUtils.daysUntil(event.date)
    val formattedDate = DateUtils.formatDate(event.date)
    val statusColors = AppTheme.statusColors
    val haptic = LocalHapticFeedback.current

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "card_press_scale"
    )

    val cardColors = when (status) {
        EventStatus.URGENT -> CardDefaults.cardColors(
            containerColor = statusColors.urgentContainer
        )
        EventStatus.TODAY -> CardDefaults.cardColors(
            containerColor = statusColors.todayContainer
        )
        EventStatus.UPCOMING -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
        EventStatus.PASSED -> CardDefaults.cardColors(
            containerColor = statusColors.passedContainer
        )
    }

    val border = when (status) {
        EventStatus.URGENT -> BorderStroke(1.dp, statusColors.urgentBorder)
        EventStatus.TODAY -> BorderStroke(1.dp, statusColors.todayBorder)
        EventStatus.UPCOMING -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        EventStatus.PASSED -> BorderStroke(1.dp, statusColors.passedBorder)
    }

    val a11yDescription = when (status) {
        EventStatus.URGENT -> "${event.title}, urgent: $daysUntil days left on $formattedDate, tap to view details"
        EventStatus.TODAY -> "${event.title}, happening today, tap to view details"
        EventStatus.UPCOMING -> "${event.title}, $daysUntil days left on $formattedDate, tap to view details"
        EventStatus.PASSED -> "${event.title}, passed ${abs(daysUntil)} days ago on $formattedDate, tap to view details"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(AppTheme.shapes.Card)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
            .semantics {
                contentDescription = a11yDescription
            },
        shape = AppTheme.shapes.Card,
        colors = cardColors,
        border = border,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Title and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                StatusBadge(status = status, daysUntil = daysUntil)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Countdown Value Row
            when (status) {
                EventStatus.URGENT, EventStatus.UPCOMING -> {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = daysUntil.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (status == EventStatus.URGENT) statusColors.urgent else statusColors.upcoming,
                            lineHeight = 44.sp
                        )
                        Text(
                            text = if (daysUntil == 1L) "DAY LEFT" else "DAYS LEFT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (status == EventStatus.URGENT) statusColors.urgent else MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
                EventStatus.TODAY -> {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "TODAY",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusColors.today,
                            lineHeight = 44.sp
                        )
                        Text(
                            text = "IS THE DAY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColors.today,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
                EventStatus.PASSED -> {
                    val daysAgo = abs(daysUntil)
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = daysAgo.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusColors.passed,
                            lineHeight = 44.sp
                        )
                        Text(
                            text = if (daysAgo == 1L) "DAY AGO" else "DAYS AGO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColors.textTertiary,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Row: Formatted Date
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusBadge(
    status: EventStatus,
    daysUntil: Long
) {
    val statusColors = AppTheme.statusColors

    val config = when (status) {
        EventStatus.URGENT -> {
            val labelText = if (daysUntil == 1L) "TOMORROW" else "$daysUntil DAYS"
            StatusBadgeConfig(statusColors.urgentContainer, statusColors.urgent, labelText, Icons.Filled.LocalFireDepartment)
        }
        EventStatus.TODAY -> {
            StatusBadgeConfig(statusColors.todayContainer, statusColors.today, "TODAY", Icons.Filled.AutoAwesome)
        }
        EventStatus.UPCOMING -> {
            StatusBadgeConfig(statusColors.upcomingContainer, statusColors.upcoming, "UPCOMING", Icons.Filled.CalendarMonth)
        }
        EventStatus.PASSED -> {
            StatusBadgeConfig(statusColors.passedContainer, statusColors.passed, "PASSED", Icons.Filled.Check)
        }
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(config.bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = config.icon,
                contentDescription = null,
                tint = config.textColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = config.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = config.textColor,
                letterSpacing = 0.8.sp
            )
        }
    }
}

private data class StatusBadgeConfig(
    val bg: Color,
    val textColor: Color,
    val label: String,
    val icon: ImageVector
)
