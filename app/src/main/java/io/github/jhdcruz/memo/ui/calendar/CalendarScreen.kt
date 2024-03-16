package io.github.jhdcruz.memo.ui.calendar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.jhdcruz.memo.ui.theme.MemoTheme

@Composable
fun CalendarScreen(
    navController: NavController,
) {
    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text(text = "Calendar")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPreview() {
    MemoTheme {
        val navController = rememberNavController()

        CalendarScreen(navController)
    }
}

