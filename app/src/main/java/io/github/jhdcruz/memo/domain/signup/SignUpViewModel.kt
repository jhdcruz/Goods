package io.github.jhdcruz.memo.domain.signup

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow

abstract class SignUpViewModel : ViewModel() {
    abstract val email: Flow<String>
    abstract val password: Flow<String>

    abstract fun onEmailChange(email: String)
    abstract fun onPasswordChange(password: String)
    abstract fun onSignUp(context: Context)
}
