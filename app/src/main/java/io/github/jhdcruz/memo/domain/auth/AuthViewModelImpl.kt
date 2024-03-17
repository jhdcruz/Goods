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

    override suspend fun onSignIn(context: Context): AuthResponse {
        authenticationRepository.signIn(
            context = context,
        ).apply {
            return when (this) {
                is AuthResponse.Success -> {
                    navigateToMain(context)
                    this
                }

                is AuthResponse.Invalid -> {
                    _status.value = this.message
                    this
                }

                is AuthResponse.NotFound -> {
                    _status.value = this.message
                    this
                }

                is AuthResponse.Error -> {
                    _status.value = this.exception.message ?: "An error occurred"
                    this
                }

                is AuthResponse.Failure -> {
                    _status.value = this.exception.message ?: "An error occurred"
                    this
                }

                else -> this // Do nothing
            }
        }
    }

    override suspend fun onGoogleSignIn(context: Context): AuthResponse {
        authenticationRepository.googleSignIn(
            context = context
        ).apply {
            return when (this) {
                is AuthResponse.Success -> {
                    navigateToMain(context)
                    this
                }

                is AuthResponse.Invalid -> {
                    _status.value = this.message
                    this
                }

                is AuthResponse.NotFound -> {
                    _status.value = this.message
                    this
                }

                is AuthResponse.Error -> {
                    _status.value = this.exception.message ?: "An error occurred"
                    this
                }

                is AuthResponse.Failure -> {
                    _status.value = this.exception.message ?: "An error occurred"
                    this
                }

                else -> this // Do nothing
            }
        }
    }

    override suspend fun onSignUp(context: Context): AuthResponse {
        authenticationRepository.signUp(
            context = context,
            email = _email.value,
            password = _password.value
        ).apply {
            return when (this) {
                is AuthResponse.Success -> {
                    navigateToMain(context)
                    this
                }

                is AuthResponse.Invalid -> {
                    _status.value = this.message
                    this
                }

                is AuthResponse.Error -> {
                    _status.value = this.exception.message ?: "An error occurred"
                    this
                }

                is AuthResponse.Failure -> {
                    _status.value = this.exception.message ?: "An error occurred"
                    this
                }

                else -> this // Do nothing
            }
        }
    }

    private fun navigateToMain(context: Context) {
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(this)
        }
    }
}
