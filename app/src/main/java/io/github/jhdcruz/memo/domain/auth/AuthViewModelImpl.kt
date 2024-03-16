package io.github.jhdcruz.memo.domain.auth

import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jhdcruz.memo.MainActivity
import io.github.jhdcruz.memo.data.auth.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModelImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : AuthViewModel() {

    private val _email = MutableStateFlow("")
    override val email: Flow<String> = _email

    private val _password = MutableStateFlow("")
    override val password: Flow<String> = _password

    override fun onEmailChange(email: String) {
        _email.value = email
    }

    override fun onPasswordChange(password: String) {
        _password.value = password
    }

    override fun onSignIn(context: Context) {
        viewModelScope.launch {
            authenticationRepository.signIn(
                context = context,
            ).apply {
                if (this) navigateToMain(context)
            }
        }
    }

    override fun onGoogleSignIn(context: Context) {
        viewModelScope.launch {
            authenticationRepository.googleSignIn(
                context = context
            ).apply {
                if (this) navigateToMain(context)
            }
        }
    }

    override fun onSignUp(context: Context) {
        viewModelScope.launch {
            authenticationRepository.signUp(
                context = context,
                email = _email.value,
                password = _password.value
            ).apply {
                if (this) navigateToMain(context)
            }
        }
    }

    private fun navigateToMain(context: Context) {
        Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }
}
