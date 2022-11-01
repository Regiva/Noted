package com.noted.features.reminder.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import com.noted.features.reminder.ReminderManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ReminderModule {

    @Binds
    @Singleton
    fun bindsReminderManager(impl: ReminderManager): ReminderManager

    companion object {
        @Provides
        @Singleton
        fun provideNotificationManager(context: Context): NotificationManager {
            return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        @Provides
        @Singleton
        fun provideAlarmManager(context: Context): AlarmManager {
            return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
    }
}