package io.github.jhdcruz.memo.ui.tasks

import android.content.Intent
import android.speech.RecognizerIntent
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jhdcruz.memo.data.task.Task
import io.github.jhdcruz.memo.data.task.TasksRepository
import io.github.jhdcruz.memo.domain.createTimestamp
import io.github.jhdcruz.memo.domain.toTimestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDateTime
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

    // Populating tasks
    private val _taskTitle = MutableStateFlow("")
    override val taskTitle: Flow<String> = _taskTitle

    private val _taskDescription = MutableStateFlow("")
    override val taskDescription: Flow<String> = _taskDescription

    private val _taskCategory = MutableStateFlow("")
    override val taskCategory: Flow<String> = _taskCategory

    private val _taskTags = MutableStateFlow<List<String>>(emptyList())
    override val taskTags: Flow<List<String>> = _taskTags

    private val _taskAttachments = MutableStateFlow<List<String>>(emptyList())
    override val taskAttachments: Flow<List<String>> = _taskAttachments

    private val _taskSelectedDate = MutableStateFlow<Long?>(null)
    override val taskSelectedDate: Flow<Long?> = _taskSelectedDate

    private val _taskSelectedHour = MutableStateFlow<Int?>(null)
    override val taskSelectedHour: Flow<Int?> = _taskSelectedHour

    private val _taskSelectedMinute = MutableStateFlow<Int?>(null)
    override val taskSelectedMinute: Flow<Int?> = _taskSelectedMinute


    private val _taskPriority = MutableStateFlow(0)
    override val taskPriority: Flow<Int> = _taskPriority

    private val _taskUpdated = MutableStateFlow(LocalDateTime.now().toTimestamp())
    override val taskUpdated: Flow<Timestamp> = _taskUpdated

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

    override fun onQueryChange(query: String) {
        _query.value = query
    }

    override fun onTaskListChange(taskList: List<Task>) {
        _taskList.value = taskList
    }

    override fun onTaskTitleChange(title: String) {
        _taskTitle.value = title
    }

    override fun onTaskDescriptionChange(description: String) {
        _taskDescription.value = description
    }

    override fun onTaskCategoryChange(category: String) {
        _taskCategory.value = category
    }

    override fun onTaskTagsChange(tags: List<String>) {
        _taskTags.value = tags
    }

    override fun onTaskAttachmentsChange(attachments: List<String>) {
        _taskAttachments.value = attachments
    }

    override fun onTaskSelectedDateChange(date: Long) {
        _taskSelectedDate.value = date
    }

    override fun onTaskSelectedHourChange(hour: Int) {
        _taskSelectedHour.value = hour
    }

    override fun onTaskSelectedMinuteChange(minute: Int) {
        _taskSelectedMinute.value = minute
    }

    override fun onTaskPriorityChange(priority: Int) {
        _taskPriority.value = priority
    }

    override fun onTaskUpdatedChange(updated: Timestamp) {
        _taskUpdated.value = updated
    }

    override fun getTaskDueDate(millis: Long, hour: Int, minute: Int): Timestamp {
        return createTimestamp(millis, hour, minute)
    }
}
