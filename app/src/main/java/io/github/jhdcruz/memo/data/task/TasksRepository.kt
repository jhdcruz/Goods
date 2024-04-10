package io.github.jhdcruz.memo.data.task

import io.github.jhdcruz.memo.data.model.Task
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase

interface TasksRepository {
    suspend fun onGetTasks(): List<Task>
    suspend fun onGetTask(id: String): Task

    suspend fun onSearch(query: String): List<Task>

    suspend fun onTaskAdd(task: Task): FirestoreResponseUseCase
    suspend fun onTaskUpdate(id: String, task: Task): FirestoreResponseUseCase
    suspend fun onTaskDelete(id: String): FirestoreResponseUseCase
    suspend fun onTaskCompleted(id: String): FirestoreResponseUseCase

    suspend fun onCategoryAdd(category: String): FirestoreResponseUseCase
    suspend fun onCategoryUpdate(category: String, newCategory: String): FirestoreResponseUseCase
    suspend fun onCategoriesDelete(categories: List<String>): FirestoreResponseUseCase

    suspend fun onTagAdd(tag: String): FirestoreResponseUseCase
    suspend fun onTagUpdate(tag: String, newTag: String): FirestoreResponseUseCase
    suspend fun onTagsDelete(tags: List<String>): FirestoreResponseUseCase

    suspend fun onGetCategories(): List<String>
    suspend fun onGetTags(): List<String>
}
