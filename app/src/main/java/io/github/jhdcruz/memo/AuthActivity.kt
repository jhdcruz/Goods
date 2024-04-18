package io.github.jhdcruz.memo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import io.github.jhdcruz.memo.ui.screens.login.LoginScreen
import io.github.jhdcruz.memo.ui.theme.MemoTheme

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MemoTheme {
                LoginScreen(context = this)
            }
        }
    }
}
