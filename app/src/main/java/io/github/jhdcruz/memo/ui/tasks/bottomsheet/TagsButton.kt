package io.github.jhdcruz.memo.ui.tasks.bottomsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.ui.shared.EmptyState
import io.github.jhdcruz.memo.ui.shared.LoadingState
import io.github.jhdcruz.memo.ui.shared.PickerDialog
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelPreview
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import kotlinx.coroutines.launch

/**
 * Icon button for use with the bottom sheet task detail panel
 * which shows dialog for complete tags list, and input for adding one.
 */
@Composable
fun TagsButton(
    modifier: Modifier = Modifier,
    tasksViewModel: TasksViewModel,
) {
    val scope = rememberCoroutineScope()

    val tags = tasksViewModel.tags.collectAsState(initial = emptyList())
    val taskTags = tasksViewModel.taskTags.collectAsState(initial = emptyList())

    var showTagsDialog by remember { mutableStateOf(false) }

    val buttonIcon = if (taskTags.value.isEmpty()) {
        R.drawable.baseline_label_24
    } else {
        R.drawable.baseline_label_filled_24
    }

    IconButton(
        modifier = modifier.widthIn(min = 48.dp),
        onClick = {
            showTagsDialog = true

            scope.launch {
                tasksViewModel.onGetTags()
            }
        }
    ) {
        BadgedBox(
            badge = {
                if (taskTags.value.isNotEmpty()) {
                    Badge {
                        Text(
                            modifier = Modifier.semantics {
                                contentDescription = "${taskTags.value.size} tags selected"
                            },
                            text = taskTags.value.size.toString(),
                        )
                    }
                }
            }
        ) {
            Image(
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                painter = painterResource(id = buttonIcon),
                contentDescription = "Set task's tags"
            )
        }
    }

    if (showTagsDialog) {
        var selectedTag by remember { mutableStateOf(taskTags.value) }

        TagSelectionDialog(
            tasksViewModel = tasksViewModel,
            onDismissRequest = { showTagsDialog = false },
            onConfirm = {
                scope.launch {
                    tasksViewModel.onTagsChange(selectedTag)
                    showTagsDialog = false
                }
            },
            tags = tags.value,
            selectedTags = selectedTag,
            onTagSelected = { tag ->
                scope.launch {
                    selectedTag = if (selectedTag.contains(tag)) {
                        selectedTag - tag
                    } else {
                        selectedTag + tag
                    }
                }
            }
        )
    }
}

@Composable
private fun TagSelectionDialog(
    tasksViewModel: TasksViewModel,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    tags: List<String>,
    selectedTags: List<String>,
    onTagSelected: (String) -> Unit,
) {
    PickerDialog(
        title = { Text(text = "Assign tags for this task") },
        onDismissRequest = onDismissRequest,
        buttons = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }

            TextButton(onClick = onConfirm) {
                Text(text = "Confirm")
            }
        }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            TagInputField(tasksViewModel = tasksViewModel)
            HorizontalDivider(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
            TagList(tags = tags, selectedTags = selectedTags, onTagSelected = onTagSelected)
        }
    }
}

@Composable
private fun TagInputField(
    tasksViewModel: TasksViewModel,
) {
    val scope = rememberCoroutineScope()
    val tags = tasksViewModel.tags.collectAsState(initial = emptyList())

    var newTag by remember { mutableStateOf("") }
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
                        tasksViewModel.onTagAdd(newTag)

                        val appendedTag = listOf(newTag) + tags.value
                        // append manually to avoid calling onGetTags again
                        tasksViewModel.onLocalTagsChange(appendedTag)
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

@Composable
private fun TagList(
    tags: List<String>,
    selectedTags: List<String>,
    onTagSelected: (String) -> Unit,
) {
    when {
        tags.contains("loading") -> LoadingState()
        tags.isEmpty() -> EmptyState("No tags created yet.")
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(ScrollState(0))
                        .selectableGroup()
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    tags.forEach { tag ->
                        TagRow(
                            tag = tag,
                            isSelected = selectedTags.contains(tag),
                            onTagSelected = onTagSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TagRow(tag: String, isSelected: Boolean, onTagSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .toggleable(
                role = Role.Checkbox,
                value = isSelected,
                onValueChange = { onTagSelected(tag) }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = null
        )
        Text(
            text = tag,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun TagsButtonPreview() {
    val previewViewModel = TasksViewModelPreview()

    MemoTheme {
        TagsButton(tasksViewModel = previewViewModel)
    }
}
