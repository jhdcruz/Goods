@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.jhdcruz.memo.ui.screens.tasks.detailsheet

import android.text.format.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.domain.createTimestamp
import io.github.jhdcruz.memo.ui.components.TimePickerDialog
import io.github.jhdcruz.memo.ui.screens.tasks.TasksViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun DueDatePicker(
    tasksViewModel: TasksViewModel,
    showPicker: Boolean = false,
) {
    var showDatePicker by remember { mutableStateOf(showPicker) }
    var showTimePicker by remember { mutableStateOf(false) }

    val taskDueDate = tasksViewModel.taskDueDate.collectAsState(null)

    val buttonIcon = if (taskDueDate.value == null) {
        R.drawable.baseline_calendar_24
    } else {
        R.drawable.baseline_calendar_filled_24
    }

    IconButton(
        onClick = {
            showDatePicker = true
        }) {
        Image(
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            painter = painterResource(id = buttonIcon),
            contentDescription = "Set task's due date"
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
}

/**
 * Modal dialog that shows a date picker
 * to be assigned at the tasks view model instance passed.
 */
@Composable
fun TaskDatePickerDialog(
    modifier: Modifier = Modifier,
    tasksViewModel: TasksViewModel,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val taskDueDate = tasksViewModel.taskDueDate.collectAsState(null)
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = taskDueDate.value?.toDate()?.time ?: System.currentTimeMillis()
    )

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    val date = datePickerState.selectedDateMillis

                    if (date != null) {
                        tasksViewModel.onTaskSelectedDateChange(date)
                        tasksViewModel.onTaskDueDateChange(
                            createTimestamp(date)
                        )
                    }

                    onConfirmRequest()
                }
            }) {
                Text(text = "Confirm")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
        )
    }
}

/**
 * Modal dialog that shows a time picker
 * to be assigned at the tasks view model instance passed.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TaskTimePickerDialog(
    modifier: Modifier = Modifier,
    tasksViewModel: TasksViewModel,
    onDismissRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext

    val taskSelectedDate = tasksViewModel.taskSelectedDate.collectAsState(null)
    val taskDueDate = tasksViewModel.taskDueDate.collectAsState(null)

    val taskHours = Calendar.getInstance().apply {
        timeInMillis = taskDueDate.value?.toDate()?.time ?: System.currentTimeMillis()
    }.get(Calendar.HOUR_OF_DAY)

    val taskMinutes = Calendar.getInstance().apply {
        timeInMillis = taskDueDate.value?.toDate()?.time ?: System.currentTimeMillis()
    }.get(Calendar.MINUTE)

    val is24HourFormat = DateFormat.is24HourFormat(context)
    val timeState = rememberTimePickerState(
        initialHour = taskHours,
        initialMinute = taskMinutes,
        is24Hour = is24HourFormat
    )

    TimePickerDialog(
        modifier = modifier,
        timeState = timeState,
        onCancel = {
            onDismissRequest()
        },
        onConfirm = { timePickerState ->
            scope.launch {
                val hour = timePickerState.hour
                val minute = timePickerState.minute

                tasksViewModel.onTaskSelectedHourChange(hour)
                tasksViewModel.onTaskSelectedMinuteChange(minute)

                tasksViewModel.onTaskDueDateChange(
                    createTimestamp(
                        taskSelectedDate.value!!,
                        hour,
                        minute
                    )
                )

                onDismissRequest()
            }
        }
    )
}
