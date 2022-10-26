package com.noted.features.note.domain.usecase

import com.noted.features.note.domain.model.Note
import com.noted.features.note.domain.repository.NoteRepository

class DeleteNote(
    private val noteRepository: NoteRepository,
) {

    suspend operator fun invoke(note: Note) {
        noteRepository.deleteNote(note)
    }
}