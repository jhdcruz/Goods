package io.github.jhdcruz.memo.service.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.AndroidEntryPoint
import io.github.jhdcruz.memo.data.task.TasksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var tasksRepository: TasksRepository

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        when (intent.action) {
            "NOTIF_ACTION_DONE" -> {
                // get task ID
                val taskId = intent.getStringExtra("id")

                coroutineScope.launch {
                    tasksRepository.onTaskCompleted(taskId!!)
                }
            }

            "NOTIF_ACTION_SNOOZE" -> {
                // Schedule a new notification after 5 minutes
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val reminderIntent = Intent(context, ReminderNotifyService::class.java)

                val pendingIntent =
                    PendingIntent.getService(
                        context,
                        0,
                        reminderIntent,
                        PendingIntent.FLAG_IMMUTABLE,
                    )

                // 5m from now
                val triggerAtMillis = System.currentTimeMillis() + (5 * 60 * 1000)

                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                        // For API level 31 and above
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                triggerAtMillis,
                                pendingIntent,
                            )
                        }
                    }

                    else -> {
                        // For API level 23 to 30
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            triggerAtMillis,
                            pendingIntent,
                        )
                    }
                }
            }
        }
    }
}
