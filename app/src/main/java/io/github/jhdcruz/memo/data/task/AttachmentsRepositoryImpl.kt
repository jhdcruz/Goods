package io.github.jhdcruz.memo.data.task

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import io.github.jhdcruz.memo.domain.generateHash
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AttachmentsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
) : AttachmentsRepository {

    override suspend fun onAttachmentsUpload(
        uid: String,
        attachments: List<Uri>,
    ): FirestoreResponseUseCase {
        val userUid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            val attachmentUrls = mutableListOf(mapOf<String, String>())

            // upload attachments to Firestore storage
            attachments.forEach { attachment ->
                storage.reference.child(
                    // store attachments with added hash to prevent file collisions
                    "$userUid/attachments/${attachment.lastPathSegment}-${
                        generateHash(
                            8
                        )
                    }"
                )
                    .putFile(attachment)
                    .addOnSuccessListener {
                        // store both storage path and download url
                        attachmentUrls.add(
                            mapOf(
                                "path" to it.storage.path,
                                "downloadUrl" to it.storage.downloadUrl.toString()
                            )
                        )
                    }
                    .await()
            }

            // store url to firestore
            firestore.collection("users").document(userUid).collection("tasks")
                .document(uid)
                .update("attachments", listOf(attachmentUrls))
                .await()

            FirestoreResponseUseCase.Success("Attachments uploaded!")
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error uploading attachments", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

    override suspend fun onAttachmentDelete(
        uid: String,
        path: String,
    ): FirestoreResponseUseCase {
        auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            // delete attachment from Firestore storage
            storage.reference.child(path)
                .delete()
                .await()

            FirestoreResponseUseCase.Success("Attachment deleted!")
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error deleting attachment", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

    override suspend fun onAttachmentDownload(
        uid: String,
        path: String,
    ): FirestoreResponseUseCase {
        auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

        return try {
            // download attachment from Firestore storage
            val attachment = storage.reference.child(path)
                .downloadUrl
                .await()

            FirestoreResponseUseCase.Success(attachment)
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error downloading attachment", e)
            FirestoreResponseUseCase.Error(e)
        }
    }

}
