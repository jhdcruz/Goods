package io.github.jhdcruz.memo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import io.github.jhdcruz.memo.CalendarDestination
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.TasksDestination
import io.github.jhdcruz.memo.ui.calendar.CalendarScreen
import io.github.jhdcruz.memo.ui.navigation.BottomNavigation
import io.github.jhdcruz.memo.ui.shared.AppSearch
import io.github.jhdcruz.memo.ui.shared.Sidebar
import io.github.jhdcruz.memo.ui.tasks.TasksScreen
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelImpl
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelPreview
import io.github.jhdcruz.memo.ui.tasks.bottomsheet.TaskDetailsSheet
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContainerScreen(
    user: FirebaseUser?,
    tasksViewModel: TasksViewModel = hiltViewModel<TasksViewModelImpl>(),
) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    val sheetState = rememberModalBottomSheetState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var showAddTask by remember { mutableStateOf(false) }
    val photoUrl = remember { mutableStateOf("") }

    LaunchedEffect(user?.uid) {
        photoUrl.value = user?.photoUrl.toString()
    }

    Box(Modifier.safeDrawingPadding()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Sidebar(drawerState = drawerState)
                }
            },
        ) {
            Scaffold(
                topBar = {
                    AppSearch(
                        navController = navController,
                        profile = photoUrl.value,
                        drawerState = drawerState,
                    )
                },
                bottomBar = {
                    BottomNavigation(
                        navController = navController,
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = {
                            scope.launch {
                                showAddTask = true
                                sheetState.show()
                            }
                        },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Image(
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                                painter = painterResource(id = R.drawable.baseline_add_24),
                                contentDescription = null
                            )
                            Text(text = "New task")
                        }
                    }
                }
            ) { innerPadding ->
                // New task bottom sheet
                if (showAddTask) {
                    TaskDetailsSheet(
                        tasksViewModel = tasksViewModel,
                        onDismissRequest = {
                            scope.launch {
                                sheetState.hide()
                                showAddTask = false
                            }
                        },
                        sheetState = sheetState,
                    )
                }

                NavHost(
                    navController,
                    startDestination = TasksDestination.route,
                    Modifier.padding(innerPadding)
                ) {
                    composable(TasksDestination.route) {
                        TasksScreen(
                            navController = navController,
                        )
                    }

                    composable(CalendarDestination.route) {
                        CalendarScreen(
                            navController = navController,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ContainerScreenPreview() {
    val tasksViewModel = TasksViewModelPreview()

    MemoTheme {
        ContainerScreen(
            user = null,
            tasksViewModel = tasksViewModel
        )
    }
}
