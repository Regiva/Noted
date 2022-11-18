package com.noted.features.note.data.datasource

import androidx.room.*
import com.noted.features.note.domain.model.Note
import com.noted.features.reminder.domain.model.NoteWithReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * from note")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT * from note WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Delete
    suspend fun deleteNote(note: Note)

    @Transaction
    @Query("SELECT * from note WHERE id = :id")
    suspend fun getNoteWithReminderById(id: Int): NoteWithReminder?
}