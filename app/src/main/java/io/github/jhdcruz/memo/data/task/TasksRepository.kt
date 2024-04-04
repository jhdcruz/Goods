package io.github.jhdcruz.memo.data.task

import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase

interface TasksRepository {
    suspend fun onSearch(query: String): List<Task>

    suspend fun onTaskAdd(task: Task): FirestoreResponseUseCase
    suspend fun onTaskUpdate(uid: String, task: Task): FirestoreResponseUseCase
    suspend fun onTaskDelete(uid: String): FirestoreResponseUseCase
    suspend fun onTaskCompleted(uid: String): FirestoreResponseUseCase
}
