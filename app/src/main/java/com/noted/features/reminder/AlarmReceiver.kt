package com.noted.features.reminder

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.noted.MainActivity
import com.noted.R
import com.noted.features.reminder.domain.usecase.ReminderUseCases
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderUseCases: ReminderUseCases

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("note_title") ?: "Important note"
        val content = intent.getStringExtra("note_content") ?: "is notifying you"
        val reminderId = intent.getIntExtra("reminder_id", -1)
        sendReminderNotification(context, title, content)
        deleteOnceTimeReminder(reminderId)
    }

    private fun deleteOnceTimeReminder(reminderId: Int) {
        if (reminderId != -1) {
            goAsync {
                reminderUseCases.deleteOnceTimeReminder(reminderId)
            }
        }
    }

    private fun sendReminderNotification(
        context: Context,
        title: String,
        content: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.sendReminderNotification(
            context = context,
            channelId = context.getString(R.string.reminder_notification_channel_id),
            title = title,
            content = content,
        )
    }
}

fun BroadcastReceiver.goAsync(
    context: CoroutineContext = Dispatchers.Default,
    block: suspend CoroutineScope.() -> Unit
) {
    val pendingResult = goAsync()
    CoroutineScope(SupervisorJob()).launch(context) {
        try {
            block()
        } finally {
            pendingResult.finish()
        }
    }
}

fun NotificationManager.sendReminderNotification(
    context: Context,
    channelId: String,
    title: String,
    content: String,
) {
    val contentIntent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        1,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setSmallIcon(R.drawable.ic_launcher)
        .setStyle(NotificationCompat.BigTextStyle().bigText(content))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

const val NOTIFICATION_ID = 1