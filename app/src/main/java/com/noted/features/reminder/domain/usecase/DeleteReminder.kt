package com.noted.features.reminder.domain.usecase

import com.noted.features.reminder.ReminderManager
import com.noted.features.reminder.domain.model.Reminder
import com.noted.features.reminder.domain.repository.ReminderRepository

class DeleteReminder(
    private val reminderRepository: ReminderRepository,
    private val reminderManager: ReminderManager,
) {

    suspend operator fun invoke(reminder: Reminder) {
        reminderManager.stopReminder(reminder)
        reminderRepository.deleteReminder(reminder)
    }

    suspend operator fun invoke(id: Int) {
        val reminder = reminderRepository.getReminderById(id)
        if (reminder != null) {
            reminderManager.stopReminder(reminder)
            reminderRepository.deleteReminder(reminder)
        }
    }
}