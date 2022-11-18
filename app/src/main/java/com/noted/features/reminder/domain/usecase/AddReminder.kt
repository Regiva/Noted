package com.noted.features.reminder.domain.usecase

import com.noted.features.note.domain.model.Note
import com.noted.features.reminder.ReminderManager
import com.noted.features.reminder.domain.model.Reminder
import com.noted.features.reminder.domain.repository.ReminderRepository

class AddReminder(
    private val reminderRepository: ReminderRepository,
    private val reminderManager: ReminderManager,
) {

    suspend operator fun invoke(
        reminder: Reminder,
        note: Note,
    ): Reminder? {
        reminderManager.stopReminder(reminder)
        val insertedId = reminderRepository.insertReminder(reminder).toInt()
        val insertedReminder = reminder.copy(id = insertedId)
        reminderManager.startReminder(insertedReminder, note)
        return reminderRepository.getReminderById(insertedId)
    }
}