package com.noted.features.note.domain.usecase

import com.noted.features.note.domain.model.Note
import com.noted.features.note.domain.repository.NoteRepository

class AddNote(
    private val repository: NoteRepository,
) {

    suspend operator fun invoke(note: Note): Note? {
        val insertedId = repository.insertNote(note)
        return repository.getNoteById(insertedId.toInt())
    }
}