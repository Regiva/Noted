package com.noted.features.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*
import javax.inject.Inject

class ReminderManager @Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager,
) {

    fun startReminder(
        // TODO: create repeat entity which will consist of time and repeat strategy
        //repeatModel: Repeat,
        reminderTime: String = "08:00",
        reminderId: Int = REMINDER_NOTIFICATION_REQUEST_CODE,
    ) {
        val (hours, minutes) = reminderTime.split(":").map { it.toInt() }

        val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context.applicationContext,
                reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT,
            )
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
        }

        if (Calendar.getInstance()
                .apply { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0
        ) {
            calendar.add(Calendar.DATE, 1)
        }

        // TODO: use variety of alarmManager calls depending on repeat time chosen by user as mentioned above
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent),
            intent
        )
    }

    fun stopReminder(
        reminderId: Int = REMINDER_NOTIFICATION_REQUEST_CODE
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context.applicationContext,
                reminderId,
                intent,
                0
            )
        }
        alarmManager.cancel(intent)
    }

    companion object {
        private const val REMINDER_NOTIFICATION_REQUEST_CODE = 137
    }
}