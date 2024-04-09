@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.jhdcruz.memo.ui.tasks.details

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.data.model.Task
import io.github.jhdcruz.memo.domain.createTimestamp
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelImpl
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelPreview
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import kotlinx.coroutines.launch

@Composable
fun TaskDetailsSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    tasksViewModel: TasksViewModel = hiltViewModel<TasksViewModelImpl>(),
) {
    ModalBottomSheet(
        modifier = modifier
            .wrapContentHeight()
            .imePadding(),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { }
    ) {
        TaskDetailsContent(tasksViewModel)
    }
}

@Composable
private fun TaskDetailsContent(tasksViewModel: TasksViewModel) {
    val scope = rememberCoroutineScope()

    // Populating tasks
    val taskTitle = tasksViewModel.taskTitle.collectAsState("")
    val taskDescription = tasksViewModel.taskDescription.collectAsState(TextFieldValue(""))
    val taskCategory = tasksViewModel.taskCategory.collectAsState("")
    val taskTags = tasksViewModel.taskTags.collectAsState(emptyList())
    val taskPriority = tasksViewModel.taskPriority.collectAsState(0)

    val taskAttachments = tasksViewModel.taskAttachments.collectAsState(emptyList())
    val taskLocalAttachments =
        tasksViewModel.taskLocalAttachments.collectAsState(emptyList())

    val taskSelectedDate = tasksViewModel.taskSelectedDate.collectAsState(null)
    val taskSelectedHour = tasksViewModel.taskSelectedHour.collectAsState(null)
    val taskSelectedMinute = tasksViewModel.taskSelectedMinute.collectAsState(null)

    var fileUris by remember { mutableStateOf(emptyList<Uri>()) }

    val selectTaskAttachments =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri>? ->
            if (uris != null) {
                // append selected files, instead of overwriting
                fileUris += uris
            }
        }

    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .imePadding()
            .wrapContentHeight(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1F),
                value = taskTitle.value,
                onValueChange = { tasksViewModel.onTaskTitleChange(it) },
                singleLine = true,
                placeholder = { Text(text = "Headline of your task") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            // submit button
            IconButton(
                onClick = {
                    scope.launch {
                        // get due date from pickers else return null
                        // means there is no due date set
                        val dueDate =
                            if (
                                taskSelectedDate.value != null &&
                                taskSelectedHour.value != null &&
                                taskSelectedMinute.value != null
                            ) {
                                // convert to firestore compatible timestamp
                                createTimestamp(
                                    taskSelectedDate.value!!,
                                    taskSelectedHour.value!!,
                                    taskSelectedMinute.value!!
                                )
                            } else {
                                null
                            }

                        // save to firestore
                        val id = tasksViewModel.onTaskAdd(
                            Task(
                                priority = taskPriority.value,
                                dueDate = dueDate,
                                title = taskTitle.value,
                                description = taskDescription.value.text,
                                category = taskCategory.value,
                                tags = taskTags.value,
                            )
                        ) as FirestoreResponseUseCase.Success

                        // upload attachments
                        if (fileUris.isNotEmpty()) {
                            tasksViewModel.onAttachmentsUpload(
                                id = id.result as String,
                                attachments = taskLocalAttachments.value
                            )
                        }
                    }
                }) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_done_24),
                    contentDescription = "Add task"
                )
            }
        }

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
        AttachmentsList(tasksViewModel = tasksViewModel, localFiles = fileUris)

        HorizontalDivider()

        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PriorityButton(
                taskPriority = taskPriority.value,
                tasksViewModel = tasksViewModel
            )

            CategoryButton(viewModel = tasksViewModel)

            DueDatePicker(
                tasksViewModel = tasksViewModel,
            )

            IconButton(
                onClick = {
                    selectTaskAttachments.launch(arrayOf("*/*"))
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
private fun TaskAddPreview() {
    val previewViewModel = TasksViewModelPreview()

    MemoTheme {
        TaskDetailsContent(previewViewModel)
    }
}
