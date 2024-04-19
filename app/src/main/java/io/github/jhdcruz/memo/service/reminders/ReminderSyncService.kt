package io.github.jhdcruz.memo.service.reminders

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import io.github.jhdcruz.memo.data.model.Task
import io.github.jhdcruz.memo.data.model.TaskList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class ReminderSyncService : Service() {
    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var serviceScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()

        serviceScope = CoroutineScope(Dispatchers.Main)
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val context = this

        // Fetch the necessary data here
        serviceScope.launch {
            val data = fetchData()

            val schedulerIntent =
                Intent(context, ReminderSchedulerService::class.java).apply {
                    putExtra("data", data as Serializable)
                }

            context.startService(schedulerIntent)
        }
        // Return START_STICKY so the service will be restarted if it is killed
        return START_STICKY
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_DETACH)
        super.onDestroy()
    }

    /**
     * Get tasks that are due in the next 30 minutes.
     */
    private suspend fun fetchData(): TaskList {
        val uid =
            auth.currentUser?.uid
                ?: throw IllegalStateException("ReminderWorker: User not signed in")

        val currentDate = System.currentTimeMillis()
        val in30m = currentDate + (30 * 60 * 1000)

        val tasksDue =
            firestore.collection("users").document(uid).collection("tasks")
                .whereEqualTo("isCompleted", false)
                .whereGreaterThanOrEqualTo("dueDate", in30m)
                .get()
                .await()
                .toObjects(Task::class.java)
                .sortedByDescending { it.dueDate }

        Log.d("ReminderWorker", "Fetched ${tasksDue.size} tasks: $tasksDue")
        return TaskList(tasksDue)
    }
}
