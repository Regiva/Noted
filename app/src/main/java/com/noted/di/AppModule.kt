package com.noted.di

import android.app.Application
import androidx.room.Room
import com.noted.features.note.data.datasource.MIGRATION_1_2
import com.noted.features.note.data.datasource.MIGRATION_2_3
import com.noted.features.note.data.datasource.NoteDatabase
import com.noted.features.note.data.repository.NoteRepositoryImpl
import com.noted.features.note.domain.repository.NoteRepository
import com.noted.features.note.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME,
        ).addMigrations(
            MIGRATION_1_2,
            MIGRATION_2_3,
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideNotesUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository),
            getNoteWithReminder = GetNoteWithReminder(repository),
        )
    }
}