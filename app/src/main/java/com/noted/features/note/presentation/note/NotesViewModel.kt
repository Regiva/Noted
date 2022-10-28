package com.noted.features.note.presentation.note

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.noted.core.base.presentation.StatefulViewModel
import com.noted.features.note.domain.model.Note
import com.noted.features.note.domain.usecase.NoteUseCases
import com.noted.features.note.domain.util.NoteOrder
import com.noted.features.note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesUseCases: NoteUseCases,
) : StatefulViewModel<NotesState>(NotesState()) {

    private var recentlyDeletedNote: Note? = null

    private var getNotesJob: Job? = null

    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NotesScreenEvents) {
        when (event) {
            is NotesScreenEvents.DeleteNote -> {
                viewModelScope.launch {
                    notesUseCases.deleteNote(event.note)
                    recentlyDeletedNote = event.note
                }
            }
            is NotesScreenEvents.Order -> {
                if (state.noteOrder::class == event.noteOrder::class &&
                    state.noteOrder.orderType == event.noteOrder.orderType
                ) {
                    return
                }
                getNotes(event.noteOrder)
            }
            is NotesScreenEvents.RestoreNote -> {
                viewModelScope.launch {
                    notesUseCases.addNote(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }
            is NotesScreenEvents.ToggleOrderSection -> {
                updateState {
                    copy(isOrderSectionVisible = !state.isOrderSectionVisible)
                }
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        getNotesJob?.cancel()
        getNotesJob = notesUseCases.getNotes(noteOrder)
            .onEach { notes ->
                Log.d("rere", notes.toString())
                updateState {
                    copy(
                        notes = notes,
                        noteOrder = noteOrder,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

data class NotesState(
    val notes: List<Note> = emptyList(),
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false,
)

sealed class NotesScreenEvents {
    data class Order(val noteOrder: NoteOrder) : NotesScreenEvents()
    data class DeleteNote(val note: Note) : NotesScreenEvents()
    object RestoreNote : NotesScreenEvents()
    object ToggleOrderSection : NotesScreenEvents()
}
