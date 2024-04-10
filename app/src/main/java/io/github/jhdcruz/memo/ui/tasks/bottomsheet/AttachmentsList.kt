package io.github.jhdcruz.memo.ui.tasks.bottomsheet

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import kotlinx.coroutines.launch

@Composable
fun AttachmentsList(
    tasksViewModel: TasksViewModel,
    localFiles: List<Uri>?,
) {
    val scope = rememberCoroutineScope()

    val appContext = LocalContext.current.applicationContext
    val contentResolver = appContext.contentResolver

    // for existing tasks
    val taskId = tasksViewModel.taskId.collectAsState(null)
    val taskAttachments = tasksViewModel.taskAttachments.collectAsState(null)

    val selectedAttachments = remember { mutableStateListOf<Pair<String, Uri>>() }
    val taskLocalAttachments =
        tasksViewModel.taskLocalAttachments.collectAsState(selectedAttachments)

    // Collect the display names of the local files
    localFiles?.mapNotNull { file ->
        contentResolver.query(
            file,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null
        )?.use { cursor ->
            // get the file name
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (cursor.moveToFirst()) {
                val attachment = Pair(
                    cursor.getString(nameIndex),
                    file
                )

                if (attachment !in selectedAttachments) {
                    selectedAttachments.add(attachment)
                    tasksViewModel.onTaskLocalAttachmentsChange(selectedAttachments)
                }
            }
        }
    } ?: emptyList()

    LazyRow {
        // for local files to be uploaded
        items(taskLocalAttachments.value) { attachment ->
            InputChip(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .width(140.dp)
                    .height(32.dp),
                selected = false,
                label = {
                    // add ellipsis on text on overflow
                    Text(
                        text = attachment.first,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                avatar = {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_cloud_upload_24),
                        contentDescription = "File yet to be uploaded",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            selectedAttachments.remove(attachment)
                            tasksViewModel.onTaskLocalAttachmentsChange(selectedAttachments)
                        }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Remove attachment"
                        )
                    }
                },
                onClick = {
                    // Preview file
                    scope.launch {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(
                                attachment.second,
                                contentResolver.getType(attachment.second)
                            )
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        appContext.startActivity(intent)
                    }
                },
            )
        }

        if (taskAttachments.value?.entries?.isNotEmpty() == true) {
            items(taskAttachments.value!!.entries.toList()) { attachment ->
                val file = attachment.value

                InputChip(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .width(140.dp)
                        .height(32.dp),
                    selected = false,
                    label = {
                        // add ellipsis on text on overflow
                        Text(
                            text = file.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    tasksViewModel.removeTaskAttachment(
                                        taskId = taskId.value!!,
                                        filename = file.name,
                                        originalAttachments = taskAttachments.value!!
                                    )
                                }
                            }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Remove attachment"
                            )
                        }
                    },
                    onClick = {
                        scope.launch {
                            // Open the URL link
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(file.downloadUrl)
                            }
                            appContext.startActivity(intent)
                        }
                    },
                )
            }
        }
    }
}
