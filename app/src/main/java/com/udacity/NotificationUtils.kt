package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationUtils {

    const val NOTIFICATION_ID = 10010

    fun sendNotification(
        appContext: Context,
        nm: NotificationManager,
        pendingIntent: PendingIntent
    ) {

        val builder =  NotificationCompat.Builder(
            appContext,
            appContext.getString(R.string.channel_id)
        ).setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(appContext.getString(R.string.notification_title))
            .setContentText(appContext.getString(R.string.notification_description))
            // close notification once user taps on it
            .setAutoCancel(true)
            // alert only once
            .setOnlyAlertOnce(true)
            // add notification action button
            .addAction(
                R.drawable.ic_assistant_black_24dp,
                appContext.getString(R.string.notification_button),
                // open a specific activity
                pendingIntent
            )

        nm.notify(NOTIFICATION_ID, builder.build())
    }


    fun createChannel(nm: NotificationManager, channelId: String, channelName: String) {
        // create channel for api level 26 (oreo) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.description = "File Download Status"

            nm.createNotificationChannel(notificationChannel)
        }
    }

    fun cancelNotification(nm: NotificationManager) {
        nm.cancel(NOTIFICATION_ID)
    }
}