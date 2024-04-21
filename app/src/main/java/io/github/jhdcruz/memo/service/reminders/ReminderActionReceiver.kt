package io.github.jhdcruz.memo.service.reminders

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
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

    private lateinit var coroutineScope: CoroutineScope

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        Log.i("ReminderActionReceiver", "Received action ${intent.action}")

        coroutineScope = CoroutineScope(Dispatchers.Default)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // get task ID
        val index = intent.getIntExtra("index", 1)
        val taskId = intent.getStringExtra("id")

        Log.d("ReminderActionReceiver", "Notif Index: $index")
        Log.d("ReminderActionReceiver", "Task ID: $taskId")

        if (taskId?.isNotBlank() == true) {
            when (intent.action) {
                "io.github.jhdcruz.memo.NOTIF_ACTION_DONE" -> {
                    coroutineScope.launch {
                        tasksRepository.onTaskCompleted(taskId)
                    }
                }
            }
        }

        // cancel notification
        notificationManager.cancel(index)
    }
}
