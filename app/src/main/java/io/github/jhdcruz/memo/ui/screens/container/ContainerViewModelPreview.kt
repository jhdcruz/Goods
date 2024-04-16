package io.github.jhdcruz.memo.ui.screens.container

import android.content.Intent
import android.net.Uri
import io.github.jhdcruz.memo.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ContainerViewModelPreview : ContainerViewModel() {
    override val query: Flow<String> = flowOf("")
    override val isFetchingTasks: Flow<Boolean> = flowOf(false)
    override val taskList: Flow<List<Task>> = flowOf(emptyList())
    override val tags: Flow<List<String>> = flowOf(emptyList())
    override val categories: Flow<List<String>> = flowOf(emptyList())

    override fun onVoiceSearch(): Intent {
        return Intent()
    }

    override fun onSearch() {
    }

    override fun onGetTasks() {
    }

    override fun onTaskAdd(task: Task, localAttachments: List<Pair<String, Uri>>) {
    }

    override fun onTaskUpdate(id: String, task: Task, localAttachments: List<Pair<String, Uri>>) {
    }

    override fun onTaskDelete(id: String) {
    }

    override fun onTaskCompleted(id: String) {
    }

    override fun onCategoryAdd(category: String) {
    }

    override fun onCategoryUpdate(category: String, newCategory: String) {
    }

    override fun onCategoriesDelete(categories: List<String>) {
    }

    override fun onTagAdd(tag: String) {
    }

    override fun onTagUpdate(tag: String, newTag: String) {
    }

    override fun onTagsDelete(tags: List<String>) {
    }

    override fun onAttachmentsUpload(id: String, attachments: List<Pair<String, Uri>>) {
    }

    override fun onGetCategories() {
    }

    override fun onGetTags() {
    }

    override fun onIsFetchingTasksChange(isFetching: Boolean) {
    }

    override fun onQueryChange(query: String) {
    }

    override fun onTaskListChange(taskList: List<Task>) {
    }

    override fun onLocalTagsChange(tags: List<String>) {
    }

    override fun onLocalCategoryChange(categories: List<String>) {
    }
}
