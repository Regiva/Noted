package com.noted.features.note.presentation

import com.noted.features.note.domain.model.Note
import com.noted.features.note.domain.util.NoteOrder
import com.noted.features.note.domain.util.OrderType

data class NotesState(
    val notes: List<Note> = emptyList(),
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false,
)
