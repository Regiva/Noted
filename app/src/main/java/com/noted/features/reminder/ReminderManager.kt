package com.noted.features.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.noted.features.note.domain.model.Note
import com.noted.features.reminder.domain.model.Reminder
import com.noted.features.reminder.domain.model.Repeat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneOffset
import javax.inject.Inject

class ReminderManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
) {

    fun startReminder(
        reminder: Reminder,
        note: Note,
    ) {

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("note_title", note.title)
            putExtra("note_content", note.content)
        }.let { intent ->
            PendingIntent.getBroadcast(
                context.applicationContext,
                note.id ?: 137, // TODO: change this :)
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        }

        if (canScheduleExactAlarms()) {
            val triggerTime = reminder.dateTimeOfFirstRemind.toEpochSecond(ZoneOffset.UTC)
            val interval = reminder.repeat.getAlarmInterval()
            when (reminder.repeat) {
                Repeat.Once -> {
                    alarmManager.setAlarmClock(
                        AlarmManager.AlarmClockInfo(triggerTime, intent),
                        intent
                    )
                }
                else -> {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        interval,
                        intent
                    )
                }
            }
        }
    }

    fun stopReminder(
        reminderId: Int
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context.applicationContext,
                reminderId,
                intent,
                0 or PendingIntent.FLAG_IMMUTABLE
            )
        }
        alarmManager.cancel(intent)
    }

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            true
        } else {
            alarmManager.canScheduleExactAlarms()
        }
    }
}

fun Repeat.getAlarmInterval() = when (this) {
    Repeat.Once -> 0
    Repeat.Day -> AlarmManager.INTERVAL_DAY
    Repeat.Week -> 7 * AlarmManager.INTERVAL_DAY
    Repeat.Month -> 30 * AlarmManager.INTERVAL_DAY
    Repeat.Year -> 365 * AlarmManager.INTERVAL_DAY
    Repeat.Interval -> AlarmManager.INTERVAL_DAY
}