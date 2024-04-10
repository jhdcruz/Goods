package io.github.jhdcruz.memo.ui.tasks

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import io.github.jhdcruz.memo.data.model.Task
import io.github.jhdcruz.memo.data.model.TaskAttachment
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase
import kotlinx.coroutines.flow.Flow

abstract class TasksViewModel : ViewModel() {
    abstract val query: Flow<String>
    abstract val taskList: Flow<List<Task>>

    // Populating tasks
    abstract val taskId: Flow<String>
    abstract val taskTitle: Flow<String>
    abstract val taskDescription: Flow<TextFieldValue>
    abstract val taskCategory: Flow<String>
    abstract val taskTags: Flow<List<String>>
    abstract val taskAttachments: Flow<Map<Int, TaskAttachment>?>

    abstract val taskDueDate: Flow<Timestamp?>
    abstract val taskSelectedDate: Flow<Long?>
    abstract val taskSelectedHour: Flow<Int?>
    abstract val taskSelectedMinute: Flow<Int?>

    abstract val taskPriority: Flow<Int>
    abstract val taskUpdated: Flow<Timestamp?>

    // Attachments to be uploaded
    abstract val taskLocalAttachments: Flow<List<Pair<String, Uri>>>

    abstract fun onVoiceSearch(): Intent
    abstract suspend fun onSearch()

    // fetch operations
    abstract suspend fun onGetTasks()

    // tasks operations
    abstract suspend fun onTaskAdd(task: Task): String
    abstract suspend fun onTaskUpdate(id: String, task: Task)
    abstract suspend fun onTaskDelete(id: String)
    abstract suspend fun onTaskCompleted(id: String)

    // singular tasks operations
    abstract suspend fun onCategoryAdd(category: String)
    abstract suspend fun onCategoryUpdate(category: String, newCategory: String)
    abstract suspend fun onCategoriesDelete(categories: List<String>)

    abstract suspend fun onTagAdd(tag: String)
    abstract suspend fun onTagUpdate(tag: String, newTag: String)
    abstract suspend fun onTagsDelete(tags: List<String>)

    abstract suspend fun onAttachmentsUpload(
        id: String,
        attachments: List<Pair<String, Uri>>,
    )

    abstract suspend fun onAttachmentDelete(id: String, path: String)
    abstract suspend fun onAttachmentDownload(path: String)

    // fetch
    abstract suspend fun onGetCategories(): List<String>
    abstract suspend fun onGetTags(): List<String>

    // input handlers
    abstract fun onQueryChange(query: String)
    abstract fun onTaskListChange(taskList: List<Task>)
    abstract fun onTagsChange(tags: List<String>)
    abstract fun onCategoryChange(category: String)

    abstract fun onTaskIdChange(id: String)
    abstract fun onTaskTitleChange(title: String)
    abstract fun onTaskDescriptionChange(description: TextFieldValue)
    abstract fun onTaskCategoryChange(category: String)
    abstract fun onTaskTagsChange(tags: List<String>)

    abstract fun onTaskAttachmentsChange(attachments: Map<Int, TaskAttachment>?)
    abstract fun onTaskLocalAttachmentsChange(attachments: List<Pair<String, Uri>>)
    abstract suspend fun onTaskAttachmentPreview(context: Context, attachment: Map<String, String>)
    abstract suspend fun onTaskAttachmentPreview(context: Context, attachment: Pair<String, Uri>)
    abstract suspend fun removeTaskAttachment(
        taskId: String,
        filename: String,
        originalAttachments: Map<Int, TaskAttachment>,
    )

    abstract fun onTaskDueDateChange(date: Timestamp)
    abstract fun onTaskSelectedDateChange(date: Long)
    abstract fun onTaskSelectedHourChange(hour: Int)
    abstract fun onTaskSelectedMinuteChange(minute: Int)

    abstract fun onTaskPriorityChange(priority: Int)
    abstract fun onTaskUpdatedChange(updated: Timestamp)


    abstract fun onClearInput()
    abstract fun onTaskPreview(task: Task)

    abstract fun getTaskDueDate(millis: Long, hour: Int, minute: Int): Timestamp
}
