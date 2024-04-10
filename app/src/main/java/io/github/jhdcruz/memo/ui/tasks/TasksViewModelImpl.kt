package io.github.jhdcruz.memo.ui.tasks

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jhdcruz.memo.data.model.Task
import io.github.jhdcruz.memo.data.model.TaskAttachment
import io.github.jhdcruz.memo.data.task.AttachmentsRepository
import io.github.jhdcruz.memo.data.task.TasksRepository
import io.github.jhdcruz.memo.domain.createTimestamp
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase
import io.github.jhdcruz.memo.domain.toTimestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TasksViewModelImpl @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val attachmentsRepository: AttachmentsRepository,
) : TasksViewModel() {
    private val _query = MutableStateFlow("")
    override val query: Flow<String> = _query

    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    override val taskList: Flow<List<Task>> = _taskList

    // Populating tasks
    private val _taskId = MutableStateFlow("")
    override val taskId: Flow<String> = _taskId

    private val _taskTitle = MutableStateFlow("")
    override val taskTitle: Flow<String> = _taskTitle

    private val _taskDescription = MutableStateFlow(TextFieldValue(""))
    override val taskDescription: Flow<TextFieldValue> = _taskDescription

    private val _taskCategory = MutableStateFlow("")
    override val taskCategory: Flow<String> = _taskCategory

    private val _taskTags = MutableStateFlow<List<String>>(emptyList())
    override val taskTags: Flow<List<String>> = _taskTags

    private val _taskAttachments =
        MutableStateFlow<Map<Int, TaskAttachment>?>(null)
    override val taskAttachments: Flow<Map<Int, TaskAttachment>?> = _taskAttachments

    private val _taskDueDate = MutableStateFlow<Timestamp?>(null)
    override val taskDueDate: Flow<Timestamp?> = _taskDueDate

    private val _taskSelectedDate = MutableStateFlow<Long?>(null)
    override val taskSelectedDate: Flow<Long?> = _taskSelectedDate

    private val _taskSelectedHour = MutableStateFlow<Int?>(null)
    override val taskSelectedHour: Flow<Int?> = _taskSelectedHour

    private val _taskSelectedMinute = MutableStateFlow<Int?>(null)
    override val taskSelectedMinute: Flow<Int?> = _taskSelectedMinute


    private val _taskPriority = MutableStateFlow(0)
    override val taskPriority: Flow<Int> = _taskPriority

    private val _taskUpdated = MutableStateFlow<Timestamp?>(LocalDateTime.now().toTimestamp())
    override val taskUpdated: Flow<Timestamp?> = _taskUpdated

    private val _taskLocalAttachments = MutableStateFlow<List<Pair<String, Uri>>>(emptyList())
    override val taskLocalAttachments: Flow<List<Pair<String, Uri>>> = _taskLocalAttachments

    init {
        viewModelScope.launch {
            onGetTasks()
        }
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

    override suspend fun onGetTasks() {
        _taskList.value = tasksRepository.onGetTasks()
    }

    override suspend fun onSearch() {
        val taskList = tasksRepository.onSearch(query.toString())
        onTaskListChange(taskList)
    }

    override suspend fun onTaskAdd(task: Task): String {
        val taskJob = tasksRepository.onTaskAdd(task) as FirestoreResponseUseCase.Success
        val taskId = taskJob.result as String

        // query task
        val newTask = tasksRepository.onGetTask(taskId)

        // append to taskList
        _taskList.value = _taskList.value.toMutableList().apply {
            add(newTask)
        }

        return taskId
    }

    override suspend fun onTaskUpdate(id: String, task: Task) {
        viewModelScope.launch {
            _taskList.value.toMutableList().apply {
                val index = indexOfFirst { it.id == id }
                set(index, task)
            }

            tasksRepository.onTaskUpdate(id, task)
        }
    }

    override suspend fun onTaskDelete(id: String) {
        viewModelScope.launch {
            _taskList.value = _taskList.value.toMutableList().apply {
                removeIf { it.id == id }
            }

            tasksRepository.onTaskDelete(id)
        }
    }

    override suspend fun onTaskCompleted(id: String) {
        viewModelScope.launch {
            // update task status for AnimatedVisibility
            _taskList.value.firstOrNull { it.id == id }?.let {
                tasksRepository.onTaskUpdate(id, it.copy(isCompleted = true))
            }

            // remove from the list
            _taskList.value = _taskList.value.toMutableList().apply {
                removeIf { it.id == id }
            }

            tasksRepository.onTaskCompleted(id)
        }
    }

    override suspend fun onCategoryAdd(category: String) {
        tasksRepository.onCategoryAdd(category)
    }

    override suspend fun onCategoryUpdate(category: String, newCategory: String) {
        tasksRepository.onCategoryUpdate(category, newCategory)
    }

    override suspend fun onCategoriesDelete(categories: List<String>) {
        tasksRepository.onCategoriesDelete(categories)
    }

    override suspend fun onTagAdd(tag: String) {
        tasksRepository.onTagAdd(tag)
    }

    override suspend fun onTagUpdate(tag: String, newTag: String) {
        tasksRepository.onTagUpdate(tag, newTag)
    }

    override suspend fun onTagsDelete(tags: List<String>) {
        tasksRepository.onTagsDelete(tags)
    }

    override suspend fun onAttachmentsUpload(
        id: String,
        attachments: List<Pair<String, Uri>>,
    ) {
        withContext(Dispatchers.IO) {
            attachmentsRepository.onAttachmentsUpload(id, attachments)
        }
    }

    override suspend fun onAttachmentDelete(id: String, path: String) {
        attachmentsRepository.onAttachmentDelete(id, path)
    }

    override suspend fun onAttachmentDownload(path: String) {
        attachmentsRepository.onAttachmentDownload(path)
    }

    override suspend fun onGetCategories(): List<String> {
        return tasksRepository.onGetCategories()
    }

    override suspend fun onGetTags(): List<String> {
        return tasksRepository.onGetTags()
    }

    override fun onQueryChange(query: String) {
        _query.value = query
    }

    override fun onTaskListChange(taskList: List<Task>) {
        _taskList.value = taskList
    }

    override fun onTagsChange(tags: List<String>) {
        _taskTags.value = tags
    }

    override fun onCategoryChange(category: String) {
        _taskCategory.value = category
    }

    override fun onTaskIdChange(id: String) {
        _taskId.value = id
    }

    override fun onTaskTitleChange(title: String) {
        _taskTitle.value = title
    }

    override fun onTaskDescriptionChange(description: TextFieldValue) {
        _taskDescription.value = description
    }

    override fun onTaskCategoryChange(category: String) {
        _taskCategory.value = category
    }

    override fun onTaskTagsChange(tags: List<String>) {
        _taskTags.value = tags
    }

    override fun onTaskAttachmentsChange(attachments: Map<Int, TaskAttachment>?) {
        _taskAttachments.value = attachments
    }

    override fun onTaskLocalAttachmentsChange(attachments: List<Pair<String, Uri>>) {
        _taskLocalAttachments.value = attachments
    }

    override suspend fun onTaskAttachmentPreview(
        context: Context,
        attachment: Map<String, String>,
    ) {
        val intent = Intent(Intent.ACTION_VIEW)
        val fileProvider = context.contentResolver

        intent.setDataAndType(
            Uri.parse(attachment.values.last()),
            fileProvider.getType(Uri.parse(attachment.values.last()))
        ).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        context.startActivity(intent)
    }

    override suspend fun onTaskAttachmentPreview(context: Context, attachment: Pair<String, Uri>) {
        val intent = Intent(Intent.ACTION_VIEW)
        val fileProvider = context.contentResolver

        intent.setDataAndType(
            attachment.second,
            fileProvider.getType(attachment.second)
        ).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        context.startActivity(intent)
    }

    override suspend fun removeTaskAttachment(
        taskId: String,
        filename: String,
        originalAttachments: Map<Int, TaskAttachment>,
    ) {
        viewModelScope.launch {
            attachmentsRepository.onAttachmentDelete(taskId, filename)

            // remove attachment that contains the same filename
            val updatedAttachments = originalAttachments.toMutableMap().apply {
                entries.removeIf { it.value.name == filename }
            }

            _taskAttachments.value = updatedAttachments
        }
    }

    override fun onTaskDueDateChange(date: Timestamp) {
        _taskDueDate.value = date
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

    override fun onClearInput() {
        _taskTitle.value = ""
        _taskDescription.value = TextFieldValue("")
        _taskCategory.value = ""
        _taskTags.value = emptyList()
        _taskAttachments.value = null
        _taskLocalAttachments.value = emptyList()
        _taskDueDate.value = null
        _taskSelectedDate.value = null
        _taskSelectedHour.value = null
        _taskSelectedMinute.value = null
        _taskPriority.value = 0
        _taskUpdated.value = LocalDateTime.now().toTimestamp()
    }

    override fun onTaskPreview(task: Task) {
        _taskId.value = task.id!! // will never be null (handled by firestore auto-id)
        _taskTitle.value = task.title
        _taskDueDate.value = task.dueDate
        _taskPriority.value = task.priority
        _taskUpdated.value = task.updated

        if (task.description?.isNotEmpty() != null) {
            _taskDescription.value = TextFieldValue(task.description)
        }
        if (task.category?.isNotEmpty() != null) {
            _taskCategory.value = task.category
        }
        if (task.tags?.isNotEmpty() != null) {
            _taskTags.value = task.tags
        }
        if (task.attachments?.isNotEmpty() != null) {
            _taskAttachments.value = task.attachments
        }
    }

    override fun getTaskDueDate(millis: Long, hour: Int, minute: Int): Timestamp {
        return createTimestamp(millis, hour, minute)
    }
}
