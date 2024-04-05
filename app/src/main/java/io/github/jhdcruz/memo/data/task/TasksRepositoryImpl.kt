package io.github.jhdcruz.memo.data.task

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : TasksRepository {

    override suspend fun onSearch(query: String): List<Task> {
        val userUid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            // search tasks in Firestore nested collection located in 'users/uid/tasks'
            val result = firestore.collection("users").document(userUid).collection("tasks")
                .whereEqualTo("title", query)
                .get()
                .await()
                .toObjects(Task::class.java)

            Log.i("TasksRepository", "Tasks search yields ${result.size} results")
            return result
        } catch (e: FirebaseFirestoreException) {
            Log.i("TasksRepository", "Tasks search yields no results", e)
            emptyList()
        }
    }

    override suspend fun onTaskAdd(task: Task): FirestoreResponseUseCase {
        val userUid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            // save task to Firestore nested collection located in 'users/uid/tasks'
            firestore.collection("users").document(userUid).collection("tasks")
                .add(task)
                .await()

            FirestoreResponseUseCase.Success("New task added!")
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error adding new task", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

    override suspend fun onTaskUpdate(uid: String, task: Task): FirestoreResponseUseCase {
        val userUid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            // update task in Firestore nested collection located in 'users/uid/tasks'
            firestore.collection("users").document(userUid).collection("tasks")
                .document(uid)
                .set(task, SetOptions.merge())
                .await()

            FirestoreResponseUseCase.Success("Task updated!")
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error updating task", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

    override suspend fun onTaskDelete(uid: String): FirestoreResponseUseCase {
        val userUid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            // delete task in Firestore nested collection located in 'users/uid/tasks'
            firestore.collection("users").document(userUid).collection("tasks")
                .document(uid)
                .delete()
                .await()

            FirestoreResponseUseCase.Success("Task deleted!")
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error deleting task $uid", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

    override suspend fun onTaskCompleted(uid: String): FirestoreResponseUseCase {
        val userUid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            // update task in Firestore nested collection located in 'users/uid/tasks'
            firestore.collection("users").document(userUid).collection("tasks")
                .document(uid)
                .update("isCompleted", true)
                .await()

            FirestoreResponseUseCase.Success("Task completed!")
        } catch (e: Exception) {
            FirestoreResponseUseCase.Error(e)
        }
    }
}
