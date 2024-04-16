package io.github.jhdcruz.memo.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.ui.ContainerViewModel
import io.github.jhdcruz.memo.ui.ContainerViewModelImpl
import io.github.jhdcruz.memo.ui.ContainerViewModelPreview
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import kotlinx.coroutines.launch

@Composable
fun Sidebar(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    containerViewModel: ContainerViewModel = hiltViewModel<ContainerViewModelImpl>(),
) {
    val scope = rememberCoroutineScope()

    val categories = containerViewModel.categories.collectAsState(initial = listOf("loading"))
    var selected by remember { mutableIntStateOf(0) }

    LaunchedEffect(drawerState.isOpen, categories.value) {
        containerViewModel.onGetCategories()
    }

    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Tasks",
                modifier = Modifier.padding(16.dp)
            )

            NavigationDrawerItem(
                label = { Text(text = "All") },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_checklist_24),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        contentDescription = null
                    )
                },
                selected = selected == 0,
                onClick = {
                    selected = 0

                    scope.launch {
                        drawerState.close()
                    }
                }
            )
            NavigationDrawerItem(
                label = { Text(text = "Inbox") },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_inbox_24),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        contentDescription = null
                    )
                },
                selected = selected == 1,
                onClick = {
                    selected = 1

                    scope.launch {
                        drawerState.close()
                    }
                }
            )
            NavigationDrawerItem(
                label = { Text(text = "Next 7 days") },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_event_upcoming_24),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        contentDescription = null
                    )
                },
                selected = selected == 2,
                onClick = {
                    selected = 2

                    scope.launch {
                        drawerState.close()
                    }
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Categories",
                modifier = Modifier.padding(16.dp),
            )


            // loading state
            when {
                drawerState.isOpen && categories.value.contains("loading") ->
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp)
                    )

                drawerState.isOpen && categories.value.isEmpty() ->
                    EmptyCategory(containerViewModel = containerViewModel)

                else ->
                    LazyColumn {
                        itemsIndexed(categories.value) { index, category ->
                            NavigationDrawerItem(
                                label = { Text(text = category) },
                                icon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.baseline_folder_24),
                                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                                        contentDescription = null
                                    )
                                },
                                // +3 because there are 3 items above the categories
                                selected = selected == index + 3,
                                onClick = {
                                    scope.launch {
                                        selected = index + 3
                                        drawerState.close()
                                    }
                                }
                            )
                        }
                    }
            }

        }
    }
}

@Composable
private fun EmptyCategory(
    containerViewModel: ContainerViewModel,
) {
    val scope = rememberCoroutineScope()

    var showCategoryDialog by remember { mutableStateOf(false) }
    val newCategory = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            text = "No categories",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,

            )

        FilledTonalButton(
            modifier = Modifier.height(46.dp),
            onClick = { showCategoryDialog = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                Text(text = "Create new category")
            }
        }

        if (showCategoryDialog) {
            ConfirmDialog(
                onDismissRequest = { showCategoryDialog = false },
                onConfirmation = {
                    scope.launch {
                        containerViewModel.onCategoryAdd(newCategory.value)
                        showCategoryDialog = false
                    }
                },
                dialogTitle = {
                    Text(text = "Add new category")
                },
                dialogContent = {
                    OutlinedTextField(
                        label = { Text(text = "Category") },
                        value = newCategory.value,
                        onValueChange = { newCategory.value = it })
                },
                icon = {
                    Icon(
                        modifier = Modifier.size(38.dp),
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun SidebarPreview() {
    MemoTheme {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val containerViewModel = ContainerViewModelPreview()

        Sidebar(drawerState = drawerState, containerViewModel = containerViewModel)
    }
}
