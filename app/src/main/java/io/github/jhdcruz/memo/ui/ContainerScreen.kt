package io.github.jhdcruz.memo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.ui.calendar.CalendarScreen
import io.github.jhdcruz.memo.ui.navigation.BottomNavigation
import io.github.jhdcruz.memo.ui.navigation.RootScreens
import io.github.jhdcruz.memo.ui.shared.AppSearch
import io.github.jhdcruz.memo.ui.shared.Sidebar
import io.github.jhdcruz.memo.ui.tasks.TasksScreen
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelImpl
import io.github.jhdcruz.memo.ui.tasks.detailsheet.TaskDetailsSheet
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

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val photoUrl = remember { mutableStateOf("") }

    LaunchedEffect(user?.uid) {
        photoUrl.value = user?.photoUrl.toString()
    }

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
                Box(modifier = Modifier.statusBarsPadding()) {
                    AppSearch(
                        profile = photoUrl.value,
                        drawerState = drawerState,
                    )
                }
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
            NavHost(
                navController = navController,
                startDestination = RootScreens.Tasks.route,
            ) {
                composable(RootScreens.Tasks.route) {
                    TasksScreen(
                        modifier = Modifier.padding(innerPadding),
                        tasksViewModel = tasksViewModel,
                    )
                }

                composable(RootScreens.Calendar.route) {
                    CalendarScreen(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }

            // New task bottom sheet
            if (sheetState.isVisible) {
                TaskDetailsSheet(
                    sheetState = sheetState,
                    tasksViewModel = tasksViewModel
                )
            }
        }
    }
}

@Preview
@Composable
private fun ContainerScreenPreview() {
    MemoTheme {
        ContainerScreen(
            user = null,
        )
    }
}
