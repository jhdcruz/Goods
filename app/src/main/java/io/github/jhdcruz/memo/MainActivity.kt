package io.github.jhdcruz.memo

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import io.github.jhdcruz.memo.service.reminders.ReminderService
import io.github.jhdcruz.memo.service.reminders.ReminderSyncService
import io.github.jhdcruz.memo.ui.screens.container.ContainerScreen
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var auth: FirebaseAuth

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            // Start services if all permissions are granted
            if (permissions.values.all { isGranted -> isGranted }) {
                startServices()
            } else {
                Toast.makeText(
                    this,
                    "Reminders will not work when denied.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // check if user is signed in
        if (auth.currentUser == null) {
            Toast.makeText(this, "User timed out, Redirecting to login.", Toast.LENGTH_SHORT).show()

            // navigate to AuthActivity
            Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(this)
            }
        } else {
            checkServicePermissions()

            setContent {
                MemoTheme {
                    ContainerScreen(user = auth.currentUser)
                }
            }
        }
    }

    /**
     * Check permission for background services
     * based on android versions.
     */
    private fun checkServicePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS,
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                )
            } else {
                Intent(this, ReminderSyncService::class.java).apply {
                    startService(this)
                }
            }

            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.SCHEDULE_EXACT_ALARM,
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.SCHEDULE_EXACT_ALARM,
                        android.Manifest.permission.SET_ALARM,
                        android.Manifest.permission.USE_EXACT_ALARM,
                    ),
                )
            } else {
                Intent(this, ReminderSyncService::class.java).apply {
                    startService(this)
                }
            }
        }

        // For Android 14 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC,
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(
                    arrayOf(android.Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC),
                )
            } else {
                startServices()
            }
        }
    }

    private fun startServices() {
        Intent(this, ReminderService::class.java).apply {
            startService(this)
        }
    }
}
