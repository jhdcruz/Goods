package io.github.jhdcruz.memo.ui.shared

import android.app.Activity
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.ui.login.LoginViewModel
import io.github.jhdcruz.memo.ui.login.LoginViewModelImpl
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelImpl
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelPreview
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSearch(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    profile: String? = null,
    tasksViewModel: TasksViewModel = hiltViewModel<TasksViewModelImpl>(),
    loginViewModel: LoginViewModel = hiltViewModel<LoginViewModelImpl>(),
) {
    val scope = rememberCoroutineScope()
    val query = tasksViewModel.query.collectAsState(initial = "").value

    var showProfileMenu by remember { mutableStateOf(false) }

    val voiceSearch =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // matches[0] will contain the result of voice input
                val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                scope.launch {
                    tasksViewModel.onQueryChange(matches?.get(0) ?: "")
                    tasksViewModel.onSearch()
                }
            }
        }

    DockedSearchBar(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        query = query,
        onQueryChange = tasksViewModel::onQueryChange,
        onSearch = {
            scope.launch {
                tasksViewModel.onSearch()
            }
        },
        leadingIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }
            ) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_menu_24),
                    contentDescription = "Tasks menu"
                )
            }
        },
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    modifier = Modifier.offset(x = 4.dp),
                    onClick = {
                        voiceSearch.launch(tasksViewModel.onVoiceSearch())
                    }) {
                    Image(
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        painter = painterResource(id = R.drawable.baseline_mic_24),
                        contentDescription = "Search tasks using voice input"
                    )
                }
                IconButton(onClick = { showProfileMenu = true }) {
                    AsyncImage(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape),
                        model = profile,
                        contentDescription = "Profile icon",
                        placeholder = painterResource(id = R.drawable.baseline_user_circle_24),
                        error = painterResource(id = R.drawable.baseline_user_circle_24),
                    )

                    DropdownMenu(
                        expanded = showProfileMenu,
                        onDismissRequest = { showProfileMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Sign out") },
                            onClick = { loginViewModel.onSignOut() }
                        )
                    }
                }
            }
        },

        // we don't need the search bar content, we filter directly
        active = false,
        onActiveChange = {},
        placeholder = {
            Text(text = "Search your tasks")
        },
    ) {
    }
}

@Preview
@Composable
private fun AppSearchPreview() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    MemoTheme {
        AppSearch(
            tasksViewModel = TasksViewModelPreview(),
            drawerState = drawerState,
        )
    }
}
