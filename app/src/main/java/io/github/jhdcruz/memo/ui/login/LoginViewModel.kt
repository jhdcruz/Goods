package io.github.jhdcruz.memo.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.jhdcruz.memo.domain.response.AuthResponseUseCase
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
