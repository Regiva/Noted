package com.noted.features.note.presentation.addeditnote

import android.app.AlarmManager
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.noted.core.base.presentation.StatefulViewModel
import com.noted.features.note.domain.model.Note
import com.noted.features.note.domain.usecase.NoteUseCases
import com.noted.features.note.presentation.addeditnote.uimodel.ReminderUiModel
import com.noted.features.note.utils.localDateTimeOfEpochSec
import com.noted.features.note.utils.toEpochSec
import com.noted.features.reminder.domain.model.Reminder
import com.noted.features.reminder.domain.model.Repeat
import com.noted.features.reminder.domain.usecase.ReminderUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AddEditNoteViewModel @AssistedInject constructor(
    private val notesUseCases: NoteUseCases,
    private val reminderUseCases: ReminderUseCases,
    private val alarmManager: AlarmManager,
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
                            reminderUi = ReminderUiModel(
                                reminder = noteWithReminder.reminder,
                                dateTime = localDateTimeOfEpochSec(
                                    seconds = noteWithReminder.reminder?.epochSecondsOfFirstRemind,
                                ),
                            ),
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
            updateState {
                copy(
                    reminderUi = state.reminderUi.copy(
                        dateTime = LocalDateTime.of(event.date, event.time),
                    ),
                )
            }
        }

        is AddEditNoteScreenEvent.AddReminder -> {
            viewModelScope.launch {
                saveNote()
                state.note?.id?.let { noteId ->
                    val reminder = Reminder(
                        noteId = noteId,
                        epochSecondsOfFirstRemind = state.reminderUi.dateTime.toEpochSec(),
                        repeat = event.repeat,
                    )
                    addReminderToNote(reminder)
                }
            }
            toggleReminderDialogVisibility()
        }

        is AddEditNoteScreenEvent.DeleteReminder -> {
            deleteReminder()
            toggleReminderDialogVisibility()
        }
    }

    private fun toggleReminderDialogVisibility(
        visible: Boolean = !state.dialogVisible,
    ) {
        updateState {
            copy(dialogVisible = visible)
        }
    }

    private fun deleteReminder() {
        viewModelScope.launch {
            state.reminderUi.reminder?.let { reminder ->
                reminderUseCases.deleteReminder(reminder)
                updateState {
                    copy(
                        reminderUi = ReminderUiModel(),
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
            if (canScheduleExactAlarms()) {
                state.note?.let { note ->
                    val addedReminder = reminderUseCases.addReminder(
                        reminder = reminder,
                        note = note
                    )
                    updateState {
                        copy(
                            reminderUi = state.reminderUi.copy(
                                reminder = addedReminder,
                            ),
                        )
                    }
                }
            } else {
                _eventFlow.emit(UiEvent.ShowAlarmRationaleSnackbar)
            }
        }
    }

    private fun canScheduleExactAlarms(): Boolean {
        return if (isVersionLowerThanS()) {
            true
        } else {
            alarmManager.canScheduleExactAlarms()
        }
    }

    fun isVersionLowerThanS(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.S

    private fun navigateUp() {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.SaveNote)
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
        object ShowAlarmRationaleSnackbar : UiEvent()
    }

    @AssistedFactory
    interface Factory {
        fun create(noteId: Int?): AddEditNoteViewModel
    }

    companion object {
        fun providesFactory(
            assistedFactory: Factory,
            noteId: Int?,
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
    val dialogVisible: Boolean = false,
    val reminderUi: ReminderUiModel = ReminderUiModel(),
) {
    val reminderPresent: Boolean = reminderUi.reminder != null
    val dialogDateTimeError: Boolean = reminderUi.dateTime < LocalDateTime.now()
}

sealed class AddEditNoteScreenEvent {
    data class EnteredTitle(val value: String) : AddEditNoteScreenEvent()
    data class EnteredContent(val value: String) : AddEditNoteScreenEvent()
    data class ChangeColor(val color: Color) : AddEditNoteScreenEvent()
    object SaveNote : AddEditNoteScreenEvent()
    object OpenCloseReminderDialog : AddEditNoteScreenEvent()
    data class EnteredReminder(
        val date: LocalDate,
        val time: LocalTime,
    ) : AddEditNoteScreenEvent()

    data class AddReminder(
        val date: LocalDate,
        val time: LocalTime,
        val repeat: Repeat,
    ) : AddEditNoteScreenEvent()

    object DeleteReminder : AddEditNoteScreenEvent()
}
