package io.github.jhdcruz.memo.ui.tasks

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.jhdcruz.memo.R
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
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { /*TODO*/ }) {
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
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
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
