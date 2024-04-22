package io.github.jhdcruz.memo.ui.navigation

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.jhdcruz.memo.ui.theme.MemoTheme

@Composable
fun BottomNavigation(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val rootScreens =
        listOf(
            RootScreens.Tasks,
            RootScreens.Calendar,
            RootScreens.Settings,
        )

    NavigationBar {
        rootScreens.forEach { item ->
            NavigationBarItem(
                alwaysShowLabel = true,
                label = {
                    Text(
                        fontWeight =
                            if (currentRoute == item.route) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                        text = item.title,
                    )
                },
                selected = currentRoute == item.route,
                icon = {
                    // change icon to filled based on selected
                    val icon: Int by animateIntAsState(
                        targetValue =
                            if (currentRoute == item.route) {
                                item.activeIcon
                            } else {
                                item.inactiveIcon
                            },
                        label = "selected navigation transition",
                    )

                    Image(
                        colorFilter =
                            if (currentRoute == item.route) {
                                ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer)
                            } else {
                                ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                        painter = painterResource(id = icon),
                        contentDescription = item.title,
                    )
                },
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NavPreview() {
    MemoTheme {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = { BottomNavigation(navController) },
        ) { innerPadding ->
            Text(
                modifier = Modifier.padding(innerPadding),
                text = "Surface content",
            )
        }
    }
}
