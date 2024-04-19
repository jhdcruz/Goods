package io.github.jhdcruz.memo.service.reminders

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jhdcruz.memo.data.model.Task
import io.github.jhdcruz.memo.data.model.TaskList
import kotlinx.coroutines.tasks.await
import java.io.Serializable

/**
 * Fetches tasks that are due in the next 30 minutes
 * and starts [ReminderSchedulerService] with the fetched data
 * for scheduling notifications.
 */
class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun doWork(): Result {
        // Fetch the necessary data here
        val data = fetchData()

        val intent =
            Intent(applicationContext, ReminderSchedulerService::class.java).apply {
                putExtra("data", data as Serializable)
            }

        applicationContext.startService(intent)

        return Result.success()
    }

    private suspend fun fetchData(): TaskList {
        val uid =
            auth.currentUser?.uid
                ?: throw IllegalStateException("ReminderWorker: User not signed in")

        val currentDate = System.currentTimeMillis()
        val in30m = currentDate + (30 * 60 * 1000)

        // get tasks that are due in the next 30 minutes
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
