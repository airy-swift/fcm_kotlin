package com.example.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        message.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${message.data}")
        }

        message.notification?.let {
            Log.d(TAG, "Message Notification Body:  ${it.body}")
        }

        val title = message.notification?.title
        val content = message.notification?.body
        if (title != null && content != null) {
            this.sendNotification(
                title = title,
                message = content
            )
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "asdfasdfasdf Message Notification Body: $token")
    }

    private fun sendNotification(title: String, message: String) {
        val intent = MainActivity.newNotificationIntent(this)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val notificationId = 42
        val pendingIntent = PendingIntent.getActivity(
            this, notificationId, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.notification_channel_id)
        val channelName = getString(R.string.notification_channel_name)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private var TAG = "FirebaseMessagingService"
    }
}