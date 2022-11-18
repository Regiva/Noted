package com.noted.features.note.domain.usecase

import com.noted.features.note.domain.repository.NoteRepository
import com.noted.features.reminder.domain.model.NoteWithReminder

class GetNoteWithReminder(
    private val repository: NoteRepository,
) {

    suspend operator fun invoke(id: Int): NoteWithReminder? {
        return repository.getNoteWithReminderById(id)
    }
}
