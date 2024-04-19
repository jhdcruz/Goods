package io.github.jhdcruz.memo.service.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import io.github.jhdcruz.memo.data.model.TaskList
import io.github.jhdcruz.memo.domain.format
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

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        // get the data from intent
        val tasks =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getSerializableExtra("data", TaskList::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent?.getSerializableExtra("data") as TaskList
            }

        // schedule notifications
        if (tasks?.tasks?.isNotEmpty() == true) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            tasks.tasks.forEach { task ->
                // Intent and content for the notification
                val reminderIntent =
                    Intent(this, ReminderNotifyService::class.java).apply {
                        putExtra("id", task.id)
                        putExtra("title", task.title)
                        putExtra("dueDate", task.dueDate!!.format(super.getApplicationContext()))
                    }

                // Use the due date as trigger time
                val triggerAtMillis = task.dueDate?.toDate()?.time!!

                val pendingIntent =
                    PendingIntent.getBroadcast(
                        this,
                        0,
                        reminderIntent,
                        PendingIntent.FLAG_IMMUTABLE,
                    )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // For API level 31 and above
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerAtMillis,
                            pendingIntent,
                        )
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            triggerAtMillis,
                            pendingIntent,
                        )
                    }
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent,
                    )
                }
            }
        }

        // Return START_STICKY so the service will be restarted if it is killed
        return START_STICKY
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_DETACH)
        super.onDestroy()
    }
}
