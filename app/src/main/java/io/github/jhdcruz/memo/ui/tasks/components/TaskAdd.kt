@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.jhdcruz.memo.ui.tasks.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelImpl
import io.github.jhdcruz.memo.ui.theme.MemoTheme

@Composable
fun TaskAdd(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    tasksViewModel: TasksViewModel = hiltViewModel<TasksViewModelImpl>()
) {
    ModalBottomSheet(
        modifier = modifier.wrapContentHeight(),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        TaskAddContent()
    }
}

@Composable
private fun TaskAddContent() {
    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1F),
                value = "",
                onValueChange = { },
                singleLine = true,
                label = { Text(text = "Title") },
                placeholder = { Text(text = "Headline of your task") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent
                )
            )

            IconButton(
                onClick = {
                }) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_done_24),
                    contentDescription = "Add task"
                )
            }
        }

        HorizontalDivider()

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            minLines = 10,
            maxLines = 30,
            placeholder = { Text(text = "Elaborate the details of your task") },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent
            )
        )

        HorizontalDivider()

        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                }) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_flag_24),
                    contentDescription = "Set task's priority"
                )
            }

            IconButton(
                onClick = {
                }) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_folder_24),
                    contentDescription = "Set task's category"
                )
            }

            IconButton(
                onClick = {
                }) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_calendar_24),
                    contentDescription = "Set task's due date"
                )
            }

            IconButton(
                onClick = {
                }) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_attach_24),
                    contentDescription = "Upload attachments"
                )
            }

            // space-between
            Spacer(modifier = Modifier.weight(1F))

            IconButton(
                onClick = {
                }) {
                Image(
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    painter = painterResource(id = R.drawable.baseline_label_24),
                    contentDescription = "Set task's label"
                )
            }

        }
    }
}


@Composable
@Preview(showBackground = true)
private fun TaskAddPreview() {
    MemoTheme {
        TaskAddContent()
    }
}
