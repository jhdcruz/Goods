package io.github.jhdcruz.memo.ui.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
    val refreshState = rememberPullToRefreshState()
    val detailsSheetState = rememberModalBottomSheetState()

    var isLoading by remember { mutableStateOf(true) }
    var showTaskDetails by remember { mutableStateOf(false) }

    val taskList = tasksViewModel.taskList.collectAsState(emptyList())

    LaunchedEffect(refreshState, isLoading) {
        if (refreshState.isRefreshing || isLoading) {
            tasksViewModel.onGetTasks()
            isLoading = false
        }
    }

    PullToRefreshContainer(state = refreshState)

    when {
        isLoading -> {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        taskList.value.isEmpty() && !isLoading -> {
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
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                itemsIndexed(taskList.value) { _, task ->
                    ListItem(
                        modifier = Modifier.clickable {
                            scope.launch {
                                showTaskDetails = true
                                detailsSheetState.show()
                            }
                        },
                        overlineContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.baseline_folder_filled_24),
                                    contentDescription = null
                                )

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
                            if (task.description?.isNotBlank() == true) Text(task.description)

                            if (task.tags?.isNotEmpty() == true) {
                                Row(
                                    modifier = Modifier.horizontalScroll(ScrollState(0)),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    task.tags.forEach { tag ->
                                        SuggestionChip(
                                            modifier = Modifier.padding(horizontal = 4.dp),
                                            onClick = {},
                                            label = { Text(text = tag) }
                                        )
                                    }
                                }
                            }
                        },
                        leadingContent = {
                            AnimatedVisibility(
                                visible = !task.isCompleted,
                                enter = EnterTransition.None
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = {
                                        scope.launch {
                                            tasksViewModel.onTaskCompleted(task.id!!)
                                        }
                                    }
                                )
                            }
                        },
                        trailingContent = {
                            if (task.attachments?.isNotEmpty() == true) {
                                Image(
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceVariant),
                                    painter = painterResource(id = R.drawable.baseline_attach_24),
                                    contentDescription = "This task has attachments"
                                )
                            }

                            if (task.dueDate != null) {
                                Text(
                                    text = task.dueDate.dateUntil(),
                                    maxLines = 1
                                )
                            }
                        }
                    )
                    HorizontalDivider()

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
