package io.github.jhdcruz.memo.ui.tasks.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import kotlinx.coroutines.launch


@Composable
fun TagsButton(
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel,
) {
    val scope = rememberCoroutineScope()

    var showTagsDialog by remember { mutableStateOf(false) }
    var newTag by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf<List<String>>(emptyList()) }

    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var onConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(onConfirm) {
        viewModel.onTagsChange(selectedTag)
        onConfirm = false
    }

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

    AlertDialog(
        modifier = Modifier.verticalScroll(ScrollState(0)),
        onDismissRequest = { showTagsDialog = false },
        dismissButton = {
            newTag = ""
            showTagsDialog = false
        },
        confirmButton = { onConfirm = true },
        text = {
            Row(modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = newTag,
                    onValueChange = {
                        // limit length
                        if (it.length <= 20) {
                            newTag = it
                        }
                    },
                    label = { Text(text = "Tag") },
                    placeholder = { Text(text = "Create new tag") },
                    supportingText = { Text(text = "Max. 20 characters") },
                    singleLine = true,
                )

                IconButton(
                    modifier = Modifier.padding(start = 8.dp),
                    onClick = {
                        scope.launch {
                            viewModel.onTagAdd(newTag)
                            newTag = ""

                            // append to tags instead of calling onGetTags() again
                            // this gets updated on opening the dialog anyways
                            tags = tags.toMutableList().apply {
                                add(newTag)
                            }
                        }
                    }
                ) {
                    Image(imageVector = Icons.Filled.Add, contentDescription = "Create tag")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (tags.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize()
                ) {
                    Text(text = "No tags created yet.")
                }
            } else {
                LazyRow(
                    modifier = Modifier.selectableGroup(),
                ) {
                    items(tags) { tag ->
                        Row {
                            Checkbox(
                                checked = selectedTag.contains(tag),
                                onCheckedChange = {
                                    // update selected tags
                                    scope.launch {
                                        selectedTag = if (it) {
                                            selectedTag.toMutableList().apply {
                                                add(tag)
                                            }
                                        } else {
                                            selectedTag.toMutableList().apply {
                                                remove(tag)
                                            }
                                        }

                                    }
                                },
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
    )
}
