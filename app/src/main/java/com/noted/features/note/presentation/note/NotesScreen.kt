package com.noted.features.note.presentation.note

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noted.R
import com.noted.core.navigation.Screen
import com.noted.features.note.domain.model.Note
import com.noted.features.note.domain.util.NoteOrder
import com.noted.features.note.presentation.note.components.NoteItem
import com.noted.features.note.presentation.note.components.OrderSection
import com.noted.ui.icon.NotedIcons
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: NotesViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddEditNoteScreen.route) },
            ) {
                Icon(
                    imageVector = NotedIcons.Add,
                    contentDescription = stringResource(R.string.noted_add_note),
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            NotesScreenHeader(
                onToggleOrder = { viewModel.onEvent(NotesScreenEvents.ToggleOrderSection) }
            )
            NotesScreenOrderSection(
                isOrderSectionVisible = state.isOrderSectionVisible,
                noteOrder = state.noteOrder,
                onOrderChange = { noteOrder ->
                    viewModel.onEvent(NotesScreenEvents.Order(noteOrder))
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            NotesScreenNotesList(
                notes = state.notes,
                onNoteClick = { note ->
                    // TODO: open note with animation
                    // TODO: change params
                    navController.navigate(Screen.AddEditNoteScreen.route +
                            "?noteId=${note.id}&noteColor=${note.color}")
                },
                onDeleteNoteClick = { note ->
                    viewModel.onEvent(NotesScreenEvents.DeleteNote(note))
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Note deleted",
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Short,
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.onEvent(NotesScreenEvents.RestoreNote)
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun NotesScreenNotesList(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onDeleteNoteClick: (Note) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(notes) { note ->
            NoteItem(
                note = note,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNoteClick(note) },
                onDeleteClick = { onDeleteNoteClick(note) },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NotesScreenHeader(
    onToggleOrder: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.noted_your_notes),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        IconButton(
            onClick = onToggleOrder,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground,
            )
        ) {
            Icon(
                imageVector = NotedIcons.Sort,
                contentDescription = stringResource(R.string.noted_sort),
            )
        }
    }
}

@Composable
private fun NotesScreenOrderSection(
    isOrderSectionVisible: Boolean,
    noteOrder: NoteOrder,
    onOrderChange: (NoteOrder) -> Unit,
) {
    AnimatedVisibility(
        visible = isOrderSectionVisible,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically(),
    ) {
        OrderSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            noteOrder = noteOrder,
            onOrderChange = onOrderChange
        )
    }
}