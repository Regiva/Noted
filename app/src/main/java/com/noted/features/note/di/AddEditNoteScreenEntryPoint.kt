package com.noted.features.note.di

import com.noted.features.note.presentation.addeditnote.AddEditNoteViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AddEditNoteScreenEntryPoint {
    val addEditNoteVmFactory: AddEditNoteViewModel.Factory
}
