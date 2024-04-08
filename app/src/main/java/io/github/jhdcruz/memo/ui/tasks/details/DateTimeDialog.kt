@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.jhdcruz.memo.ui.tasks.details

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.jhdcruz.memo.ui.shared.TimePickerDialog
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel

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
    val datePickerState = rememberDatePickerState()

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
                val date = datePickerState.selectedDateMillis

                if (date != null) {
                    tasksViewModel.onTaskSelectedDateChange(date)
                }

                onConfirmRequest()
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
    TimePickerDialog(
        modifier = modifier,
        onCancel = {
            onDismissRequest()
        },
        onConfirm = { timePickerState ->
            val hour = timePickerState.hour
            val minute = timePickerState.minute

            tasksViewModel.onTaskSelectedHourChange(hour)
            tasksViewModel.onTaskSelectedMinuteChange(minute)

            onDismissRequest()
        }
    )
}
