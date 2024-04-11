package io.github.jhdcruz.memo.data.task

import android.net.Uri
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase

interface AttachmentsRepository {

    suspend fun onAttachmentsUpload(
        id: String,
        attachments: List<Pair<String, Uri>>,
    ): FirestoreResponseUseCase

    suspend fun onAttachmentDelete(
        id: String,
        filename: String,
    ): FirestoreResponseUseCase

    suspend fun onAttachmentDeleteAll(
        id: String,
    ): FirestoreResponseUseCase

    suspend fun onAttachmentDownload(
        path: String,
    ): FirestoreResponseUseCase

}
