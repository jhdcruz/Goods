package io.github.jhdcruz.memo.ui.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.jhdcruz.memo.ui.navigation.BottomNavigation
import io.github.jhdcruz.memo.ui.shared.AppSearch
import io.github.jhdcruz.memo.ui.theme.MemoTheme

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: TasksViewModel = hiltViewModel<TasksViewModelImpl>(),
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
        ) {
            // Main screen content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Tasks")
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
                viewModel = TasksViewModelPreview(),
            )
        }
    }
}
