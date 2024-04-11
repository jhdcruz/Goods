@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.jhdcruz.memo.ui.tasks.bottomsheet

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.data.model.Task
import io.github.jhdcruz.memo.domain.format
import io.github.jhdcruz.memo.ui.shared.ConfirmDialog
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelImpl
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelPreview
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import kotlinx.coroutines.launch

/**
 * Provide a [task] for viewing existing task data
 */
@Composable
fun TaskDetailsSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    tasksViewModel: TasksViewModel = hiltViewModel<TasksViewModelImpl>(),
    task: Task? = null,
) {
    ModalBottomSheet(
        modifier = modifier
            .wrapContentHeight()
            .imePadding(),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { }
    ) {
        // assign existing task data to view model
        if (task != null) {
            tasksViewModel.onTaskPreview(task)
        }

        TaskDetailsContent(tasksViewModel, sheetState, task)
    }
}

@Composable
private fun TaskDetailsContent(
    tasksViewModel: TasksViewModel,
    sheetState: SheetState,
    task: Task? = null,
) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Populating tasks
    val taskId = tasksViewModel.taskId.collectAsState(null)
    val taskTitle = tasksViewModel.taskTitle.collectAsState("")
    val taskDescription = tasksViewModel.taskDescription.collectAsState(TextFieldValue(""))
    val taskCategory = tasksViewModel.taskCategory.collectAsState("")
    val taskTags = tasksViewModel.taskTags.collectAsState(emptyList())
    val taskPriority = tasksViewModel.taskPriority.collectAsState(0)

    val taskLocalAttachments =
        tasksViewModel.taskLocalAttachments.collectAsState(emptyList())
    val taskDueDate = tasksViewModel.taskDueDate.collectAsState(null)

    val fileUris = remember { mutableStateOf(emptyList<Uri>()) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val selectTaskAttachments =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri>? ->
            if (uris != null) {
                // append selected files, instead of overwriting
                fileUris.value = uris
            }
        }

    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .imePadding()
            .wrapContentHeight(),
    ) {
        if (showDeleteDialog) {
            ConfirmDialog(
                onDismissRequest = { showDeleteDialog = false },
                onConfirmation = {
                    scope.launch {
                        tasksViewModel.onTaskDelete(taskId.value!!)
                    }
                },
                dialogTitle = "Delete task?",
                dialogText = "This actions is permanent, this task cannot be recovered.",
                icon = Icons.Outlined.Warning,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        showDeleteDialog = true
                    }
                }
            ) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error),
                    painter = painterResource(id = R.drawable.baseline_delete_24),
                    contentDescription = "Delete task"
                )
            }

            if (taskDueDate.value != null) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = { showDatePicker = true }
                ) {
                    Text(text = taskDueDate.value!!.format())
                }
            } else {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            // submit button
            IconButton(
                enabled = taskTitle.value.isNotEmpty(),
                onClick = {
                    scope.launch {
                        // hide bottom sheet to avoid blocking UI
                        keyboardController?.hide()
                        sheetState.hide()

                        if (task != null) {
                            // update task
                            tasksViewModel.onTaskUpdate(
                                taskId.value!!,
                                Task(
                                    id = taskId.value,
                                    priority = taskPriority.value,
                                    dueDate = taskDueDate.value,
                                    title = taskTitle.value,
                                    description = taskDescription.value.text,
                                    category = taskCategory.value,
                                    tags = taskTags.value,
                                    created = task.created,
                                    updated = Timestamp.now()
                                ),
                                taskLocalAttachments.value
                            )
                        } else {
                            // add new task
                            tasksViewModel.onTaskAdd(
                                Task(
                                    priority = taskPriority.value,
                                    dueDate = taskDueDate.value,
                                    title = taskTitle.value,
                                    description = taskDescription.value.text,
                                    category = taskCategory.value,
                                    tags = taskTags.value,
                                ),
                                taskLocalAttachments.value
                            )
                        }

                        // reset selected files
                        fileUris.value = emptyList()
                    }
                }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_done_24),
                    contentDescription = "Save task",
                    colorFilter = if (taskTitle.value.isNotEmpty()) {
                        ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    } else {
                        ColorFilter.tint(Color.Gray)
                    },
                )
            }
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = taskTitle.value,
            onValueChange = { tasksViewModel.onTaskTitleChange(it) },
            singleLine = true,
            placeholder = { Text(text = "Headline of your task") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )

        HorizontalDivider()

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = taskDescription.value,
            onValueChange = { tasksViewModel.onTaskDescriptionChange(it) },
            minLines = 10,
            maxLines = 30,
            placeholder = { Text(text = "Elaborate the details of your task") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )

        // list of attachments uploaded
        AttachmentsList(tasksViewModel = tasksViewModel, localFiles = fileUris.value)

        HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp))

        Row(
            modifier = Modifier.padding(bottom = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PriorityButton(
                taskPriority = taskPriority.value,
                tasksViewModel = tasksViewModel
            )

            CategoryButton(viewModel = tasksViewModel)

            DueDatePicker(
                tasksViewModel = tasksViewModel,
                showPicker = showDatePicker
            )

            IconButton(
                onClick = {
                    scope.launch {
                        selectTaskAttachments.launch(arrayOf("*/*"))
                    }
                }) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_attach_24),
                    contentDescription = "Upload attachments"
                )
            }

            // space-between
            Spacer(modifier = Modifier.weight(1F))

            TagsButton(viewModel = tasksViewModel)
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun TaskDetailsContentPreview() {
    val previewViewModel = TasksViewModelPreview()
    val sheetState = rememberModalBottomSheetState()

    MemoTheme {
        TaskDetailsContent(previewViewModel, sheetState)
    }
}
