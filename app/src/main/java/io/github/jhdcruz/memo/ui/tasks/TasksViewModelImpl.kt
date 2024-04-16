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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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

    private val _isFetchingTasks = MutableStateFlow(false)
    override val isFetchingTasks: Flow<Boolean> = _isFetchingTasks

    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    override val taskList: Flow<List<Task>> = _taskList

    private val _tags = MutableStateFlow<List<String>>(emptyList())
    override val tags: Flow<List<String>> = _tags

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    override val categories: Flow<List<String>> = _categories

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
        MutableStateFlow<Map<String, TaskAttachment>?>(null)
    override val taskAttachments: Flow<Map<String, TaskAttachment>?> = _taskAttachments

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

    override fun onGetTasks() {
        viewModelScope.launch {
            onIsFetchingTasksChange(true)
            _taskList.value = tasksRepository.onGetTasks()
            onIsFetchingTasksChange(false)
        }
    }

    override fun onSearch() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_query.value.isNotEmpty()) {
                onIsFetchingTasksChange(true)

                val taskList = tasksRepository.onSearch(_query.value)
                onTaskListChange(taskList)

                onIsFetchingTasksChange(false)
            } else {
                onGetTasks()
            }
        }
    }

    override fun onTaskAdd(
        task: Task,
        localAttachments: List<Pair<String, Uri>>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val taskJob = tasksRepository.onTaskAdd(task) as FirestoreResponseUseCase.Success
            val taskId = taskJob.result as String

            // upload local files
            if (localAttachments.isNotEmpty()) {
                onAttachmentsUpload(taskId, localAttachments)
            }

            onClearInput()
            onGetTasks()
        }
    }

    override fun onTaskUpdate(
        id: String,
        task: Task,
        localAttachments: List<Pair<String, Uri>>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val taskUpdateJob = async { tasksRepository.onTaskUpdate(id, task) }
            val attachmentsUploadJob = async {
                if (localAttachments.isNotEmpty()) {
                    onAttachmentsUpload(id, localAttachments)
                }
            }

            taskUpdateJob.await()
            attachmentsUploadJob.await()

            onClearInput()
            onGetTasks()
        }
    }

    override fun onTaskDelete(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val taskDeleteJob = async { tasksRepository.onTaskDelete(id) }
            val attachmentDeleteJob = async { attachmentsRepository.onAttachmentDeleteAll(id) }

            taskDeleteJob.await()
            attachmentDeleteJob.await()

            onGetTasks()
        }
    }

    override fun onTaskCompleted(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // update local task for AnimatedVisibility
            _taskList.value = _taskList.value.toMutableList().apply {
                val index = indexOfFirst { it.id == id }
                set(index, get(index).copy(isCompleted = true))
            }

            // remove from the list
            _taskList.value = _taskList.value.toMutableList().apply {
                removeIf { it.id == id }
            }

            tasksRepository.onTaskCompleted(id)
        }
    }

    override fun onCategoryAdd(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.onCategoryAdd(category)
        }
    }

    override fun onCategoryUpdate(category: String, newCategory: String) {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.onCategoryUpdate(category, newCategory)
        }
    }

    override fun onCategoriesDelete(categories: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.onCategoriesDelete(categories)
        }
    }

    override fun onTagAdd(tag: String) {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.onTagAdd(tag)
        }
    }

    override fun onTagUpdate(tag: String, newTag: String) {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.onTagUpdate(tag, newTag)
        }
    }

    override fun onTagsDelete(tags: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.onTagsDelete(tags)
        }
    }

    override fun onAttachmentsUpload(
        id: String,
        attachments: List<Pair<String, Uri>>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            attachmentsRepository.onAttachmentsUpload(id, attachments)
        }
    }

    override fun onAttachmentDelete(id: String, path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            attachmentsRepository.onAttachmentDelete(id, path)
        }
    }

    override fun onAttachmentDownload(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            attachmentsRepository.onAttachmentDownload(path)
        }
    }

    override fun onGetCategories() {
        viewModelScope.launch {
            _categories.value = tasksRepository.onGetCategories()
        }
    }

    override fun onGetTags() {
        viewModelScope.launch {
            _tags.value = tasksRepository.onGetTags()
        }
    }

    override fun onIsFetchingTasksChange(isFetching: Boolean) {
        _isFetchingTasks.value = isFetching
    }

    override fun onQueryChange(query: String) {
        _query.value = query
    }

    override fun onTaskListChange(taskList: List<Task>) {
        _taskList.value = taskList
    }

    override fun onLocalTagsChange(tags: List<String>) {
        _tags.value = tags
    }

    override fun onTagsChange(tags: List<String>) {
        _taskTags.value = tags
    }

    override fun onLocalCategoryChange(categories: List<String>) {
        _categories.value = categories
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

    override fun onTaskAttachmentsChange(attachments: Map<String, TaskAttachment>?) {
        _taskAttachments.value = attachments
    }

    override fun onTaskLocalAttachmentsChange(attachments: List<Pair<String, Uri>>) {
        _taskLocalAttachments.value = attachments
    }

    override fun onTaskAttachmentPreview(
        context: Context,
        attachment: Map<String, String>,
    ) {
        viewModelScope.launch {
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
    }

    override fun onTaskAttachmentPreview(context: Context, attachment: Pair<String, Uri>) {
        viewModelScope.launch {
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
    }

    override fun removeTaskAttachment(
        taskId: String,
        filename: String,
        originalAttachments: Map<String, TaskAttachment>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
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
    }

    override fun onTaskPreview(task: Task) {
        viewModelScope.launch {
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
    }

    override fun getTaskDueDate(millis: Long, hour: Int, minute: Int): Timestamp {
        return createTimestamp(millis, hour, minute)
    }
}
