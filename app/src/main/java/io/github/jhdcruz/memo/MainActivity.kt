package io.github.jhdcruz.memo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import io.github.jhdcruz.memo.ui.ContainerScreen
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // check if user is signed in
        if (auth.currentUser == null) {
            Toast.makeText(this, "User timed out, Redirecting to login.", Toast.LENGTH_SHORT).show()

            // navigate to AuthActivity
            val intent = Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        } else {
            setContent {
                MemoTheme {
                    ContainerScreen(user = auth.currentUser)
                }
            }
        }
    }
}
