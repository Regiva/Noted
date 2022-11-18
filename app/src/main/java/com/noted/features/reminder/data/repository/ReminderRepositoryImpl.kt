package com.noted.features.reminder.data.repository

import com.noted.features.reminder.data.datasource.ReminderDao
import com.noted.features.reminder.domain.model.Reminder
import com.noted.features.reminder.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
) : ReminderRepository {

    override fun getReminders(): Flow<List<Reminder>> {
        return reminderDao.getReminders()
    }

    override suspend fun getReminderById(id: Int): Reminder? {
        return reminderDao.getReminderById(id)
    }

    override suspend fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder)
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    override suspend fun deleteReminderById(id: Int) {
        reminderDao.deleteReminderById(id)
    }
}