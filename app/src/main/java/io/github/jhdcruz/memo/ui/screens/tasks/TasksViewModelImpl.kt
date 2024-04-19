package io.github.jhdcruz.memo.ui.screens.tasks

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jhdcruz.memo.data.model.Task
import io.github.jhdcruz.memo.data.model.TaskAttachment
import io.github.jhdcruz.memo.data.task.AttachmentsRepository
import io.github.jhdcruz.memo.data.task.TasksRepository
import io.github.jhdcruz.memo.domain.createTimestamp
import io.github.jhdcruz.memo.domain.toTimestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TasksViewModelImpl
    @Inject
    constructor(
        private val tasksRepository: TasksRepository,
        private val attachmentsRepository: AttachmentsRepository,
    ) : TasksViewModel() {
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

        override fun getTaskDueDate(
            millis: Long,
            hour: Int,
            minute: Int,
        ): Timestamp {
            return createTimestamp(millis, hour, minute)
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
                    fileProvider.getType(Uri.parse(attachment.values.last())),
                ).apply {
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

                context.startActivity(intent)
            }
        }

        override fun onTaskAttachmentPreview(
            context: Context,
            attachment: Pair<String, Uri>,
        ) {
            viewModelScope.launch {
                val intent = Intent(Intent.ACTION_VIEW)
                val fileProvider = context.contentResolver

                intent.setDataAndType(
                    attachment.second,
                    fileProvider.getType(attachment.second),
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
                val updatedAttachments =
                    originalAttachments.toMutableMap().apply {
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
    }
