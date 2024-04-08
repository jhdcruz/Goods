package io.github.jhdcruz.memo.data.task

import android.net.Uri
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase

interface AttachmentsRepository {

    suspend fun onAttachmentsUpload(
        uid: String,
        attachments: List<Uri>,
    ): FirestoreResponseUseCase

    suspend fun onAttachmentDelete(
        uid: String,
        path: String,
    ): FirestoreResponseUseCase

    suspend fun onAttachmentDownload(
        uid: String,
        path: String,
    ): FirestoreResponseUseCase

}
