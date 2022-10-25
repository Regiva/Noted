package com.noted.features.note.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.noted.features.note.domain.model.Note

@Database(
    entities = [Note::class],
    version = 1
)
abstract class NoteDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao
}