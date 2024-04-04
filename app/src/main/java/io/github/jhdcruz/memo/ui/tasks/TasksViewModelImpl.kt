package io.github.jhdcruz.memo.ui.tasks

import android.content.Intent
import android.speech.RecognizerIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jhdcruz.memo.data.task.Task
import io.github.jhdcruz.memo.data.task.TasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TasksViewModelImpl @Inject constructor(
    private val tasksRepository: TasksRepository,
) : TasksViewModel() {
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
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).also {
            it.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
            )
            it.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            it.putExtra(RecognizerIntent.EXTRA_PROMPT, "What tasks to search for?")
        }
    }

    override suspend fun onSearch() {
        val taskList = tasksRepository.onSearch(query.toString())
        onTaskListChange(taskList)
    }

    override suspend fun onTaskAdd(task: Task) {
        tasksRepository.onTaskAdd(task)
    }

    override suspend fun onTaskUpdate(uid: String, task: Task) {
        tasksRepository.onTaskUpdate(uid, task)
    }

    override suspend fun onTaskDelete(uid: String) {
        tasksRepository.onTaskDelete(uid)
    }

    override suspend fun onTaskCompleted(uid: String) {
        tasksRepository.onTaskCompleted(uid)
    }
}
