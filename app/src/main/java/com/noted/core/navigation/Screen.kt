package com.noted.core.navigation

sealed class Screen(val route: String) {
    object NotesScreen : Screen("note_notes_screen")
    object AddEditNoteScreen : Screen("note_add_edit_note_screen")
}
