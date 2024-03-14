package io.github.jhdcruz.memo.domain.auth

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class AuthViewModelPreview : AuthViewModel() {
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    override val email: Flow<String> = _email
    override val password: Flow<String> = _password

    override fun onEmailChange(email: String) {
        _email.value = email
    }

    override fun onPasswordChange(password: String) {
        _password.value = password
    }

    override fun onSignIn(context: Context) {
        flow {
            emit(true)
        }
    }

    override fun onGoogleSignIn(context: Context) {
        flow {
            emit(true)
        }
    }

    override fun onSignUp(context: Context) {
        flow {
            emit(true)
        }
    }
}
