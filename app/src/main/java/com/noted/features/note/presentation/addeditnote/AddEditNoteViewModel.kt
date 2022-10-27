package com.noted.features.note.presentation.addeditnote

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.noted.core.base.BaseViewModel
import com.noted.features.note.domain.model.Note
import com.noted.features.note.domain.usecase.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val notesUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddEditNoteState>(AddEditNoteState()) {

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

    fun onEvent(events: AddEditNoteUiEvents) {
        when(events) {
            is AddEditNoteUiEvents.ChangeColor -> {

            }
            is AddEditNoteUiEvents.ChangeContentFocus -> {

            }
            is AddEditNoteUiEvents.ChangeTitleFocus -> {

            }
            is AddEditNoteUiEvents.EnteredContent -> {

            }
            is AddEditNoteUiEvents.EnteredTitle -> {

            }
            is AddEditNoteUiEvents.SaveNote -> {

            }
            is AddEditNoteUiEvents.ShowSnackbar -> {

            }
        }
    }
}

data class AddEditNoteState(
    val noteTitleTextFieldValue: TextFieldValue = TextFieldValue(),
    val noteContentTextFieldValue: TextFieldValue = TextFieldValue(),
    val noteColor: Color = Note.noteColors.random(),
)

sealed class AddEditNoteUiEvents {
    data class ShowSnackbar(val message: String) : AddEditNoteUiEvents()
    data class EnteredTitle(val value: String): AddEditNoteUiEvents()
    data class ChangeTitleFocus(val focused: Boolean): AddEditNoteUiEvents()
//    data class ChangeTitleFocus(val focusState: FocusState): AddEditNoteUiEvents()
    data class EnteredContent(val value: String): AddEditNoteUiEvents()
    data class ChangeContentFocus(val focused: Boolean): AddEditNoteUiEvents()
//    data class ChangeContentFocus(val focusState: FocusState): AddEditNoteUiEvents()
    data class ChangeColor(val color: Int): AddEditNoteUiEvents()
    object SaveNote: AddEditNoteUiEvents()
}
