package io.github.jhdcruz.memo.ui.screens.container

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jhdcruz.memo.data.model.Task
import io.github.jhdcruz.memo.data.task.AttachmentsRepository
import io.github.jhdcruz.memo.data.task.TasksRepository
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase
import io.github.jhdcruz.memo.service.reminders.ReminderSyncService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ContainerViewModelImpl
    @Inject
    constructor(
        private val tasksRepository: TasksRepository,
        private val attachmentsRepository: AttachmentsRepository,
    ) : ContainerViewModel() {
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

        init {
            viewModelScope.launch {
                onGetTasks()
            }
        }

        override fun restartReminderService(context: Context) {
            Intent(context, ReminderSyncService::class.java).apply {
                context.startService(this)
            }
        }

        override fun onVoiceSearch(): Intent {
            return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).also {
                it.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH,
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
                onIsFetchingTasksChange(true)

                val taskJob = tasksRepository.onTaskAdd(task) as FirestoreResponseUseCase.Success
                val taskId = taskJob.result as String

                // upload local files
                if (localAttachments.isNotEmpty()) {
                    onAttachmentsUpload(taskId, localAttachments)
                }

                onGetTasks()
            }
        }

        override fun onTaskUpdate(
            id: String,
            task: Task,
            localAttachments: List<Pair<String, Uri>>,
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                onIsFetchingTasksChange(true)

                val taskUpdateJob = async { tasksRepository.onTaskUpdate(id, task) }
                val attachmentsUploadJob =
                    async {
                        if (localAttachments.isNotEmpty()) {
                            onAttachmentsUpload(id, localAttachments)
                        }
                    }

                taskUpdateJob.await()
                attachmentsUploadJob.await()

                onGetTasks()
            }
        }

        override fun onTaskDelete(id: String) {
            viewModelScope.launch(Dispatchers.IO) {
                onIsFetchingTasksChange(true)

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
                _taskList.value =
                    _taskList.value.toMutableList().apply {
                        val index = indexOfFirst { it.id == id }
                        set(index, get(index).copy(isCompleted = true))
                    }

                // remove from the list
                _taskList.value =
                    _taskList.value.toMutableList().apply {
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

        override fun onCategoryUpdate(
            category: String,
            newCategory: String,
        ) {
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

        override fun onTagUpdate(
            tag: String,
            newTag: String,
        ) {
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

        override fun onFilterCategory(category: String) {
            viewModelScope.launch(Dispatchers.IO) {
                onIsFetchingTasksChange(true)

                val taskList = tasksRepository.onFilterCategory(category)
                onTaskListChange(taskList)

                onIsFetchingTasksChange(false)
            }
        }

        override fun onFilterTag(tag: String) {
            viewModelScope.launch(Dispatchers.IO) {
                onIsFetchingTasksChange(true)

                val taskList = tasksRepository.onFilterTag(tag)
                onTaskListChange(taskList)

                onIsFetchingTasksChange(false)
            }
        }

    override fun onFilterInbox() {
        viewModelScope.launch(Dispatchers.IO) {
            onIsFetchingTasksChange(true)

            val taskList = tasksRepository.onFiterInbox()
            onTaskListChange(taskList)

            onIsFetchingTasksChange(false)
        }
    }

    override fun onFilterDueWeek() {
        viewModelScope.launch(Dispatchers.IO) {
            onIsFetchingTasksChange(true)

            val taskList = tasksRepository.onFilterDueWeek()
            onTaskListChange(taskList)

            onIsFetchingTasksChange(false)
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

        override fun onLocalCategoryChange(categories: List<String>) {
            _categories.value = categories
        }
    }
