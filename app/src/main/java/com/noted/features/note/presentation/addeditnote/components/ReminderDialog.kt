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
import com.noted.features.note.presentation.addeditnote.uimodel.ReminderUiModel
import com.noted.features.reminder.domain.model.Day
import com.noted.features.reminder.domain.model.Repeat
import com.noted.features.reminder.domain.model.Time
import com.noted.ui.components.ErrorText
import com.vanpra.composematerialdialogs.MaterialDialogScope
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun ReminderDialog(
    reminderUiModel: ReminderUiModel,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, LocalTime, Repeat) -> Unit,
    onEntered: (LocalDate, LocalTime) -> Unit,
    onDelete: () -> Unit,
    error: Boolean,
    deleteButton: Boolean,
) {
    var repeat by remember { mutableStateOf(Repeat.Once) }
    val nowDate = remember { LocalDate.now() }
    var pickedDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    var date by remember { mutableStateOf(reminderUiModel.dateTime.toLocalDate()) }
    var time by remember { mutableStateOf(reminderUiModel.dateTime.toLocalTime()) }

    LaunchedEffect(key1 = error) {
        onEntered(date, time)
    }

    fun onPickerOkClicked() {
        date = pickedDateTime.toLocalDate()
        time = pickedDateTime.toLocalTime()
        onEntered(date, time)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            FilledTonalButton(
                onClick = { onConfirm.invoke(date, time, repeat) },
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
                    Text(text = reminderUiModel.formattedDateTime)

                    var dateDropdownExpanded by remember { mutableStateOf(false) }
                    val yearRange = remember { IntRange(nowDate.year, 2100) }
                    ReminderDropdown(
                        items = Day.values(),
                        onOrdinaryItemSelected = { day ->
                            date = LocalDate.now().plusDays(day.toLong())
                            time = pickedDateTime.toLocalTime()
                            onEntered(date, time)
                        },
                        specialItem = Day.OtherDay,
                        onPickerOkCLicked = ::onPickerOkClicked,
                        expanded = dateDropdownExpanded,
                        setExpanded = { dateDropdownExpanded = !dateDropdownExpanded },
                    ) {
                        datepicker(
                            initialDate = pickedDateTime.toLocalDate(),
                            yearRange = yearRange,
                            allowedDateValidator = { select -> select >= nowDate },
                        ) { picked ->
                            pickedDateTime = LocalDateTime.of(picked, pickedDateTime.toLocalTime())
                            dateDropdownExpanded = false
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    var timeDropdownExpanded by remember { mutableStateOf(false) }
                    ReminderDropdown(
                        items = Time.values(),
                        onOrdinaryItemSelected = { timeItem ->
                            date = pickedDateTime.toLocalDate()
                            time = LocalTime.of(timeItem.getHour(), timeItem.getMinute())
                            onEntered(date, time)
                        },
                        specialItem = Time.Other,
                        onPickerOkCLicked = ::onPickerOkClicked,
                        expanded = timeDropdownExpanded,
                        setExpanded = { timeDropdownExpanded = !timeDropdownExpanded },
                    ) {
                        timepicker(
                            initialTime = pickedDateTime.toLocalTime(),
                            is24HourClock = true,
                        ) { picked ->
                            pickedDateTime = LocalDateTime.of(pickedDateTime.toLocalDate(), picked)
                            timeDropdownExpanded = false
                        }
                    }

                    if (error) {
                        ErrorText(text = stringResource(R.string.reminder_dialog_time_error))
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    var repeatDropdownExpanded by remember { mutableStateOf(false) }
                    ReminderDropdown(
                        items = Repeat.values(),
                        onOrdinaryItemSelected = { repeatItem ->
                            repeat = repeatItem
                        },
                        expanded = repeatDropdownExpanded,
                        setExpanded = { repeatDropdownExpanded = !repeatDropdownExpanded },
                    )

                    if (deleteButton) {
                        TextButton(
                            onClick = onDelete,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = stringResource(R.string.noted_delete),
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
    onOrdinaryItemSelected: (E) -> Unit,
    specialItem: E? = null,
    onPickerOkCLicked: () -> Unit = {},
    isError: Boolean = false,
    expanded: Boolean,
    setExpanded: () -> Unit,
    picker: (@Composable MaterialDialogScope.() -> Unit)? = null,
) {
    val pickerState = rememberMaterialDialogState()

    var selectedItem by remember { mutableStateOf(items[0]) }

    DateTimePicker(
        state = pickerState,
        onPickerOkCLicked = onPickerOkCLicked,
    ) {
        picker?.invoke(this)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { setExpanded() }
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
            onDismissRequest = setExpanded,
        ) {
            items.forEach { selectedOption ->
                DropdownMenuItem(
                    text = { Text(text = selectedOption.name) },
                    onClick = {
                        selectedItem = selectedOption
                        if (selectedOption != specialItem) {
                            onOrdinaryItemSelected(selectedOption)
                            setExpanded()
                        } else {
                            pickerState.show()
                        }
                    },
                )
            }
        }
    }
}
