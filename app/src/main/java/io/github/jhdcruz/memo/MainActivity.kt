package io.github.jhdcruz.memo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.jhdcruz.memo.ui.signup.SignUpScreen
import io.github.jhdcruz.memo.ui.theme.MemoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemoTheme {
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStack?.destination

                Scaffold { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = SignUpDestination.route,
                        Modifier.padding(innerPadding)
                    ) {
                        composable(SignUpDestination.route) {
                            SignUpScreen(
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}
