@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.jhdcruz.memo.ui.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.data.model.Task
import io.github.jhdcruz.memo.domain.dateUntil
import io.github.jhdcruz.memo.ui.navigation.BottomNavigation
import io.github.jhdcruz.memo.ui.shared.AppSearch
import io.github.jhdcruz.memo.ui.tasks.bottomsheet.TaskDetailsSheet
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import kotlinx.coroutines.launch

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    tasksViewModel: TasksViewModel = hiltViewModel<TasksViewModelImpl>(),
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
        ) {
            TasksListContent(tasksViewModel = tasksViewModel)
        }
    }
}


@Composable
private fun TasksListContent(tasksViewModel: TasksViewModel) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    val isFetchingTasks = tasksViewModel.isFetchingTasks.collectAsState(true)
    val taskList = tasksViewModel.taskList.collectAsState(emptyList())

    var selectedTask by remember { mutableStateOf<Task?>(null) }

    LaunchedEffect(isFetchingTasks.value) {
        if (isFetchingTasks.value) {
            tasksViewModel.onGetTasks()
            sheetState.hide()
            selectedTask = null
        }
    }

    when {
        isFetchingTasks.value -> LoadingState(isFetchingTasks.value)
        !isFetchingTasks.value && taskList.value.isEmpty() -> EmptyState()
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                itemsIndexed(taskList.value) { _, task ->
                    TaskItem(
                        task = task,
                        onTaskClick = {
                            scope.launch {
                                selectedTask = it
                                sheetState.show()
                            }
                        },
                        onTaskCompleted = { tasksViewModel.onTaskCompleted(it.id!!) }
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp, horizontal = 16.dp)
                    )
                }
            }

            if (sheetState.isVisible) {
                TaskDetailsSheet(
                    tasksViewModel = tasksViewModel,
                    onDismissRequest = {},
                    sheetState = sheetState,
                    task = selectedTask!!
                )
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onTaskClick: (Task) -> Unit,
    onTaskCompleted: (Task) -> Unit,
) {
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        modifier = Modifier.wrapContentHeight(),
        visible = !task.isCompleted,
        enter = fadeIn(),
        exit = fadeOut(),
        label = "Task completion anim"
    ) {
        ListItem(
            modifier = Modifier.clickable { onTaskClick(task) },
            overlineContent = {
                Row(
                    modifier = Modifier.wrapContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(id = R.drawable.baseline_folder_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = if (task.category?.isNotBlank() == true) {
                            task.category
                        } else {
                            "Inbox"
                        }
                    )
                }
            },
            headlineContent = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.SemiBold,
                    text = task.title
                )
            },
            supportingContent = {
                if (task.description?.isNotBlank() == true) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = task.description,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            },
            leadingContent = {
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = {
                            scope.launch {
                                onTaskCompleted(task)
                            }
                        }
                    )
                }
            },
            trailingContent = {
                TaskTrailingContent(task)
            }
        )
    }
}

@Composable
private fun TaskTrailingContent(task: Task) {
    Box(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.End,
        ) {
            // due date & priority
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (task.dueDate != null) {
                    Text(
                        text = task.dueDate.dateUntil(),
                        maxLines = 1
                    )
                }

                Image(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    colorFilter = ColorFilter.tint(
                        when (task.priority) {
                            3 -> Color.Red
                            2 -> Color.Yellow
                            1 -> Color.Blue
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    ),
                    painter = painterResource(
                        id = if (task.priority != 0) {
                            R.drawable.baseline_flag_filled_24
                        } else {
                            R.drawable.baseline_flag_24
                        }
                    ),
                    contentDescription = "Set task's priority"
                )
            }

            if (task.attachments != null) {
                // attachment indicator
                Image(
                    modifier = Modifier.padding(end = 12.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                    painter = painterResource(id = R.drawable.baseline_attach_24),
                    contentDescription = "This task has ${task.attachments.size} attachment",
                )
            }
        }
    }
}

@Composable
private fun LoadingState(isLoading: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        AnimatedVisibility(visible = isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No tasks created yet",
            color = Color.Gray,
        )
    }
}

@Preview
@Composable
private fun TasksScreenPreview() {
    MemoTheme {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

        Scaffold(
            topBar = {
                AppSearch(
                    tasksViewModel = TasksViewModelPreview(),
                    drawerState = drawerState,
                )
            },
            bottomBar = {
                BottomNavigation(navController)
            }
        ) { innerPadding ->
            TasksScreen(
                modifier = Modifier.padding(innerPadding),
                tasksViewModel = TasksViewModelPreview(),
            )
        }
    }
}
