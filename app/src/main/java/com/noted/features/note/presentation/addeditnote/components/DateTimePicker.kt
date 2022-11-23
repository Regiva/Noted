package com.noted.features.note.presentation.addeditnote.components

import androidx.compose.runtime.Composable
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogScope
import com.vanpra.composematerialdialogs.MaterialDialogState

@Composable
fun DateTimePicker(
    state: MaterialDialogState,
    onPickerOkCLicked: () -> Unit,
    picker: @Composable MaterialDialogScope.() -> Unit,
) {
    MaterialDialogPicker(
        dialogState = state,
        onPickerOkCLicked = onPickerOkCLicked,
    ) {
        picker()
    }
}

@Composable
fun MaterialDialogPicker(
    dialogState: MaterialDialogState,
    onPickerOkCLicked: () -> Unit,
    content: @Composable MaterialDialogScope.() -> Unit
) {
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(
                res = android.R.string.ok,
                onClick = onPickerOkCLicked,
            )
            negativeButton(
                res = android.R.string.cancel,
                onClick = dialogState::hide,
            )
        }
    ) {
        content()
    }
}
