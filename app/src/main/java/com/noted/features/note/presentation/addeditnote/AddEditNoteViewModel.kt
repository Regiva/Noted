package com.noted.features.note.presentation.addeditnote

import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.noted.core.base.presentation.StatefulViewModel
import com.noted.features.note.domain.model.Note
import com.noted.features.note.domain.usecase.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val notesUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle,
) : StatefulViewModel<AddEditNoteState>(AddEditNoteState()) {

    private var currentNoteId: Int? = null

    init {
        savedStateHandle.get<Int>("note_id")?.let { noteId ->
            if (noteId != -1) {
                viewModelScope.launch {
                    notesUseCases.getNote(noteId)?.also { note ->
                        currentNoteId = note.id
                        // TODO:
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditNoteUiEvent) {
        when (event) {
            is AddEditNoteUiEvent.ChangeColor -> {
                updateState {
                    copy(noteColor = event.color)
                }
            }
            is AddEditNoteUiEvent.EnteredContent -> {

            }
            is AddEditNoteUiEvent.EnteredTitle -> {

            }
            is AddEditNoteUiEvent.SaveNote -> {

            }
            is AddEditNoteUiEvent.ShowSnackbar -> {

            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }
}

data class AddEditNoteState(
    val noteTitleTextFieldValue: TextFieldValue = TextFieldValue(),
    val noteContentTextFieldValue: TextFieldValue = TextFieldValue(),
    val noteColor: Int = Note.noteColors.random().toArgb(),
)

sealed class AddEditNoteUiEvent {
    data class ShowSnackbar(val message: String) : AddEditNoteUiEvent()
    data class EnteredTitle(val value: String) : AddEditNoteUiEvent()

    //    data class ChangeTitleFocus(val focused: Boolean): AddEditNoteUiEvent()
//    data class ChangeTitleFocus(val focusState: FocusState): AddEditNoteUiEvents()
    data class EnteredContent(val value: String) : AddEditNoteUiEvent()

    //    data class ChangeContentFocus(val focused: Boolean): AddEditNoteUiEvent()
//    data class ChangeContentFocus(val focusState: FocusState): AddEditNoteUiEvents()
    data class ChangeColor(val color: Int) : AddEditNoteUiEvent()
    object SaveNote : AddEditNoteUiEvent()
}
