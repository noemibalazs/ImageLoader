package com.noemi.imageloader.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.noemi.imageloader.R
import com.noemi.imageloader.util.CACHE_CLEAR_ACTION
import com.noemi.imageloader.util.SCHEDULED_CACHE_CLEAR_ACTION

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class CacheCleanerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == CACHE_CLEAR_ACTION) {
            notifyUserCacheCleared(context)
        }
    }

    private fun notifyUserCacheCleared(context: Context?) {
        context?.let {
            val intent = launchMainActivity(it)
            val notification = NotificationCompat.Builder(it, CHANNEL_ID)
                .setContentTitle(it.getString(R.string.label_notification_title))
                .setContentText(it.getString(R.string.label_notification_content))
                .setSmallIcon(R.drawable.clear)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(it.getString(R.string.label_notification_content))
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(intent)
                .setAutoCancel(true)
                .build()
            val manager = NotificationManagerCompat.from(it)
            manager.notify(12, notification)

            checkNotificationChannelIsAvailable(it)
        }
    }

    private fun launchMainActivity(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = SCHEDULED_CACHE_CLEAR_ACTION
        return PendingIntent.getActivity(context, 21, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun checkNotificationChannelIsAvailable(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_TITLE, NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "12"
        private const val CHANNEL_TITLE = "Cache Cleaner"
    }
}