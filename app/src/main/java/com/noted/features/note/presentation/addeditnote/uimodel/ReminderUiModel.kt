package com.noted.features.note.presentation.addeditnote.uimodel

import com.noted.features.reminder.domain.model.Reminder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ReminderUiModel(
    val reminder: Reminder? = null,
    val dateTime: LocalDateTime = LocalDateTime.now(),
) {
    val formattedDateTime: String = dateTime.format(format)

    companion object {
        val format: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM y HH:mm")
    }
}
