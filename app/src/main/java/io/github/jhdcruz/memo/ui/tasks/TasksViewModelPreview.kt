package io.github.jhdcruz.memo.ui.tasks

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import com.google.firebase.Timestamp
import io.github.jhdcruz.memo.data.model.Task
import io.github.jhdcruz.memo.data.model.TaskAttachment
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TasksViewModelPreview : TasksViewModel() {
    override val query = flowOf("Generic Query")
    override val taskList = flowOf(listOf<Task>())
    override val taskId: Flow<String> = flowOf("Generic ID")
    override val taskTitle = flowOf("Generic Title")
    override val taskDescription = flowOf(TextFieldValue("Generic Description"))
    override val taskCategory = flowOf("Generic Category")
    override val taskTags = flowOf(listOf("Generic Tag"))
    override val taskAttachments: Flow<Map<Int, TaskAttachment>?> = flowOf(null)
    override val taskDueDate: Flow<Timestamp?> = flowOf(null)
    override val taskSelectedDate = flowOf(0L)
    override val taskSelectedHour = flowOf(0)
    override val taskSelectedMinute = flowOf(0)
    override val taskPriority = flowOf(0)
    override val taskUpdated = flowOf(Timestamp.now())
    override val taskLocalAttachments = flowOf(listOf<Pair<String, Uri>>())

    override fun onVoiceSearch(): Intent {
        return Intent()
    }

    override suspend fun onSearch() {}
    override suspend fun onGetTasks() {}

    override suspend fun onTaskAdd(task: Task): FirestoreResponseUseCase {
        return FirestoreResponseUseCase.Success("Generic Success")
    }

    override suspend fun onTaskUpdate(uid: String, task: Task) {}

    override suspend fun onTaskDelete(uid: String) {}

    override suspend fun onTaskCompleted(uid: String) {}

    override suspend fun onCategoryAdd(category: String) {}

    override suspend fun onCategoryUpdate(category: String, newCategory: String) {}

    override suspend fun onCategoriesDelete(categories: List<String>) {}

    override suspend fun onTagAdd(tag: String) {}

    override suspend fun onTagUpdate(tag: String, newTag: String) {}

    override suspend fun onTagsDelete(tags: List<String>) {}

    override suspend fun onAttachmentsUpload(
        id: String,
        attachments: List<Pair<String, Uri>>,
    ) {
    }

    override suspend fun onAttachmentDelete(id: String, path: String) {}
    override suspend fun onAttachmentDownload(path: String) {}

    override suspend fun onGetCategories(): List<String> {
        return listOf("Generic Category")
    }

    override suspend fun onGetTags(): List<String> {
        return listOf("Generic Tag")
    }

    override fun onQueryChange(query: String) {}

    override fun onTaskListChange(taskList: List<Task>) {}

    override fun onTagsChange(tags: List<String>) {}

    override fun onCategoryChange(category: String) {}
    override fun onTaskIdChange(id: String) {}

    override fun onTaskTitleChange(title: String) {}

    override fun onTaskDescriptionChange(description: TextFieldValue) {}

    override fun onTaskCategoryChange(category: String) {}

    override fun onTaskTagsChange(tags: List<String>) {}
    override fun onTaskAttachmentsChange(attachments: Map<Int, TaskAttachment>?) {
    }


    override fun onTaskLocalAttachmentsChange(attachments: List<Pair<String, Uri>>) {}

    override suspend fun onTaskAttachmentPreview(
        context: Context,
        attachment: Map<String, String>,
    ) {
    }

    override suspend fun onTaskAttachmentPreview(context: Context, attachment: Pair<String, Uri>) {}
    override suspend fun removeTaskAttachment(
        taskId: String,
        filename: String,
        originalAttachments: Map<Int, TaskAttachment>
    ) {
    }

    override fun onTaskDueDateChange(date: Timestamp) {}

    override fun onTaskSelectedDateChange(date: Long) {}

    override fun onTaskSelectedHourChange(hour: Int) {}

    override fun onTaskSelectedMinuteChange(minute: Int) {}

    override fun onTaskPriorityChange(priority: Int) {}

    override fun onTaskUpdatedChange(updated: Timestamp) {}

    override fun onClearInput() {}
    override fun onTaskPreview(task: Task) {}

    override fun getTaskDueDate(millis: Long, hour: Int, minute: Int): Timestamp {
        return Timestamp.now()
    }
}
