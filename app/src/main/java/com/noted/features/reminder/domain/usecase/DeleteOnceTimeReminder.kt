package com.noted.features.reminder.domain.usecase

import com.noted.features.reminder.ReminderManager
import com.noted.features.reminder.domain.model.Repeat
import com.noted.features.reminder.domain.repository.ReminderRepository

class DeleteOnceTimeReminder(
    private val reminderRepository: ReminderRepository,
    private val reminderManager: ReminderManager,
) {
    suspend operator fun invoke(id: Int) {
        val reminder = reminderRepository.getReminderById(id)
        if (reminder != null) {
            if (reminder.repeat == Repeat.Once) {
                reminderManager.stopReminder(reminder)
                reminderRepository.deleteReminder(reminder)
            }
        }
    }
}
