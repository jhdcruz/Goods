package io.github.jhdcruz.memo.service.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.github.jhdcruz.memo.MainActivity
import io.github.jhdcruz.memo.R
import okhttp3.internal.notify

/**
 * Starts and shows notifications based on received data
 * from [ReminderSchedulerService].
 */
class ReminderNotifyService : Service() {
    override fun onBind(intent: Intent) = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val receiver = ReminderActionReceiver()
        val intentFilter = IntentFilter("io.github.jhdcruz.memo.NOTIF_ACTION_DONE")

        ContextCompat.registerReceiver(
            this,
            receiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )

        // get the data from intent
        val index = intent?.getIntExtra("index", 1)
        val taskId = intent?.getStringExtra("id") ?: ""
        val title = intent?.getStringExtra("title") ?: ""
        val description = intent?.getStringExtra("description") ?: ""

        buildNotification(
            index = index!!,
            taskId = taskId,
            title = title,
            description = description,
        )

        return START_STICKY
    }

    private fun buildNotification(
        index: Int,
        taskId: String,
        title: String,
        description: String,
    ) {
        val channelId = "io.github.jhdcruz.memo"
        val channelName = "Task Reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val doneIntent =
            Intent("io.github.jhdcruz.memo.NOTIF_ACTION_DONE").apply {
                putExtra("index", index)
                putExtra("id", taskId)
            }

        val doneAction =
            PendingIntent.getBroadcast(
                this,
                index,
                doneIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val onTapIntent = PendingIntent.getActivity(
            this,
            9,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE,
        )

        // Create a notification
        val notification =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle("Task Due: $title")
                .setContentText(description)
                .setSmallIcon(R.drawable.baseline_notify_24)
                .addAction(R.drawable.baseline_add_24, "Done", doneAction)
                .setGroup("Due Tasks")
                .setContentIntent(onTapIntent)
                .setAutoCancel(true)
                .build()

        // Start the foreground service
        startForeground(index, notification)
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_DETACH)
        super.onDestroy()
    }
}
