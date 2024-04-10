package io.github.jhdcruz.memo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import io.github.jhdcruz.memo.ui.calendar.CalendarScreen
import io.github.jhdcruz.memo.ui.navigation.BottomNavigation
import io.github.jhdcruz.memo.ui.shared.AppSearch
import io.github.jhdcruz.memo.ui.shared.Sidebar
import io.github.jhdcruz.memo.ui.tasks.TasksScreen
import io.github.jhdcruz.memo.ui.tasks.bottomsheet.TaskDetailsSheet
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
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
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()

                val photoUrl = remember { mutableStateOf("") }

                var showAddTask by remember { mutableStateOf(false) }
                val sheetState = rememberModalBottomSheetState()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                LaunchedEffect(auth.currentUser) {
                    photoUrl.value = auth.currentUser?.photoUrl.toString()
                }

                Box(Modifier.safeDrawingPadding()) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                Sidebar(
                                    drawerState = drawerState,
                                )
                            }
                        },
                    ) {
                        Scaffold(
                            topBar = {
                                AppSearch(
                                    navController = navController,
                                    profile = photoUrl.value,
                                    drawerState = drawerState,
                                )
                            },
                            bottomBar = {
                                BottomNavigation(
                                    navController = navController,
                                )
                            },
                            floatingActionButton = {
                                ExtendedFloatingActionButton(
                                    onClick = {
                                        scope.launch {
                                            showAddTask = true
                                            sheetState.show()
                                        }
                                    },
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Image(
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                                            painter = painterResource(id = R.drawable.baseline_add_24),
                                            contentDescription = null
                                        )
                                        Text(text = "New task")
                                    }
                                }
                            }
                        ) { innerPadding ->
                            // New task bottom sheet
                            if (showAddTask) {
                                TaskDetailsSheet(
                                    onDismissRequest = {
                                        scope.launch {
                                            sheetState.hide()
                                            showAddTask = false
                                        }
                                    },
                                    sheetState = sheetState,
                                )
                            }

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
    }
}
