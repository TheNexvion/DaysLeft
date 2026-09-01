package com.daysleft.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daysleft.ui.theme.AppTheme
import com.daysleft.util.DateUtils
import com.daysleft.util.EventStatus
import java.time.LocalDate
import kotlin.math.abs

@Composable
fun CountdownHero(
    eventDate: LocalDate,
    modifier: Modifier = Modifier
) {
    val status = DateUtils.eventStatus(eventDate)
    val daysUntil = DateUtils.daysUntil(eventDate)
    val detailedBreakdown = DateUtils.getDetailedCountdown(eventDate)
    val statusColors = AppTheme.statusColors

    val config = when (status) {
        EventStatus.URGENT -> CountdownHeroConfig(
            containerColor = statusColors.urgentContainer,
            borderColor = statusColors.urgentBorder,
            primaryColor = statusColors.urgent,
            icon = Icons.Filled.LocalFireDepartment
        )
        EventStatus.TODAY -> CountdownHeroConfig(
            containerColor = statusColors.todayContainer,
            borderColor = statusColors.todayBorder,
            primaryColor = statusColors.today,
            icon = Icons.Filled.AutoAwesome
        )
        EventStatus.UPCOMING -> CountdownHeroConfig(
            containerColor = statusColors.upcomingContainer,
            borderColor = statusColors.upcomingBorder,
            primaryColor = statusColors.upcoming,
            icon = Icons.Filled.Schedule
        )
        EventStatus.PASSED -> CountdownHeroConfig(
            containerColor = statusColors.passedContainer,
            borderColor = statusColors.passedBorder,
            primaryColor = statusColors.passed,
            icon = Icons.Filled.TaskAlt
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = AppTheme.shapes.Card,
        colors = CardDefaults.cardColors(containerColor = config.containerColor),
        border = BorderStroke(1.dp, config.borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = config.icon,
                contentDescription = null,
                tint = config.primaryColor,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (status) {
                EventStatus.URGENT, EventStatus.UPCOMING -> {
                    Text(
                        text = daysUntil.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = config.primaryColor,
                        lineHeight = 56.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (daysUntil == 1L) "DAY LEFT" else "DAYS LEFT",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = config.primaryColor,
                        letterSpacing = 2.sp
                    )
                }
                EventStatus.TODAY -> {
                    Text(
                        text = "TODAY",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = config.primaryColor,
                        lineHeight = 56.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "THE DAY IS HERE",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = config.primaryColor,
                        letterSpacing = 2.sp
                    )
                }
                EventStatus.PASSED -> {
                    val daysAgo = abs(daysUntil)
                    Text(
                        text = daysAgo.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = config.primaryColor,
                        lineHeight = 56.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (daysAgo == 1L) "DAY AGO" else "DAYS AGO",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = config.primaryColor,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Humanized breakdown / message
            Text(
                text = if (status == EventStatus.TODAY) {
                    "The day you've been waiting for is here."
                } else {
                    detailedBreakdown
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private data class CountdownHeroConfig(
    val containerColor: Color,
    val borderColor: Color,
    val primaryColor: Color,
    val icon: ImageVector
)
