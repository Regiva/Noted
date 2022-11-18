package com.noted.features.note.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.noted.features.note.domain.model.Note
import com.noted.features.reminder.data.datasource.ReminderDao
import com.noted.features.reminder.domain.model.Reminder

@Database(
    entities = [Note::class, Reminder::class],
    version = 2,
)
abstract class NoteDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao
    abstract val reminderDao: ReminderDao

    companion object {
        const val DATABASE_NAME = "notes_db"
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE TABLE reminder
                (id INTEGER PRIMARY KEY,
                noteId INTEGER NOT NULL,
                dateTimeOfFirstRemind INTEGER NOT NULL,
                repeat TEXT NOT NULL);
            """.trimIndent()
        )
    }
}
