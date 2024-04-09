package io.github.jhdcruz.memo.ui.tasks

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.firebase.Timestamp
import io.github.jhdcruz.memo.data.model.Task
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase
import kotlinx.coroutines.flow.Flow

class TasksViewModelPreview : TasksViewModel() {
    override val query: Flow<String>
        get() = TODO("Not yet implemented")
    override val taskList: Flow<List<Task>>
        get() = TODO("Not yet implemented")
    override val taskTitle: Flow<String>
        get() = TODO("Not yet implemented")
    override val taskDescription: Flow<String>
        get() = TODO("Not yet implemented")
    override val taskCategory: Flow<String>
        get() = TODO("Not yet implemented")
    override val taskTags: Flow<List<String>>
        get() = TODO("Not yet implemented")
    override val taskAttachments: Flow<List<Map<String, String>>?>
        get() = TODO("Not yet implemented")
    override val taskSelectedDate: Flow<Long?>
        get() = TODO("Not yet implemented")
    override val taskSelectedHour: Flow<Int?>
        get() = TODO("Not yet implemented")
    override val taskSelectedMinute: Flow<Int?>
        get() = TODO("Not yet implemented")
    override val taskPriority: Flow<Int>
        get() = TODO("Not yet implemented")
    override val taskUpdated: Flow<Timestamp>
        get() = TODO("Not yet implemented")
    override val taskLocalAttachments: Flow<List<Pair<String, Uri>>>
        get() = TODO("Not yet implemented")

    override fun onVoiceSearch(): Intent {
        TODO("Not yet implemented")
    }

    override suspend fun onSearch() {
        TODO("Not yet implemented")
    }

    override suspend fun onTaskAdd(task: Task): FirestoreResponseUseCase {
        TODO("Not yet implemented")
    }

    override suspend fun onTaskUpdate(uid: String, task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun onTaskDelete(uid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun onTaskCompleted(uid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun onCategoryAdd(category: String) {
        TODO("Not yet implemented")
    }

    override suspend fun onCategoryUpdate(category: String, newCategory: String) {
        TODO("Not yet implemented")
    }

    override suspend fun onCategoriesDelete(categories: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun onTagAdd(tag: String) {
        TODO("Not yet implemented")
    }

    override suspend fun onTagUpdate(tag: String, newTag: String) {
        TODO("Not yet implemented")
    }

    override suspend fun onTagsDelete(tags: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun onAttachmentsUpload(
        id: String,
        attachments: List<Pair<String, Uri>>,
    ): FirestoreResponseUseCase {
        TODO("Not yet implemented")
    }

    override suspend fun onAttachmentDelete(id: String, path: String): FirestoreResponseUseCase {
        TODO("Not yet implemented")
    }

    override suspend fun onAttachmentDownload(path: String): FirestoreResponseUseCase {
        TODO("Not yet implemented")
    }

    override suspend fun onGetCategories(): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun onGetTags(): List<String> {
        TODO("Not yet implemented")
    }

    override fun onQueryChange(query: String) {
        TODO("Not yet implemented")
    }

    override fun onTaskListChange(taskList: List<Task>) {
        TODO("Not yet implemented")
    }

    override fun onTagsChange(tags: List<String>) {
        TODO("Not yet implemented")
    }

    override fun onCategoryChange(category: String) {
        TODO("Not yet implemented")
    }

    override fun onTaskTitleChange(title: String) {
        TODO("Not yet implemented")
    }

    override fun onTaskDescriptionChange(description: String) {
        TODO("Not yet implemented")
    }

    override fun onTaskCategoryChange(category: String) {
        TODO("Not yet implemented")
    }

    override fun onTaskTagsChange(tags: List<String>) {
        TODO("Not yet implemented")
    }

    override fun onTaskAttachmentsChange(attachments: List<Map<String, String>>?) {
        TODO("Not yet implemented")
    }

    override fun onTaskLocalAttachmentsChange(attachments: List<Pair<String, Uri>>) {
        TODO("Not yet implemented")
    }

    override suspend fun onTaskAttachmentPreview(
        context: Context,
        attachment: Map<String, String>,
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun onTaskAttachmentPreview(context: Context, attachment: Pair<String, Uri>) {
        TODO("Not yet implemented")
    }

    override fun removeTaskAttachment(
        attachment: Map<String, String>,
        originalAttachments: List<Map<String, String>>,
    ) {
        TODO("Not yet implemented")
    }

    override fun onTaskSelectedDateChange(date: Long) {
        TODO("Not yet implemented")
    }

    override fun onTaskSelectedHourChange(hour: Int) {
        TODO("Not yet implemented")
    }

    override fun onTaskSelectedMinuteChange(minute: Int) {
        TODO("Not yet implemented")
    }

    override fun onTaskPriorityChange(priority: Int) {
        TODO("Not yet implemented")
    }

    override fun onTaskUpdatedChange(updated: Timestamp) {
        TODO("Not yet implemented")
    }

    override fun getTaskDueDate(millis: Long, hour: Int, minute: Int): Timestamp {
        TODO("Not yet implemented")
    }
}
