package io.github.jhdcruz.memo.ui.tasks

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import io.github.jhdcruz.memo.data.model.Task
import kotlinx.coroutines.flow.Flow

abstract class TasksViewModel : ViewModel() {
    abstract val query: Flow<String>
    abstract val taskList: Flow<List<Task>>

    // Populating tasks
    abstract val taskTitle: Flow<String>
    abstract val taskDescription: Flow<String>
    abstract val taskCategory: Flow<String>
    abstract val taskTags: Flow<List<String>>
    abstract val taskAttachments: Flow<List<Uri>?>

    abstract val taskSelectedDate: Flow<Long?>
    abstract val taskSelectedHour: Flow<Int?>
    abstract val taskSelectedMinute: Flow<Int?>

    abstract val taskPriority: Flow<Int>
    abstract val taskUpdated: Flow<Timestamp>

    abstract fun onVoiceSearch(): Intent
    abstract suspend fun onSearch()

    // tasks operations
    abstract suspend fun onTaskAdd(task: Task)
    abstract suspend fun onTaskUpdate(uid: String, task: Task)
    abstract suspend fun onTaskDelete(uid: String)
    abstract suspend fun onTaskCompleted(uid: String)

    // singular tasks operations
    abstract suspend fun onCategoryAdd(category: String)
    abstract suspend fun onCategoryUpdate(category: String, newCategory: String)
    abstract suspend fun onCategoriesDelete(categories: List<String>)

    abstract suspend fun onTagAdd(tag: String)
    abstract suspend fun onTagUpdate(tag: String, newTag: String)
    abstract suspend fun onTagsDelete(tags: List<String>)

    // fetch
    abstract suspend fun onGetCategories(): List<String>
    abstract suspend fun onGetTags(): List<String>

    // input handlers
    abstract fun onQueryChange(query: String)
    abstract fun onTaskListChange(taskList: List<Task>)
    abstract fun onTagsChange(tags: List<String>)
    abstract fun onCategoryChange(category: String)

    abstract fun onTaskTitleChange(title: String)
    abstract fun onTaskDescriptionChange(description: String)
    abstract fun onTaskCategoryChange(category: String)
    abstract fun onTaskTagsChange(tags: List<String>)

    abstract fun onTaskAttachmentsChange(attachments: List<Uri>?)
    abstract fun removeTaskAttachment(index: Int)
    abstract fun onTaskSelectedDateChange(date: Long)
    abstract fun onTaskSelectedHourChange(hour: Int)
    abstract fun onTaskSelectedMinuteChange(minute: Int)

    abstract fun onTaskPriorityChange(priority: Int)
    abstract fun onTaskUpdatedChange(updated: Timestamp)

    abstract fun getTaskDueDate(millis: Long, hour: Int, minute: Int): Timestamp
}
