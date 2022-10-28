package com.noted.features.note.presentation.addeditnote

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.noted.core.base.presentation.StatefulViewModel
import com.noted.features.note.domain.model.InvalidNoteException
import com.noted.features.note.domain.model.Note
import com.noted.features.note.domain.usecase.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val notesUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle,
) : StatefulViewModel<AddEditNoteState>(AddEditNoteState()) {

    private var currentNoteId: Int? = null

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("noteId")?.let { noteId ->
            if (noteId != -1) {
                viewModelScope.launch {
                    notesUseCases.getNote(noteId)?.also { note ->
                        currentNoteId = note.id
                        updateState {
                            copy(
                                noteTitleTextFieldValue = state.noteTitleTextFieldValue.copy(
                                    text = note.title
                                ),
                                noteContentTextFieldValue = state.noteContentTextFieldValue.copy(
                                    text = note.content
                                ),
                                noteColor = Color(note.color),
                            )
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditNoteScreenEvent) {
        when (event) {
            is AddEditNoteScreenEvent.ChangeColor -> {
                updateState {
                    copy(noteColor = event.color)
                }
            }
            is AddEditNoteScreenEvent.EnteredContent -> {
                updateState {
                    copy(
                        noteContentTextFieldValue = noteContentTextFieldValue.copy(
                            text = event.value,
                            selection = TextRange(event.value.length),
                        )
                    )
                }
            }
            is AddEditNoteScreenEvent.EnteredTitle -> {
                updateState {
                    copy(
                        noteTitleTextFieldValue = noteTitleTextFieldValue.copy(
                            text = event.value,
                            selection = TextRange(event.value.length),
                        )
                    )
                }
            }
            is AddEditNoteScreenEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        notesUseCases.addNote(
                            Note(
                                title = state.noteTitleTextFieldValue.text,
                                content = state.noteContentTextFieldValue.text,
                                timestamp = System.currentTimeMillis(),
                                color = state.noteColor.toArgb(),
                                id = currentNoteId
                            )
                        )
                        _eventFlow.emit(UiEvent.SaveNote)
                    } catch (e: InvalidNoteException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = e.message ?: "Couldn't save note"
                            )
                        )
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }
}

data class AddEditNoteState(
    val noteTitleTextFieldValue: TextFieldValue = TextFieldValue(text = "title"),
    val noteContentTextFieldValue: TextFieldValue = TextFieldValue(text = "content"),
    val noteColor: Color = Note.noteColors.random(),
)

sealed class AddEditNoteScreenEvent {
    data class EnteredTitle(val value: String) : AddEditNoteScreenEvent()
    data class EnteredContent(val value: String) : AddEditNoteScreenEvent()
    data class ChangeColor(val color: Color) : AddEditNoteScreenEvent()
    object SaveNote : AddEditNoteScreenEvent()
}
