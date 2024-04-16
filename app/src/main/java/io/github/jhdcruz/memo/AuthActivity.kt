package io.github.jhdcruz.memo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.jhdcruz.memo.ui.login.LoginScreen
import io.github.jhdcruz.memo.ui.theme.MemoTheme

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MemoTheme {
                val context = this

                Box(modifier = Modifier.safeDrawingPadding()) {
                    Scaffold { innerPadding ->
                        LoginScreen(
                            modifier = Modifier.padding(innerPadding),
                            context = context,
                        )
                    }
                }
            }
        }
    }
}
