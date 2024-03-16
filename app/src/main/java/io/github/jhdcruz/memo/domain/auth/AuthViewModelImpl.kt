package io.github.jhdcruz.memo.domain.auth

import android.content.Context
import android.content.Intent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jhdcruz.memo.MainActivity
import io.github.jhdcruz.memo.data.auth.AuthenticationRepository
import io.github.jhdcruz.memo.data.response.AuthResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModelImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : AuthViewModel() {

    private val _email = MutableStateFlow("")
    override val email: Flow<String> = _email

    private val _password = MutableStateFlow("")
    override val password: Flow<String> = _password

    private val _status = MutableStateFlow("")
    override val status: Flow<String> = _status

    override fun onEmailChange(email: String) {
        _email.value = email
    }

    override fun onPasswordChange(password: String) {
        _password.value = password
    }

    override suspend fun onSignIn(context: Context) {
        authenticationRepository.signIn(
            context = context,
        ).apply {
            when (this) {
                is AuthResponse.Success -> navigateToMain(context)
                is AuthResponse.Invalid -> _status.value = this.message
                is AuthResponse.NotFound -> _status.value = this.message
                is AuthResponse.Error -> _status.value =
                    this.exception.message ?: "An error occurred"

                is AuthResponse.Failure -> _status.value =
                    this.exception.message ?: "An error occurred"

                else -> Unit // Do nothing
            }
        }
    }

    override suspend fun onGoogleSignIn(context: Context) {
        authenticationRepository.googleSignIn(
            context = context
        ).apply {
            when (this) {
                is AuthResponse.Success -> navigateToMain(context)
                is AuthResponse.Invalid -> _status.value = this.message
                is AuthResponse.NotFound -> _status.value = this.message
                is AuthResponse.Error -> _status.value =
                    this.exception.message ?: "An error occurred"

                is AuthResponse.Failure -> _status.value =
                    this.exception.message ?: "An error occurred"

                else -> Unit // Do nothing
            }
        }
    }

    override suspend fun onSignUp(context: Context) {
        authenticationRepository.signUp(
            context = context,
            email = _email.value,
            password = _password.value
        ).apply {
            when (this) {
                is AuthResponse.Success -> navigateToMain(context)
                is AuthResponse.Invalid -> _status.value = this.message
                is AuthResponse.Error -> _status.value =
                    this.exception.message ?: "An error occurred"

                is AuthResponse.Failure -> _status.value =
                    this.exception.message ?: "An error occurred"

                else -> Unit // Do nothing
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
