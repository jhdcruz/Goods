package io.github.jhdcruz.memo.ui.shared

import android.app.Activity
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelPreview
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSearch(
    modifier: Modifier = Modifier,
    navController: NavController,
    tasksViewModel: TasksViewModel,
    profile: String? = null,
) {
    var isSearching by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val query = tasksViewModel.query.collectAsState(initial = "").value

    val voiceSearch =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // matches[0] will contain the result of voice input
                val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                matches?.get(0)?.let { tasksViewModel::onQueryChange }
            }
        }

    SearchBar(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        query = query,
        onQueryChange = tasksViewModel::onQueryChange,
        onSearch = {
            scope.launch {
                tasksViewModel.onSearch()
            }
        },
        leadingIcon = {
            Image(
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                painter = painterResource(id = R.drawable.baseline_menu_24),
                contentDescription = "Tasks menu"
            )
        },
        trailingIcon = {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier.clickable { voiceSearch.launch(tasksViewModel.onVoiceSearch()) },
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_mic_24),
                    contentDescription = "Search tasks using voice input"
                )
                AsyncImage(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape),
                    model = profile,
                    contentDescription = "Profile icon",
                    placeholder = painterResource(id = R.drawable.baseline_user_circle_24),
                    error = painterResource(id = R.drawable.baseline_user_circle_24),
                )
            }
        },
        active = isSearching,
        onActiveChange = { active ->
            isSearching = active
        },
        placeholder = {
            Text(text = "Search your tasks")
        },
    ) {
    }
}

@Preview
@Composable
private fun AppSearchPreview() {
    val navController = rememberNavController()

    MemoTheme {
        AppSearch(
            navController = navController,
            tasksViewModel = TasksViewModelPreview()
        )
    }
}
