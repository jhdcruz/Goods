package io.github.jhdcruz.memo.ui.tasks

import android.content.Intent
import io.github.jhdcruz.memo.data.task.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TasksViewModelPreview : TasksViewModel() {
    private val _query = MutableStateFlow("")
    override val query: Flow<String> = _query

    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    override val taskList: Flow<List<Task>> = _taskList

    override fun onQueryChange(query: String) {
        _query.value = query
    }

    override fun onTaskListChange(taskList: List<Task>) {
        _taskList.value = taskList
    }

    override fun onVoiceSearch(): Intent {
        return Intent()
    }

    override suspend fun onSearch() {
        TODO("Preview only")
    }

    override suspend fun onTaskAdd(task: Task) {
        TODO("Preview only")
    }

    override suspend fun onTaskUpdate(uid: String, task: Task) {
        TODO("Preview only")
    }

    override suspend fun onTaskDelete(uid: String) {
        TODO("Preview only")
    }

    override suspend fun onTaskCompleted(uid: String) {
        TODO("Preview only")
    }
}
