package io.github.jhdcruz.memo.ui.tasks.details

import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel

@Composable
fun AttachmentsList(tasksViewModel: TasksViewModel, attachments: List<Uri>?) {
    val context = LocalContext.current
    val fileProvider = context.contentResolver
    val selectedAttachments = remember { mutableStateListOf<Pair<Int, String>>() }

    // Collect the display names of the attachments first
    attachments?.mapIndexedNotNull { index, attachment ->
        fileProvider.query(
            attachment,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                // append to selectedAttachments
                selectedAttachments.add(index, Pair(index, cursor.getString(nameIndex)))
            } else {
                null
            }
        }
    } ?: emptyList()

    LazyRow {
        // first = index, second = file name
        selectedAttachments.forEachIndexed { _, attachment ->
            item {
                InputChip(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 2.dp)
                        .width(140.dp)
                        .height(32.dp),
                    selected = false,
                    label = {
                        // add ellipsis on text when going over max width
                        Text(
                            maxLines = 1,
                            text = attachment.second
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                // remove selected attachment
                                tasksViewModel.removeTaskAttachment(attachment.first)

                                // remove the attachment from attachmentNames
                                selectedAttachments.remove(attachment)
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
