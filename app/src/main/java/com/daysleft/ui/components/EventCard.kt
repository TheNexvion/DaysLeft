package com.daysleft.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daysleft.data.local.Event
import com.daysleft.ui.theme.Primary
import com.daysleft.ui.theme.PrimaryContainer
import com.daysleft.ui.theme.SecondaryContainer
import com.daysleft.ui.theme.SurfaceContainerLow
import com.daysleft.ui.theme.SurfaceContainerLowest
import com.daysleft.ui.theme.SurfaceDim
import com.daysleft.ui.theme.SurfaceVariant
import com.daysleft.util.DateUtils
import com.daysleft.util.EventStatus

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = DateUtils.eventStatus(event.date)
    val daysLeft = DateUtils.daysUntil(event.date)
    val formattedDate = DateUtils.formatDate(event.date)

    when (status) {
        EventStatus.UPCOMING -> {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = daysLeft.toString(),
                            fontSize = 45.sp,
                            lineHeight = 52.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            text = if (daysLeft == 1L) "day left" else "days left",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            }
        }

        EventStatus.TODAY -> {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SecondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Today, $formattedDate",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerLowest),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Today",
                        fontSize = 45.sp,
                        lineHeight = 52.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }
        }

        EventStatus.PASSED -> {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Normal,
                                textDecoration = TextDecoration.LineThrough,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceDim),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Event passed",
                        style = MaterialTheme.typography.titleLarge,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
