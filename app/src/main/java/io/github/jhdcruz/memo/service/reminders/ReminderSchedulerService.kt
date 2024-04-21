package io.github.jhdcruz.memo.service.reminders

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.data.model.TaskList
import io.github.jhdcruz.memo.domain.toLocalDateTime
import java.time.ZoneId
import javax.inject.Inject

/**
 * Schedules notifications for tasks with due dates.
 * To be passed to [ReminderNotifyService] for notifications.
 */
@AndroidEntryPoint
class ReminderSchedulerService : Service() {
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    private lateinit var alarmManager: AlarmManager

    override fun onCreate() {
        super.onCreate()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        // get the data from intent
        val tasks =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra("data", TaskList::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent?.getParcelableExtra("data")!!
            }

        // schedule notifications
        if (tasks?.tasks?.isNotEmpty() == true) {
            tasks.tasks.forEachIndexed { index, task ->
                val reminderIntent =
                    Intent(this, ReminderNotifyService::class.java).apply {
                        putExtra("index", index + 1) // as notification ID
                        putExtra("id", task.id)
                        putExtra("title", task.title)
                        putExtra("description", task.description)
                    }

                val triggerAtDueDate =
                    task.dueDate?.toLocalDateTime()?.atZone(ZoneId.systemDefault())?.toInstant()
                        ?.toEpochMilli()!!

                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtDueDate,
                        PendingIntent.getService(
                            this,
                            index + 1,
                            reminderIntent,
                            PendingIntent.FLAG_IMMUTABLE,
                        ),
                    )
                } catch (e: SecurityException) {
                    Log.e("ReminderSchedulerService", "Permission denied for setting alarm", e)
                }
            }

            // show notification of amount of tasks are due
            buildNotification(tasks.tasks.size)
        }

        return START_STICKY
    }

    private fun buildNotification(count: Int) {
        val channelId = "io.github.jhdcruz.memo"
        val channelName = "Near Due"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        // Create a notification
        val notification =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle("You have $count tasks that due in an hour.")
                .setSmallIcon(R.drawable.baseline_notify_24)
                .build()

        // Start the foreground service
        startForeground(3, notification)
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_DETACH)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.i("ReminderSchedulerService", "Restarting ReminderSchedulerService")

        val restartServiceIntent =
            Intent(applicationContext, ReminderSchedulerService::class.java).also {
                it.setPackage(packageName)
            }

        val restartServicePendingIntent: PendingIntent =
            PendingIntent.getService(
                this,
                3,
                restartServiceIntent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        applicationContext.getSystemService(Context.ALARM_SERVICE)

        val alarmService: AlarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 1000,
            restartServicePendingIntent,
        )
    }
}
