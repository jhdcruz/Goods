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
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.ui.shared.PickerDialog
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsButton(
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel,
) {
    val scope = rememberCoroutineScope()
    var tags by remember { mutableStateOf(listOf("loading")) }

    val taskTags = viewModel.taskTags.collectAsState(initial = emptyList())

    var showTagsDialog by remember { mutableStateOf(false) }
    var newTag by remember { mutableStateOf("") }

    IconButton(
        modifier = modifier,
        onClick = {
            showTagsDialog = true

            scope.launch {
                tags = viewModel.onGetTags()
            }
        }
    ) {
        Image(
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            painter = painterResource(id = R.drawable.baseline_label_24),
            contentDescription = "Set task's tags"
        )
    }

    if (showTagsDialog) {
        var selectedTag by remember { mutableStateOf(taskTags.value) }

        PickerDialog(
            title = { Text(text = "Assign tags for this task") },
            onDismissRequest = { showTagsDialog = false },
            buttons = {
                TextButton(
                    onClick = {
                        showTagsDialog = false
                    }
                ) {
                    Text(text = "Cancel")
                }

                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.onTagsChange(selectedTag)
                            showTagsDialog = false
                        }
                    }
                ) {
                    Text(text = "Confirm")
                }
            }
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        modifier = Modifier.height(64.dp),
                        value = newTag,
                        onValueChange = {
                            // limit length
                            if (it.length <= 20) {
                                newTag = it
                            }
                        },
                        placeholder = { Text(text = "Create new tag") },
                        singleLine = true,
                        trailingIcon = {
                            FilledIconButton(
                                modifier = Modifier.padding(horizontal = 6.dp),
                                onClick = {
                                    scope.launch {
                                        viewModel.onTagAdd(newTag)

                                        // append manually to avoid calling onGetTags again
                                        tags = listOf(newTag + tags)
                                        newTag = ""
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
                    tags.contains("loading") -> {
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

                    tags.isEmpty() -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            Text(text = "No tags created yet.")
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                        ) {
                            // I dunno LazyRow doesn't work here, intrinsic size error blah-blah-blah
                            Column(
                                modifier = Modifier
                                    .verticalScroll(ScrollState(0))
                                    .selectableGroup()
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                tags.forEach { tag ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .toggleable(
                                                role = Role.Checkbox,
                                                value = selectedTag.contains(tag),
                                                onValueChange = {
                                                    scope.launch {
                                                        // selection/deselection of tags
                                                        selectedTag =
                                                            if (selectedTag.contains(tag)) {
                                                                selectedTag - tag
                                                            } else {
                                                                selectedTag + tag
                                                            }
                                                    }
                                                },
                                            ), verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = selectedTag.contains(tag),
                                            onCheckedChange = null
                                        )
                                        Text(
                                            text = tag,
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
