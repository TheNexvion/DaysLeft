package com.daysleft.ui.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daysleft.ui.theme.Primary
import com.daysleft.ui.theme.SecondaryContainer
import com.daysleft.ui.theme.Surface
import com.daysleft.ui.theme.SurfaceContainerLowest
import com.daysleft.util.DateUtils
import com.daysleft.util.EventStatus
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onEventDeleted: () -> Unit,
    viewModel: EventDetailsViewModel = viewModel(factory = EventDetailsViewModel.factory(eventId))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            onEventDeleted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Event Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "More options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Event") },
                                onClick = {
                                    showOverflowMenu = false
                                    onNavigateToEdit(eventId)
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.Edit, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Delete Event",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    showDeleteDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (uiState.event != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = { onNavigateToEdit(eventId) },
                        shape = RoundedCornerShape(percent = 50),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Edit Event",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            val event = uiState.event
            if (event != null) {
                val status = DateUtils.eventStatus(event.date)
                val daysLeft = DateUtils.daysUntil(event.date)
                val formattedDate = DateUtils.formatDate(event.date)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Event Title
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hero Countdown Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = SecondaryContainer),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp, horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            when (status) {
                                EventStatus.UPCOMING -> {
                                    Text(
                                        text = daysLeft.toString(),
                                        fontSize = 57.sp,
                                        lineHeight = 64.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Primary
                                    )
                                    Text(
                                        text = if (daysLeft == 1L) "DAY LEFT" else "DAYS LEFT",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Primary,
                                        letterSpacing = 2.sp
                                    )
                                }
                                EventStatus.TODAY -> {
                                    Text(
                                        text = "Today",
                                        fontSize = 57.sp,
                                        lineHeight = 64.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Primary
                                    )
                                }
                                EventStatus.PASSED -> {
                                    Text(
                                        text = abs(daysLeft).toString(),
                                        fontSize = 57.sp,
                                        lineHeight = 64.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Primary
                                    )
                                    Text(
                                        text = if (abs(daysLeft) == 1L) "DAY AGO" else "DAYS AGO",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Primary,
                                        letterSpacing = 2.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Information Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Event date",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete event?") },
            text = { Text("This event will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteEvent()
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
