package io.github.jhdcruz.memo.ui.tasks.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel

@Composable
fun PriorityButton(
    taskPriority: Int,
    tasksViewModel: TasksViewModel,
) {
    var showPriorityDropdown by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { showPriorityDropdown = true }
        ) {
            Image(
                colorFilter = ColorFilter.tint(
                    when (taskPriority) {
                        3 -> Color.Red
                        2 -> Color.Yellow
                        1 -> Color.Blue
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                ),
                painter = painterResource(
                    id = if (taskPriority != 0) {
                        R.drawable.baseline_flag_filled_24
                    } else {
                        R.drawable.baseline_flag_24
                    }
                ),
                contentDescription = "Set task's priority"
            )
        }

        DropdownMenu(
            expanded = showPriorityDropdown,
            onDismissRequest = { showPriorityDropdown = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = "High Priority") },
                leadingIcon = {
                    Image(
                        colorFilter = ColorFilter.tint(Color.Red),
                        painter = painterResource(id = R.drawable.baseline_flag_24),
                        contentDescription = null
                    )
                },
                onClick = {
                    tasksViewModel.onTaskPriorityChange(3)
                    showPriorityDropdown = false
                }
            )
            DropdownMenuItem(
                text = { Text(text = "Medium Priority") },
                leadingIcon = {
                    Image(
                        colorFilter = ColorFilter.tint(Color.Yellow),
                        painter = painterResource(id = R.drawable.baseline_flag_24),
                        contentDescription = null
                    )
                },
                onClick = {
                    tasksViewModel.onTaskPriorityChange(2)
                    showPriorityDropdown = false
                }
            )
            DropdownMenuItem(
                text = { Text(text = "Low Priority") },
                leadingIcon = {
                    Image(
                        colorFilter = ColorFilter.tint(Color.Blue),
                        painter = painterResource(id = R.drawable.baseline_flag_24),
                        contentDescription = null
                    )
                },
                onClick = {
                    tasksViewModel.onTaskPriorityChange(1)
                    showPriorityDropdown = false
                }
            )
            DropdownMenuItem(
                text = { Text(text = "No Priority") },
                leadingIcon = {
                    Image(
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        painter = painterResource(id = R.drawable.baseline_flag_24),
                        contentDescription = null
                    )
                },
                onClick = {
                    tasksViewModel.onTaskPriorityChange(0)
                    showPriorityDropdown = false
                }
            )
        }
    }
}
