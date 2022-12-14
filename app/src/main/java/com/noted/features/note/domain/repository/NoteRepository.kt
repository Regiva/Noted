package com.noted.features.note.domain.repository

import com.noted.features.note.domain.model.Note
import com.noted.features.reminder.domain.model.NoteWithReminder
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getNotes(): Flow<List<Note>>

    suspend fun getNoteById(id: Int): Note?

    suspend fun insertNote(note: Note): Long

    suspend fun deleteNote(note: Note)

    suspend fun getNoteWithReminderById(id: Int): NoteWithReminder?
}