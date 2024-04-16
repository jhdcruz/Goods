package io.github.jhdcruz.memo.ui.tasks

import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import io.github.jhdcruz.memo.data.model.Task
import io.github.jhdcruz.memo.data.model.TaskAttachment
import kotlinx.coroutines.flow.Flow

abstract class TasksViewModel : ViewModel() {
    abstract val taskId: Flow<String>
    abstract val taskTitle: Flow<String>
    abstract val taskDescription: Flow<TextFieldValue>
    abstract val taskCategory: Flow<String>
    abstract val taskTags: Flow<List<String>>
    abstract val taskAttachments: Flow<Map<String, TaskAttachment>?>

    abstract val taskDueDate: Flow<Timestamp?>
    abstract val taskSelectedDate: Flow<Long?>
    abstract val taskSelectedHour: Flow<Int?>
    abstract val taskSelectedMinute: Flow<Int?>

    abstract val taskPriority: Flow<Int>
    abstract val taskUpdated: Flow<Timestamp?>

    // Attachments to be uploaded
    abstract val taskLocalAttachments: Flow<List<Pair<String, Uri>>>

    abstract fun getTaskDueDate(millis: Long, hour: Int, minute: Int): Timestamp

    abstract fun onTagsChange(tags: List<String>)
    abstract fun onCategoryChange(category: String)

    abstract fun onTaskIdChange(id: String)
    abstract fun onTaskTitleChange(title: String)
    abstract fun onTaskDescriptionChange(description: TextFieldValue)
    abstract fun onTaskCategoryChange(category: String)
    abstract fun onTaskTagsChange(tags: List<String>)

    abstract fun onTaskAttachmentsChange(attachments: Map<String, TaskAttachment>?)
    abstract fun onTaskLocalAttachmentsChange(attachments: List<Pair<String, Uri>>)
    abstract fun onTaskAttachmentPreview(context: Context, attachment: Map<String, String>)
    abstract fun onTaskAttachmentPreview(context: Context, attachment: Pair<String, Uri>)
    abstract fun removeTaskAttachment(
        taskId: String,
        filename: String,
        originalAttachments: Map<String, TaskAttachment>,
    )

    abstract fun onTaskDueDateChange(date: Timestamp)
    abstract fun onTaskSelectedDateChange(date: Long)
    abstract fun onTaskSelectedHourChange(hour: Int)
    abstract fun onTaskSelectedMinuteChange(minute: Int)

    abstract fun onTaskPriorityChange(priority: Int)
    abstract fun onTaskUpdatedChange(updated: Timestamp)


    abstract fun onClearInput()
    abstract fun onTaskPreview(task: Task)
}
