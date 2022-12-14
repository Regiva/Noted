package com.noted.features.note.presentation.addeditnote

import android.content.Intent
import android.provider.Settings
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
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.back
import com.noted.R
import com.noted.core.navigation.LocalSnackbarHostState
import com.noted.core.navigation.utils.context
import com.noted.core.navigation.utils.navContainer
import com.noted.features.note.di.AddEditNoteScreenEntryPoint
import com.noted.features.note.domain.model.Note
import com.noted.features.note.presentation.addeditnote.components.ReminderDialog
import com.noted.features.note.presentation.addeditnote.components.TransparentHintTextField
import com.noted.ui.icon.NotedIcons
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
class AddEditNoteScreen(
    override val screenKey: ScreenKey = generateScreenKey(),
    private val noteId: Int? = -1,
    private val noteColor: Int = -1,
) : Screen {

    @Composable
    override fun Content() {
        val context = this.context
        val navigator = this.navContainer

        // TODO: move this to generified ext?
        val factory = remember {
            EntryPointAccessors.fromApplication(
                context = context,
                entryPoint = AddEditNoteScreenEntryPoint::class.java,
            ).addEditNoteVmFactory
        }
        // TODO: move this to generified ext?
        val viewModel: AddEditNoteViewModel = remember {
            AddEditNoteViewModel.providesFactory(
                assistedFactory = factory,
                noteId = noteId,
            ).create(AddEditNoteViewModel::class.java)
        }
        AddEditNoteScreenContent(
            noteColor = noteColor,
            viewModel = viewModel,
            openSettings = {
                if (!viewModel.isVersionLowerThanS()) {
                    context.startActivity(
                        Intent().apply {
                            action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        }
                    )
                }
            },
            navigateUp = navigator::back,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreenContent(
    viewModel: AddEditNoteViewModel,
    noteColor: Int,
    openSettings: () -> Unit,
    navigateUp: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()
    val snackbarHostState = LocalSnackbarHostState.current

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
                    navigateUp()
                }
                is AddEditNoteViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is AddEditNoteViewModel.UiEvent.ShowAlarmRationaleSnackbar -> {
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = "Please grant us permission to properly schedule alarms",
                        actionLabel = "Settings",
                    )
                    if (snackbarResult == SnackbarResult.ActionPerformed) {
                        openSettings()
                    }
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
                        imageVector = if (state.reminderPresent) {
                            NotedIcons.AddAlert
                        } else {
                            NotedIcons.Outlined.AddAlert
                        },
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = stringResource(R.string.noted_pin_note),
                    )
                }
            }
            if (state.dialogVisible) {
                ReminderDialog(
                    reminderUiModel = state.reminderUi,
                    onDismiss = {
                        viewModel.onEvent(AddEditNoteScreenEvent.OpenCloseReminderDialog)
                    },
                    onConfirm = { pickedDate, pickedTime, repeat ->
                        viewModel.onEvent(AddEditNoteScreenEvent.AddReminder(pickedDate, pickedTime, repeat))
                    },
                    onEntered = { pickedDate, pickedTime ->
                        viewModel.onEvent(AddEditNoteScreenEvent.EnteredReminder(pickedDate, pickedTime))
                    },
                    onDelete = {
                        viewModel.onEvent(AddEditNoteScreenEvent.DeleteReminder)
                    },
                    error = state.dialogDateTimeError,
                    deleteButton = state.reminderPresent,
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
