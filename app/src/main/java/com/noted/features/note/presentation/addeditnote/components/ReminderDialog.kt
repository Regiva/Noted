package com.noted.features.note.presentation.addeditnote.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noted.R
import com.noted.features.reminder.domain.model.Day
import com.noted.features.reminder.domain.model.Repeat
import com.noted.features.reminder.domain.model.Time

@Composable
fun ReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (Day, Time, Repeat) -> Unit,
    onEntered: (Day, Time) -> Unit,
    onDelete: () -> Unit,
    error: Boolean,
    deleteButton: Boolean,
) {
    var day by remember { mutableStateOf(Day.Today) }
    var time by remember { mutableStateOf(Time.Day) }
    var repeat by remember { mutableStateOf(Repeat.Once) }

    LaunchedEffect(key1 = error) {
        onEntered(day, time)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            FilledTonalButton(
                onClick = { onConfirm.invoke(day, time, repeat) },
                enabled = !error,
            ) {
                Text(text = stringResource(R.string.noted_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.noted_cancel))
            }
        },
        title = {
            Text(
                text = stringResource(R.string.noted_add_reminder),
                style = MaterialTheme.typography.labelLarge,
            )
        },
        text = {
            Box {
                Column {
                    ReminderDropdown(
                        items = Day.values(),
                        onItemSelected = { dayItem ->
                            day = dayItem
                            onEntered(day, time)
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ReminderDropdown(
                        items = Time.values(),
                        onItemSelected = { timeItem ->
                            time = timeItem
                            onEntered(day, time)
                        },
                        isError = error,
                    )
                    if (error) {
                        Text(
                            text = stringResource(R.string.reminder_dialog_time_error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    ReminderDropdown(
                        items = Repeat.values(),
                        onItemSelected = { repeatItem ->
                            repeat = repeatItem
                            onEntered(day, time)
                        }
                    )

                    if (deleteButton) {
                        TextButton(
                            onClick = onDelete,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = "Delete",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <E : Enum<E>> ReminderDropdown(
    items: Array<E>,
    onItemSelected: (E) -> Unit,
    isError: Boolean = false,
) {
    var selectedItem by remember { mutableStateOf(items[0]) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedItem.name,
            onValueChange = {},
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                containerColor = Color.Transparent,
            ),
            isError = isError,
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { selectedOption ->
                DropdownMenuItem(
                    text = { Text(text = selectedOption.name) },
                    onClick = {
                        selectedItem = selectedOption
                        onItemSelected(selectedOption)
                        expanded = false
                    },
                )
            }
        }
    }
}
