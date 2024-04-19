package io.github.jhdcruz.memo.service.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.app.NotificationCompat
import io.github.jhdcruz.memo.R

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val receiver = ReminderActionReceiver()
            val intentFilter =
                IntentFilter("NOTIF_ACTION_DONE").apply {
                    addAction("NOTIF_ACTION_SNOOZE")
                }

            registerReceiver(receiver, intentFilter, RECEIVER_NOT_EXPORTED)
        }

        // get the data from intent
        val taskId = intent?.getStringExtra("id") ?: ""
        val title = intent?.getStringExtra("title") ?: ""
        val dueDate = intent?.getStringExtra("dueDate") ?: ""

        buildNotification(
            taskId = taskId,
            message = title,
            dueDate = dueDate,
        )

        // Return START_STICKY so the service will be restarted if it is killed
        return START_NOT_STICKY
    }

    private fun buildNotification(
        taskId: String,
        message: String,
        dueDate: String,
    ) {
        val channelId = "io.github.jhdcruz.memo"
        val channelName = "Memo"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        val doneAction =
            PendingIntent.getBroadcast(
                this,
                1,
                Intent("NOTIF_ACTION_DONE").apply {
                    putExtra("id", taskId)
                },
                PendingIntent.FLAG_IMMUTABLE,
            )
        val snoozeAction =
            PendingIntent.getBroadcast(
                this,
                2,
                Intent("NOTIF_ACTION_SNOOZE"),
                PendingIntent.FLAG_IMMUTABLE,
            )

        // Create a notification
        val notification =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle("You have a task that is due.")
                .setContentText("$message\n\n $dueDate")
                .setSmallIcon(R.drawable.baseline_notify_24)
                .addAction(R.drawable.baseline_add_24, "Done", doneAction)
                .addAction(R.drawable.baseline_clock_24, "Snooze", snoozeAction)
                .build()

        // Start the foreground service
        startForeground(1, notification)
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_DETACH)
        super.onDestroy()
    }
}
