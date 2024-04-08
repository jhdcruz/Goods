package io.github.jhdcruz.memo.ui.tasks.details

import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel

@Composable
fun AttachmentsList(tasksViewModel: TasksViewModel, attachments: List<Uri>?) {
    val context = LocalContext.current
    val fileProvider = context.contentResolver

    // Collect the display names of the attachments first
    val attachmentNames = attachments?.mapIndexedNotNull { index, attachment ->
        fileProvider.query(
            attachment,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                Pair(index, cursor.getString(nameIndex))
            } else {
                null
            }
        }
    } ?: emptyList()

    LazyRow {
        // first = index, second = file name
        attachmentNames.forEachIndexed { index, attachment ->
            item {
                InputChip(
                    modifier = Modifier
                        .width(120.dp)
                        .height(32.dp),
                    selected = false,
                    label = {
                        Text(text = attachment.second)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                // remove selected attachment
                                tasksViewModel.removeTaskAttachment(attachment.first)

                                // remove the attachment from attachmentNames
                                attachmentNames.toMutableList().apply {
                                    removeAt(index)
                                }
                            }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Remove attachment"
                            )
                        }
                    },
                    onClick = {
                        // TODO: Download file
                    },
                )
            }
        }
    }
}
