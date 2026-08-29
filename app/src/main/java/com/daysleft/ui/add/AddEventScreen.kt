package com.daysleft.ui.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daysleft.ui.theme.Primary
import com.daysleft.ui.theme.Surface
import com.daysleft.util.DateUtils
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEventViewModel = viewModel(factory = AddEventViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Event",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Scrollable Form Content (Fills available space)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Event Name
                Text(
                    text = "Event name",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::updateTitle,
                    placeholder = { Text("e.g. My Birthday") },
                    isError = uiState.titleError != null,
                    supportingText = uiState.titleError?.let { error ->
                        { Text(error) }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Event Date
                Text(
                    text = "Event date",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.date?.let { DateUtils.formatDate(it) } ?: "",
                        onValueChange = { },
                        placeholder = { Text("Select a date") },
                        readOnly = true,
                        isError = uiState.dateError != null,
                        supportingText = uiState.dateError?.let { error ->
                            { Text(error) }
                        },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarToday,
                                    contentDescription = "Select date",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Clickable transparent overlay across date field
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(bottom = if (uiState.dateError != null) 16.dp else 0.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { showDatePicker = true }
                            )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom Save Event Button (Always anchored at bottom, elevated smoothly by imePadding)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = viewModel::saveEvent,
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Save Event",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.date?.atStartOfDay()
                ?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            viewModel.updateDate(selectedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
