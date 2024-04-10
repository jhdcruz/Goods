package io.github.jhdcruz.memo.ui.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.domain.dateUntil
import io.github.jhdcruz.memo.ui.navigation.BottomNavigation
import io.github.jhdcruz.memo.ui.shared.AppSearch
import io.github.jhdcruz.memo.ui.tasks.bottomsheet.TaskDetailsSheet
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import kotlinx.coroutines.launch

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksListContent(
    tasksViewModel: TasksViewModel,
) {
    val scope = rememberCoroutineScope()
    val detailsSheetState = rememberModalBottomSheetState()

    var showTaskDetails by remember { mutableStateOf(false) }
    val taskList = tasksViewModel.taskList.collectAsState(emptyList())

    when {
        taskList.value.isEmpty() -> {
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

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                itemsIndexed(taskList.value) { _, task ->
                    AnimatedVisibility(visible = true) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            ListItem(
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        showTaskDetails = true
                                        detailsSheetState.show()
                                    }
                                },
                                overlineContent = {
                                    if (task.category?.isNotEmpty() == true) {
                                        Text(text = task.category ?: "Inbox")
                                    }
                                },
                                headlineContent = {
                                    Text(
                                        fontWeight = FontWeight.SemiBold,
                                        text = task.title
                                    )
                                },
                                supportingContent = {
                                    if (task.description?.isNotBlank() == true) {
                                        Text(
                                            text = task.description,
                                            maxLines = 6,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }

                                },
                                leadingContent = {
                                    Checkbox(
                                        modifier = Modifier.fillMaxHeight(),
                                        checked = task.isCompleted,
                                        onCheckedChange = {
                                            scope.launch {
                                                tasksViewModel.onTaskCompleted(task.id!!)
                                            }
                                        }
                                    )
                                },
                                trailingContent = {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
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

                                        if (task.dueDate != null) {
                                            Text(
                                                text = task.dueDate.dateUntil(),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            )

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            )
                        }
                    }

                    // preview task details
                    if (showTaskDetails) {
                        TaskDetailsSheet(
                            task = task,
                            onDismissRequest = {
                                scope.launch {
                                    detailsSheetState.hide()
                                    tasksViewModel.onClearInput()
                                    showTaskDetails = false
                                }
                            },
                            sheetState = detailsSheetState,
                        )
                    }
                }
            }
        }
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
                    navController = navController,
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
                navController = navController,
                tasksViewModel = TasksViewModelPreview(),
            )
        }
    }
}
