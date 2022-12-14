package com.noted.features.note.data.repository

import com.noted.features.note.data.datasource.NoteDao
import com.noted.features.note.domain.model.Note
import com.noted.features.note.domain.repository.NoteRepository
import com.noted.features.reminder.domain.model.NoteWithReminder
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(
    private val dao: NoteDao,
) : NoteRepository {

    override fun getNotes(): Flow<List<Note>> {
        return dao.getNotes()
    }

    override suspend fun getNoteById(id: Int): Note? {
        return dao.getNoteById(id)
    }

    override suspend fun insertNote(note: Note): Long {
        return dao.insertNote(note)
    }

    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(note)
    }

    override suspend fun getNoteWithReminderById(id: Int): NoteWithReminder? {
        return dao.getNoteWithReminderById(id)
    }
}