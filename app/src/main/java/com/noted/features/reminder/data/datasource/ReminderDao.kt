package com.noted.features.reminder.data.datasource

import androidx.room.*
import com.noted.features.reminder.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * from reminder")
    fun getReminders(): Flow<List<Reminder>>

    @Query("SELECT * from reminder WHERE id = :id")
    suspend fun getReminderById(id: Int): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("DELETE from reminder WHERE id = :id")
    suspend fun deleteReminderById(id: Int)
}