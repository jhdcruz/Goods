package io.github.jhdcruz.memo.domain.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow

abstract class AuthViewModel : ViewModel() {
    abstract val email: Flow<String>
    abstract val password: Flow<String>

    abstract fun onEmailChange(email: String)
    abstract fun onPasswordChange(password: String)

    abstract fun initSignIn(context: Context)
    abstract fun onSignUp(context: Context)
}
