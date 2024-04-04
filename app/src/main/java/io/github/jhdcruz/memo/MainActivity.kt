package io.github.jhdcruz.memo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import io.github.jhdcruz.memo.ui.calendar.CalendarScreen
import io.github.jhdcruz.memo.ui.navigation.BottomNavigation
import io.github.jhdcruz.memo.ui.tasks.TasksScreen
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // check if user is signed in
        if (auth.currentUser == null) {
            // navigate to AuthActivity
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        setContent {
            MemoTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = { BottomNavigation(navController = navController) }
                ) { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = TasksDestination.route,
                        Modifier.padding(innerPadding)
                    ) {
                        composable(TasksDestination.route) {
                            TasksScreen(
                                navController = navController,
                            )
                        }

                        composable(CalendarDestination.route) {
                            CalendarScreen(
                                navController = navController,
                            )
                        }
                    }
                }
            }
        }
    }
}
