package com.noted.features.reminder.domain.repository

import com.noted.features.reminder.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    fun getReminders(): Flow<List<Reminder>>

    suspend fun getReminderById(id: Int): Reminder?

    suspend fun insertReminder(reminder: Reminder): Long

    suspend fun deleteReminder(reminder: Reminder)

    suspend fun deleteReminderById(id: Int)
}