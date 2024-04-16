package io.github.jhdcruz.memo.data.task

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import io.github.jhdcruz.memo.data.model.TaskAttachment
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AttachmentsRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : AttachmentsRepository {

    override suspend fun onAttachmentsUpload(
        id: String,
        attachments: List<Pair<String, Uri>>,
    ): FirestoreResponseUseCase {
        val userUid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        Log.i("TasksRepository", attachments.toString())

        return try {
            // upload attachments to Firestore storage
            attachments.forEachIndexed { index, attachment ->
                val uploadTask = storage.reference.child(userUid)
                    .child("attachments")
                    .child(id)
                    .child(attachment.first)
                    .putFile(attachment.second)
                    .await()

                val downloadUrl = uploadTask.storage.downloadUrl.await()

                // store both storage path and download url
                val attachmentRef = mapOf(
                    "name" to attachment.first,
                    "path" to uploadTask.storage.path,
                    "downloadUrl" to downloadUrl.toString()
                )

                // store each file reference to firestore with index as id
                firestore.collection("users").document(userUid)
                    .collection("tasks").document(id)
                    .update("attachments", mapOf(index.toString() to attachmentRef))
                    .await()
            }

            FirestoreResponseUseCase.Success("Attachments uploaded!")
        } catch (e: StorageException) {
            Log.e("TasksRepository", "Error uploading attachments", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun onAttachmentDelete(
        id: String,
        filename: String,
    ): FirestoreResponseUseCase {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            // delete attachment from Firestore storage
            storage.reference.child(uid)
                .child("attachments")
                .child(id)
                .child(filename)
                .delete()
                .await()

            // get the current attachments map
            val taskDocRef = firestore.collection("users").document(uid)
                .collection("tasks").document(id)

            val taskSnapshot = taskDocRef.get().await()
            val attachmentsMap =
                taskSnapshot.get("attachments") as MutableMap<Int, TaskAttachment>

            // find the key of the map entry with the matching filename
            val keyToRemove = attachmentsMap.entries.find { it.value.name == filename }?.key

            // remove the entry with the matching filename
            if (keyToRemove != null) {
                attachmentsMap.remove(keyToRemove)

                // update the attachments map in firestore
                taskDocRef.update("attachments", attachmentsMap).await()
            }

            FirestoreResponseUseCase.Success("Attachment deleted!")
        } catch (e: StorageException) {
            Log.e("TasksRepository", "Error deleting attachment", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

    override suspend fun onAttachmentDeleteAll(
        id: String,
    ): FirestoreResponseUseCase {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            // delete attachment from Firestore storage
            storage.reference.child(uid)
                .child("attachments")
                .child(id)
                .delete()
                .await()

            FirestoreResponseUseCase.Success("Attachment deleted!")
        } catch (e: StorageException) {
            Log.e("TasksRepository", "Error deleting attachment", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

    override suspend fun onAttachmentDownload(path: String): FirestoreResponseUseCase {
        auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            // download attachment from Firestore storage
            val attachment = storage.reference.child(path)
                .downloadUrl
                .await()

            FirestoreResponseUseCase.Success(attachment)
        } catch (e: StorageException) {
            Log.e("TasksRepository", "Error downloading attachment", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

}
