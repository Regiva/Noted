package com.noted.features.reminder.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import com.noted.features.note.data.datasource.NoteDatabase
import com.noted.features.reminder.ReminderManager
import com.noted.features.reminder.data.repository.ReminderRepositoryImpl
import com.noted.features.reminder.domain.repository.ReminderRepository
import com.noted.features.reminder.domain.usecase.AddReminder
import com.noted.features.reminder.domain.usecase.DeleteOnceTimeReminder
import com.noted.features.reminder.domain.usecase.DeleteReminder
import com.noted.features.reminder.domain.usecase.ReminderUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReminderModule {

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    @Singleton
    fun provideReminderRepository(db: NoteDatabase): ReminderRepository {
        return ReminderRepositoryImpl(db.reminderDao)
    }

    @Provides
    @Singleton
    fun provideReminderUseCases(
        reminderRepository: ReminderRepository,
        reminderManager: ReminderManager
    ): ReminderUseCases {
        return ReminderUseCases(
            addReminder = AddReminder(reminderRepository, reminderManager),
            deleteReminder = DeleteReminder(reminderRepository, reminderManager),
            deleteOnceTimeReminder = DeleteOnceTimeReminder(reminderRepository, reminderManager),
        )
    }
}