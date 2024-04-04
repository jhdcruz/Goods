package io.github.jhdcruz.memo.ui.tasks

import android.content.Intent
import androidx.lifecycle.ViewModel
import io.github.jhdcruz.memo.data.task.Task
import kotlinx.coroutines.flow.Flow

abstract class TasksViewModel : ViewModel() {
    abstract val query: Flow<String>
    abstract val taskList: Flow<List<Task>>

    abstract fun onQueryChange(query: String)
    abstract fun onTaskListChange(taskList: List<Task>)

    abstract fun onVoiceSearch(): Intent
    abstract suspend fun onSearch()

    abstract suspend fun onTaskAdd(task: Task)
    abstract suspend fun onTaskUpdate(uid: String, task: Task)
    abstract suspend fun onTaskDelete(uid: String)
    abstract suspend fun onTaskCompleted(uid: String)
}
