package com.noted.features.note.presentation.addeditnote

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noted.R
import com.noted.features.note.domain.model.Note
import com.noted.features.note.presentation.addeditnote.components.ReminderDialog
import com.noted.features.note.presentation.addeditnote.components.TransparentHintTextField
import com.noted.ui.icon.NotedIcons
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    noteColor: Int,
    viewModel: AddEditNoteViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsState()

    val noteBackgroundAnimatable = remember {
        Animatable(
            Color(if (noteColor != -1) noteColor else viewModel.state.noteColor.toArgb())
        )
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.SaveNote -> {
                    navController.navigateUp()
                }
                is AddEditNoteViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(AddEditNoteScreenEvent.SaveNote) },
            ) {
                Icon(
                    imageVector = NotedIcons.Save,
                    contentDescription = stringResource(R.string.noted_save_note),
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(noteBackgroundAnimatable.value)
                .padding(16.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { viewModel.onEvent(AddEditNoteScreenEvent.OpenCloseReminderDialog) }
                ) {
                    Icon(
                        imageVector = if (state.reminder != null) {
                            NotedIcons.AddAlert
                        } else {
                            NotedIcons.Outlined.AddAlert
                        },
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = stringResource(R.string.noted_pin_note),
                    )
                }
            }
            if (state.reminderDialogState.visible) {
                ReminderDialog(
                    onDismiss = {
                        viewModel.onEvent(AddEditNoteScreenEvent.OpenCloseReminderDialog)
                    },
                    onConfirm = { day, time, repeat ->
                        viewModel.onEvent(AddEditNoteScreenEvent.AddReminder(day, time, repeat))
                    },
                    onEntered = { day, time ->
                        viewModel.onEvent(AddEditNoteScreenEvent.EnteredReminder(day, time))
                    },
                    onDelete = {
                        viewModel.onEvent(AddEditNoteScreenEvent.DeleteReminder)
                    },
                    error = state.reminderDialogState.error,
                    deleteButton = state.reminderDialogState.deleteButton,
                )
            }
            ColorSection(
                noteColor = state.noteColor,
                onChangeColor = { color ->
                    viewModel.onEvent(AddEditNoteScreenEvent.ChangeColor(color))
                },
                onAnimateColor = { color ->
                    scope.launch {
                        noteBackgroundAnimatable.animateTo(
                            targetValue = color,
                            animationSpec = tween(
                                durationMillis = 500,
                            )
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                text = state.noteTitle,
                hint = "Enter title...",
                onValueChange = {
                    viewModel.onEvent(AddEditNoteScreenEvent.EnteredTitle(it))
                },
                textStyle = MaterialTheme.typography.titleMedium,
            )
            TransparentHintTextField(
                text = state.noteContent,
                hint = "Enter content...",
                onValueChange = {
                    viewModel.onEvent(AddEditNoteScreenEvent.EnteredContent(it))
                },
                textStyle = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ColorSection(
    noteColor: Color,
    onChangeColor: (Color) -> Unit,
    onAnimateColor: (Color) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Note.noteColors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .shadow(15.dp, CircleShape)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = 3.dp,
                        color = if (noteColor == color) {
                            Color.Black
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape,
                    )
                    .clickable {
                        onAnimateColor.invoke(color)
                        onChangeColor.invoke(color)
                    },
            )
        }
    }
}
