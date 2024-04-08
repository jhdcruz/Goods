package io.github.jhdcruz.memo.ui.tasks

import android.content.Intent
import android.net.Uri
import com.google.firebase.Timestamp
import io.github.jhdcruz.memo.data.model.Task
import kotlinx.coroutines.flow.Flow

class TasksViewModelPreview : TasksViewModel() {
    override val query: Flow<String>
        get() = TODO("Preview only")
    override val taskList: Flow<List<Task>>
        get() = TODO("Preview only")
    override val taskTitle: Flow<String>
        get() = TODO("Preview only")
    override val taskDescription: Flow<String>
        get() = TODO("Preview only")
    override val taskCategory: Flow<String>
        get() = TODO("Preview only")
    override val taskTags: Flow<List<String>>
        get() = TODO("Preview only")
    override val taskAttachments: Flow<List<Uri>?>
        get() = TODO("Preview only")
    override val taskSelectedDate: Flow<Long?>
        get() = TODO("Preview only")
    override val taskSelectedHour: Flow<Int?>
        get() = TODO("Preview only")
    override val taskSelectedMinute: Flow<Int?>
        get() = TODO("Preview only")
    override val taskPriority: Flow<Int>
        get() = TODO("Preview only")
    override val taskUpdated: Flow<Timestamp>
        get() = TODO("Preview only")

    override fun onVoiceSearch(): Intent {
        TODO("Preview only")
    }

    override suspend fun onSearch() {
        TODO("Preview only")
    }

    override suspend fun onTaskAdd(task: Task) {
        TODO("Preview only")
    }

    override suspend fun onTaskUpdate(uid: String, task: Task) {
        TODO("Preview only")
    }

    override suspend fun onTaskDelete(uid: String) {
        TODO("Preview only")
    }

    override suspend fun onTaskCompleted(uid: String) {
        TODO("Preview only")
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

    override suspend fun onGetCategories(): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun onGetTags(): List<String> {
        TODO("Not yet implemented")
    }

    override fun onQueryChange(query: String) {
        TODO("Preview only")
    }

    override fun onTaskListChange(taskList: List<Task>) {
        TODO("Preview only")
    }

    override fun onTagsChange(tags: List<String>) {
        TODO("Not yet implemented")
    }

    override fun onCategoryChange(category: String) {
        TODO("Not yet implemented")
    }

    override fun onTaskTitleChange(title: String) {
        TODO("Preview only")
    }

    override fun onTaskDescriptionChange(description: String) {
        TODO("Preview only")
    }

    override fun onTaskCategoryChange(category: String) {
        TODO("Preview only")
    }

    override fun onTaskTagsChange(tags: List<String>) {
        TODO("Preview only")
    }

    override fun onTaskAttachmentsChange(attachments: List<Uri>?) {
        TODO("Preview only")
    }

    override fun removeTaskAttachment(index: Int) {
        TODO("Preview only")
    }

    override fun onTaskSelectedDateChange(date: Long) {
        TODO("Preview only")
    }

    override fun onTaskSelectedHourChange(hour: Int) {
        TODO("Preview only")
    }

    override fun onTaskSelectedMinuteChange(minute: Int) {
        TODO("Preview only")
    }

    override fun onTaskPriorityChange(priority: Int) {
        TODO("Preview only")
    }

    override fun onTaskUpdatedChange(updated: Timestamp) {
        TODO("Preview only")
    }

    override fun getTaskDueDate(millis: Long, hour: Int, minute: Int): Timestamp {
        TODO("Preview only")
    }
}
