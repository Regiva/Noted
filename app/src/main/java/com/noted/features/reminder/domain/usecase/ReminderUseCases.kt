package com.noted.features.reminder.domain.usecase

data class ReminderUseCases(
    val addReminder: AddReminder,
    val deleteReminder: DeleteReminder,
    val deleteOnceTimeReminder: DeleteOnceTimeReminder,
)
