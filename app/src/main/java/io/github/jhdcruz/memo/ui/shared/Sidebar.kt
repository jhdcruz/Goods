package io.github.jhdcruz.memo.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jhdcruz.memo.R
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import kotlinx.coroutines.launch

@Composable
fun Sidebar(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
) {
    var selected by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

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

            LazyColumn {
            }
        }
    }
}

@Preview
@Composable
private fun SidebarPreview() {
    MemoTheme {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

        Sidebar(drawerState = drawerState)
    }
}
