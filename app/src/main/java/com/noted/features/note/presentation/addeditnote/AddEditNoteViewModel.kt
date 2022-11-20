package com.noted.features.note.presentation.addeditnote

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.noted.core.base.presentation.StatefulViewModel
import com.noted.features.note.domain.model.Note
import com.noted.features.note.domain.usecase.NoteUseCases
import com.noted.features.reminder.domain.model.Day
import com.noted.features.reminder.domain.model.Reminder
import com.noted.features.reminder.domain.model.Repeat
import com.noted.features.reminder.domain.model.Time
import com.noted.features.reminder.domain.usecase.ReminderUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

class AddEditNoteViewModel @AssistedInject constructor(
    private val notesUseCases: NoteUseCases,
    private val reminderUseCases: ReminderUseCases,
    @Assisted private val noteId: Int? = null,
) : StatefulViewModel<AddEditNoteState>(AddEditNoteState()) {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getNoteWithReminder()
    }

    private fun getNoteWithReminder() {
        if (noteId != -1 && noteId != null) {
            viewModelScope.launch {
                notesUseCases.getNoteWithReminder(noteId)?.also { noteWithReminder ->
                    updateState {
                        copy(
                            note = noteWithReminder.note,
                            noteTitle = noteWithReminder.note.title,
                            noteContent = noteWithReminder.note.content,
                            noteColor = Color(noteWithReminder.note.color),
                            reminder = noteWithReminder.reminder ?: state.reminder,
                            reminderDialogState = state.reminderDialogState.copy(
                                deleteButton = noteWithReminder.reminder != null,
                            )
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditNoteScreenEvent): Any = when (event) {
        is AddEditNoteScreenEvent.ChangeColor -> {
            updateState {
                copy(noteColor = event.color)
            }
        }

        is AddEditNoteScreenEvent.EnteredContent -> {
            updateState {
                copy(noteContent = event.value)
            }
        }

        is AddEditNoteScreenEvent.EnteredTitle -> {
            updateState {
                copy(noteTitle = event.value)
            }
        }

        is AddEditNoteScreenEvent.SaveNote -> {
            viewModelScope.launch {
                saveNote()
            }
            navigateUp()
        }

        is AddEditNoteScreenEvent.OpenCloseReminderDialog -> {
            toggleReminderDialogVisibility()
        }

        is AddEditNoteScreenEvent.EnteredReminder -> {
            val enteredTime = LocalTime.of(event.time.getHour(), event.time.getMinute())
            updateState {
                copy(
                    reminderDialogState = state.reminderDialogState.copy(
                        error = event.day == Day.Today && enteredTime < LocalTime.now()
                    )
                )
            }
        }

        is AddEditNoteScreenEvent.AddReminder -> {
            viewModelScope.launch {
                saveNote()
                state.note?.id?.let { noteId ->
                    val reminder = Reminder.from(event.day, event.time, event.repeat, noteId)
                    addReminderToNote(reminder)
                }
            }
            toggleReminderDialogVisibility()
            updateState {
                copy(
                    reminderDialogState = state.reminderDialogState.copy(
                        deleteButton = state.reminder != null,
                    ),
                )
            }
        }

        is AddEditNoteScreenEvent.DeleteReminder -> {
            deleteReminder()
            toggleReminderDialogVisibility()
        }
    }

    private fun toggleReminderDialogVisibility(
        visible: Boolean = !state.reminderDialogState.visible,
    ) {
        updateState {
            copy(reminderDialogState = state.reminderDialogState.copy(visible = visible))
        }
    }

    private fun deleteReminder() {
        viewModelScope.launch {
            state.reminder?.let {
                reminderUseCases.deleteReminder(it)
                updateState {
                    copy(
                        reminder = null,
                        reminderDialogState = state.reminderDialogState.copy(
                            deleteButton = false,
                        ),
                    )
                }
            }
        }
    }

    private suspend fun saveNote() {
        val savedNote = notesUseCases.addNote(
            Note(
                title = state.noteTitle,
                content = state.noteContent,
                timestamp = System.currentTimeMillis(),
                color = state.noteColor.toArgb(),
                id = state.note?.id
            )
        )
        updateState {
            copy(note = savedNote)
        }
    }

    private fun addReminderToNote(reminder: Reminder) {
        viewModelScope.launch {
            state.note?.let { note ->
                val addedReminder = reminderUseCases.addReminder(
                    reminder = reminder,
                    note = note
                )
                updateState {
                    copy(reminder = addedReminder)
                }
            }
        }
    }

    private fun navigateUp() {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.SaveNote)
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }

    @AssistedFactory
    interface Factory {
        fun create(noteId: Int?): AddEditNoteViewModel
    }

    companion object {
        fun providesFactory(
            assistedFactory: Factory,
            noteId: Int?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(noteId) as T
            }
        }
    }
}

data class AddEditNoteState(
    val note: Note? = null,
    val noteTitle: String = "",
    val noteContent: String = "",
    val noteColor: Color = Note.noteColors.random(),
    val reminderDialogState: ReminderDialogState = ReminderDialogState(),
    val reminder: Reminder? = null,
)

data class ReminderDialogState(
    val visible: Boolean = false,
    val error: Boolean = false,
    val deleteButton: Boolean = false,
)

sealed class AddEditNoteScreenEvent {
    data class EnteredTitle(val value: String) : AddEditNoteScreenEvent()
    data class EnteredContent(val value: String) : AddEditNoteScreenEvent()
    data class ChangeColor(val color: Color) : AddEditNoteScreenEvent()
    object SaveNote : AddEditNoteScreenEvent()
    object OpenCloseReminderDialog : AddEditNoteScreenEvent()
    data class EnteredReminder(val day: Day, val time: Time) : AddEditNoteScreenEvent()
    data class AddReminder(
        val day: Day,
        val time: Time,
        val repeat: Repeat,
    ) : AddEditNoteScreenEvent()

    object DeleteReminder : AddEditNoteScreenEvent()
}
