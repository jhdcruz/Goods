package io.github.jhdcruz.memo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.jhdcruz.memo.domain.auth.AuthViewModel
import io.github.jhdcruz.memo.ui.login.LoginScreen
import io.github.jhdcruz.memo.ui.theme.MemoTheme
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    @Inject
    lateinit var authViewModel: AuthViewModel

    // Google sign-in request code
    private val oneTapId = 9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemoTheme {

                Scaffold { innerPadding ->
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = authViewModel
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // initialize sign in using credential manager
        // https://developer.android.com/training/sign-in/passkeys
        authViewModel.initSignIn(this.applicationContext)
    }
}
