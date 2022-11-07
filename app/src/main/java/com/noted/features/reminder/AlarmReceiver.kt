package com.noted.features.reminder

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.noted.MainActivity
import com.noted.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("note_title") ?: "haha"
        val content = intent.getStringExtra("note_content") ?: "loser"
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