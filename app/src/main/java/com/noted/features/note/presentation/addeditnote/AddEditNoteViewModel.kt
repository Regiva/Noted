package com.noted.features.note.presentation.addeditnote

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.noted.core.base.presentation.StatefulViewModel
import com.noted.features.note.domain.model.InvalidNoteException
import com.noted.features.note.domain.model.Note
import com.noted.features.note.domain.usecase.NoteUseCases
import com.noted.features.reminder.ReminderManager
import com.noted.features.reminder.domain.model.Day
import com.noted.features.reminder.domain.model.Reminder
import com.noted.features.reminder.domain.model.Repeat
import com.noted.features.reminder.domain.model.Time
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notesUseCases: NoteUseCases,
    private val reminderManager: ReminderManager,
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
                                note = note,
                                noteTitle = note.title,
                                noteContent = note.content,
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
                    try {
                        notesUseCases.addNote(
                            Note(
                                title = state.noteTitle,
                                content = state.noteContent,
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

            is AddEditNoteScreenEvent.OpenCloseReminderDialog -> {
                updateState {
                    copy(
                        reminderState = state.reminderState.copy(
                            visible = !state.reminderState.visible,
                        )
                    )
                }
            }

            is AddEditNoteScreenEvent.EnteredReminder -> {
                val enteredTime = LocalTime.of(event.time.getHour(), event.time.getMinute())
                updateState {
                    copy(
                        reminderState = state.reminderState.copy(
                            error = event.day == Day.Today && enteredTime < LocalTime.now()
                        )
                    )
                }
            }

            is AddEditNoteScreenEvent.AddReminder -> {
                state.note?.let { note ->
                    reminderManager.startReminder(
                        reminder = Reminder.from(
                            day = event.day,
                            time = event.time,
                            repeat = event.repeat,
                        ),
                        note = note,
                    )
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
    val note: Note? = null,
    val noteTitle: String = "",
    val noteContent: String = "",
    val noteColor: Color = Note.noteColors.random(),
    val reminderState: ReminderState = ReminderState(),
)

data class ReminderState(
    val visible: Boolean = false,
    val reminder: Reminder = Reminder(),
    val error: Boolean = false,
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
}
