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
                value = state.noteTitleTextFieldValue,
                hint = "Enter title...",
                onValueChange = {
                    viewModel.onEvent(AddEditNoteScreenEvent.EnteredTitle(it.text))
                },
                onFocusChange = {},
                textStyle = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                value = state.noteContentTextFieldValue,
                hint = "Enter content...",
                onValueChange = {
                    viewModel.onEvent(AddEditNoteScreenEvent.EnteredContent(it.text))
                },
                onFocusChange = {},
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
