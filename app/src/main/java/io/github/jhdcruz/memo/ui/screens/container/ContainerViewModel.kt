package io.github.jhdcruz.memo.ui.screens.container

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import io.github.jhdcruz.memo.data.model.Task
import kotlinx.coroutines.flow.Flow

abstract class ContainerViewModel : ViewModel() {
    abstract val query: Flow<String>

    abstract val isFetchingTasks: Flow<Boolean>
    abstract val taskList: Flow<List<Task>>

    abstract val tags: Flow<List<String>>
    abstract val categories: Flow<List<String>>

    /**
     * Reruns the [io.github.jhdcruz.memo.service.reminders.ReminderWorker]
     * to fetch tasks that are due in the next 30 minutes for reminders.
     *
     * Use when a task is created/updated and are due in the next 30 minutes.
     */
    abstract fun restartReminderWorker(context: Context)

    abstract fun onVoiceSearch(): Intent

    abstract fun onSearch()

    abstract fun onGetTasks()

    abstract fun onTaskAdd(
        task: Task,
        localAttachments: List<Pair<String, Uri>>,
    )

    abstract fun onTaskUpdate(
        id: String,
        task: Task,
        localAttachments: List<Pair<String, Uri>>,
    )

    abstract fun onTaskDelete(id: String)

    abstract fun onTaskCompleted(id: String)

    abstract fun onCategoryAdd(category: String)

    abstract fun onCategoryUpdate(
        category: String,
        newCategory: String,
    )

    abstract fun onCategoriesDelete(categories: List<String>)

    abstract fun onTagAdd(tag: String)

    abstract fun onTagUpdate(
        tag: String,
        newTag: String,
    )

    abstract fun onTagsDelete(tags: List<String>)

    abstract fun onAttachmentsUpload(
        id: String,
        attachments: List<Pair<String, Uri>>,
    )

    abstract fun onGetCategories()

    abstract fun onGetTags()

    abstract fun onFilterCategory(category: String)

    abstract fun onFilterTag(tag: String)

    abstract fun onIsFetchingTasksChange(isFetching: Boolean)

    abstract fun onQueryChange(query: String)

    abstract fun onTaskListChange(taskList: List<Task>)

    abstract fun onLocalTagsChange(tags: List<String>)

    abstract fun onLocalCategoryChange(categories: List<String>)
}
