package com.noted.features.reminder

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.noted.MainActivity
import com.noted.R
import javax.inject.Inject

class AlarmReceiver @Inject constructor(
    private val notificationManager: NotificationManager,
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        notificationManager.sendReminderNotification(
            context = context,
            channelId = context.getString(R.string.reminder_notification_channel_id)
        )
    }
}

// TODO: clear the code below
fun NotificationManager.sendReminderNotification(
    context: Context,
    channelId: String,
) {
    val contentIntent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        1,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Title")
//        .setContentTitle(context.getString(R.string.title_notification_reminder))
        .setContentText("Content")
//        .setContentText(context.getString(R.string.description_notification_reminder))
        .setSmallIcon(R.drawable.ic_launcher)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("woohoo")
//                .bigText(context.getString(R.string.description_notification_reminder))
        )
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

const val NOTIFICATION_ID = 1