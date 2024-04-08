@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.jhdcruz.memo.ui.tasks.components

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.data.task.Task
import io.github.jhdcruz.memo.domain.createTimestamp
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelImpl
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelPreview
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import kotlinx.coroutines.launch

@Composable
fun TaskAdd(
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
        TaskAddContent(tasksViewModel)
    }
}

@Composable
private fun TaskAddContent(tasksViewModel: TasksViewModel) {
    val scope = rememberCoroutineScope()

    // Populating tasks
    val taskTitle = tasksViewModel.taskTitle.collectAsState(initial = "")
    val taskDescription = tasksViewModel.taskDescription.collectAsState(initial = "")
    val taskCategory = tasksViewModel.taskCategory.collectAsState(initial = "")
    val taskTags = tasksViewModel.taskTags.collectAsState(initial = emptyList())
    val taskAttachments = tasksViewModel.taskAttachments.collectAsState(initial = emptyList())
    val taskPriority = tasksViewModel.taskPriority.collectAsState(initial = 0)

    val taskSelectedDate = tasksViewModel.taskSelectedDate.collectAsState(initial = null)
    val taskSelectedHour = tasksViewModel.taskSelectedHour.collectAsState(initial = null)
    val taskSelectedMinute = tasksViewModel.taskSelectedMinute.collectAsState(initial = null)

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

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

            IconButton(
                onClick = {
                    scope.launch {
                        // get due date from pickers else return null
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

                        tasksViewModel.onTaskAdd(
                            Task(
                                title = taskTitle.value,
                                description = taskDescription.value,
                                category = taskCategory.value,
                                tags = taskTags.value,
                                attachments = taskAttachments.value,
                                dueDate = dueDate,
                                priority = taskPriority.value
                            )
                        )
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

        HorizontalDivider()

        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                }) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_flag_24),
                    contentDescription = "Set task's priority"
                )
            }

            IconButton(
                onClick = {
                }) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_folder_24),
                    contentDescription = "Set task's category"
                )
            }

            IconButton(
                onClick = {
                    showDatePicker = true
                }) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_calendar_24),
                    contentDescription = "Set task's due date"
                )
            }

            IconButton(
                onClick = {
                }) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_attach_24),
                    contentDescription = "Upload attachments"
                )
            }

            // space-between
            Spacer(modifier = Modifier.weight(1F))

            IconButton(
                onClick = {
                }) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_label_24),
                    contentDescription = "Set task's label"
                )
            }


            // Modal Dialogs
            if (showDatePicker) {
                TaskDatePickerDialog(
                    tasksViewModel = tasksViewModel,
                    onDismissRequest = {
                        showDatePicker = false
                    },
                    onConfirmRequest = {
                        showDatePicker = false
                        showTimePicker = true
                    }
                )
            }

            if (showTimePicker) {
                TaskTimePickerDialog(
                    tasksViewModel = tasksViewModel,
                    onDismissRequest = {
                        showTimePicker = false
                    }
                )
            }
        } // Row
    }
}

@Composable
@Preview(showBackground = true)
private fun TaskAddPreview() {
    val previewViewModel = TasksViewModelPreview()

    MemoTheme {
        TaskAddContent(previewViewModel)
    }
}
