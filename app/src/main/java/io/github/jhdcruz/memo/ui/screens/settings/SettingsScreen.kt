package io.github.jhdcruz.memo.ui.screens.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import io.github.jhdcruz.memo.ui.navigation.BottomNavigation
import io.github.jhdcruz.memo.ui.theme.MemoTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Text(text = "Settings")
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    MemoTheme {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = {
                BottomNavigation(navController)
            }
        ) { innerPadding ->
            SettingsScreen(
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
