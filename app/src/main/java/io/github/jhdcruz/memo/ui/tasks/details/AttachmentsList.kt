package io.github.jhdcruz.memo.ui.tasks.details

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import kotlinx.coroutines.launch

@Composable
fun AttachmentsList(tasksViewModel: TasksViewModel, localFiles: List<Uri>?) {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val fileProvider = context.contentResolver

    val selectedAttachments = remember { mutableStateListOf<Pair<String, Uri>>() }

    LaunchedEffect(selectedAttachments) {
        tasksViewModel.onTaskLocalAttachmentsChange(selectedAttachments)
    }

    // Collect the display names of the local files
    localFiles?.mapNotNull { attachment ->
        fileProvider.query(
            attachment,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                // append to selectedAttachments
                selectedAttachments.add(Pair(cursor.getString(nameIndex), attachment))
            } else {
                null
            }
        }
    } ?: emptyList()

    LazyRow {
        // for local files to be uploaded
        items(selectedAttachments) { attachment ->
            InputChip(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .width(140.dp)
                    .height(32.dp),
                selected = false,
                label = {
                    // add ellipsis on text when going over max width
                    Text(
                        text = attachment.first,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { selectedAttachments.remove(attachment) }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Remove attachment"
                        )
                    }
                },
                onClick = {
                    // Preview file
                    scope.launch {
                        val intent = Intent(Intent.ACTION_VIEW)

                        intent.setDataAndType(
                            attachment.second,
                            fileProvider.getType(attachment.second)
                        ).apply {
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }

                        context.startActivity(intent)
                    }
                },
            )
        }
    }
}
