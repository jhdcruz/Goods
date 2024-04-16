package io.github.jhdcruz.memo.ui.screens.login

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow

abstract class LoginViewModel : ViewModel() {
    abstract val email: Flow<String>
    abstract val password: Flow<String>
    abstract val status: Flow<String>

    abstract fun onEmailChange(email: String)
    abstract fun onPasswordChange(password: String)

    abstract fun onSignIn(context: Context)
    abstract fun onGoogleSignIn(context: Context)
    abstract fun onSignUp(context: Context, email: String, password: String)
    abstract fun onSignOut()
}
