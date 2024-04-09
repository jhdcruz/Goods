package io.github.jhdcruz.memo.ui.tasks.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.ui.shared.PickerDialog
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryButton(
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel,
) {
    val scope = rememberCoroutineScope()
    var categories by remember { mutableStateOf(listOf("loading")) }

    val taskCategory = viewModel.taskCategory.collectAsState(initial = "")

    var showCategoryDialog by remember { mutableStateOf(false) }
    var newCategory by remember { mutableStateOf("") }

    IconButton(
        modifier = modifier,
        onClick = {
            showCategoryDialog = true

            scope.launch {
                categories = viewModel.onGetCategories()
            }
        }
    ) {
        Image(
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            painter = painterResource(id = R.drawable.baseline_folder_24),
            contentDescription = "Set task's category"
        )
    }

    if (showCategoryDialog) {
        var selectedCategory by remember { mutableStateOf(taskCategory.value) }

        PickerDialog(
            title = { Text(text = "Assign category for this task") },
            onDismissRequest = { showCategoryDialog = false },
            buttons = {
                TextButton(
                    onClick = { showCategoryDialog = false }
                ) {
                    Text(text = "Cancel")
                }

                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.onCategoryChange(selectedCategory)
                            showCategoryDialog = false
                        }
                    }
                ) {
                    Text(text = "Confirm")
                }
            }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        modifier = Modifier.height(64.dp),
                        value = newCategory,
                        onValueChange = {
                            // limit length
                            if (it.length <= 20) {
                                newCategory = it
                            }
                        },
                        placeholder = { Text(text = "Create new category") },
                        singleLine = true,
                        trailingIcon = {
                            FilledIconButton(
                                modifier = Modifier.padding(horizontal = 6.dp),
                                onClick = {
                                    scope.launch {
                                        viewModel.onCategoryAdd(newCategory)

                                        // append manually to avoid calling onGetTags again
                                        categories = listOf(newCategory + categories)
                                        newCategory = ""
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Create new tag"
                                )
                            }
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))

                when {
                    categories.contains("loading") -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    categories.isEmpty() -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            Text(text = "No categories created yet.")
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .verticalScroll(ScrollState(0))
                                    .selectableGroup()
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                categories.forEach { category ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .selectable(
                                                selected = selectedCategory == category,
                                                onClick = {
                                                    scope.launch {
                                                        selectedCategory = category
                                                    }
                                                }
                                            ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedCategory == category,
                                            onClick = null
                                        )
                                        Text(
                                            text = category,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
