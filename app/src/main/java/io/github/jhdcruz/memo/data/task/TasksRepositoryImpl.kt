package io.github.jhdcruz.memo.data.task

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import io.github.jhdcruz.memo.data.model.Task
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
            // save task to Firestore nested collection located in 'users/uid/tasks' and get id back
            val taskId = firestore.collection("users").document(userUid).collection("tasks")
                .add(task)
                .await()
                .id

            FirestoreResponseUseCase.Success(taskId)
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

    override suspend fun onCategoryAdd(category: String): FirestoreResponseUseCase {
        val userUid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            val originalCategories: List<String> = onGetCategories()
            val updatedCategories = originalCategories.plus(category)

            // append category to 'users/uid/tasksMetadata/labels' in categories field
            firestore.collection("users").document(userUid).collection("tasksMetadata")
                .document("labels")
                .update("categories", updatedCategories)
                .await()

            FirestoreResponseUseCase.Success("New category added!")
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error adding new category", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

    override suspend fun onCategoryUpdate(
        category: String,
        newCategory: String,
    ): FirestoreResponseUseCase {
        val userUid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            val originalCategories: List<String> = onGetCategories()

            // replace matching categories with newCategory
            val updatedCategories =
                originalCategories.map { if (it == category) newCategory else it }

            // update category in 'users/uid/tasksMetadata/labels' in categories field
            firestore.collection("users").document(userUid).collection("tasksMetadata")
                .document("labels")
                .update("categories", updatedCategories)
                .await()

            FirestoreResponseUseCase.Success("Category updated!")
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error updating category", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

    override suspend fun onCategoriesDelete(categories: List<String>): FirestoreResponseUseCase {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            val originalCategories: List<String> = onGetCategories()
            val updatedCategories = originalCategories.minus(categories.toSet())

            // delete categories from Firestore nested collection located in 'users/uid/tasksMetadata/labels'
            firestore.collection("users").document(uid).collection("tasksMetadata")
                .document("labels")
                .update("categories", updatedCategories)
                .await()

            FirestoreResponseUseCase.Success("Categories deleted!")
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error deleting categories", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

    override suspend fun onTagAdd(tag: String): FirestoreResponseUseCase {
        val userUid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            val originalTags: List<String> = onGetTags()

            if (originalTags.isEmpty()) {
                firestore.collection("users").document(userUid).collection("tasksMetadata")
                    .document("labels")
                    .set(mapOf("tags" to listOf(tag)), SetOptions.merge())
                    .await()
            } else {
                val updatedTags = originalTags.plus(tag)
                firestore.collection("users").document(userUid).collection("tasksMetadata")
                    .document("labels")
                    .update("tags", updatedTags)
                    .await()
            }

            FirestoreResponseUseCase.Success("New tag added!")
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error adding new tag", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

    override suspend fun onTagUpdate(tag: String, newTag: String): FirestoreResponseUseCase {
        val userUid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            val originalTags: List<String> = onGetTags()

            // replace matching tags with newTag
            val updatedTags = originalTags.map { if (it == tag) newTag else it }

            // update tag in 'users/uid/tasksMetadata/labels' in tags field
            firestore.collection("users").document(userUid).collection("tasksMetadata")
                .document("labels")
                .update("tags", updatedTags)
                .await()

            FirestoreResponseUseCase.Success("Tag updated!")
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error updating tag", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

    override suspend fun onTagsDelete(tags: List<String>): FirestoreResponseUseCase {
        TODO("Not yet implemented")
    }

    override suspend fun onGetCategories(): List<String> {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            // get categories from users/uid/tasksMetadata/labels stored in an array in categories field
            firestore.collection("users").document(uid).collection("tasksMetadata")
                .document("labels")
                .get()
                .await()
                .data
                ?.get("categories") as? List<String>
                ?: emptyList()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TasksRepository", "Error querying categories", e)
            emptyList()
        }
    }

    override suspend fun onGetTags(): List<String> {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            // get tags from users/uid/tasksMetadata/labels stored in an array in tags field
            firestore.collection("users").document(uid).collection("tasksMetadata")
                .document("labels")
                .get()
                .await()
                .data
                ?.get("tags") as? List<String>
                ?: emptyList()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TasksRepository", "Error querying tags", e)
            emptyList()
        }
    }
}
