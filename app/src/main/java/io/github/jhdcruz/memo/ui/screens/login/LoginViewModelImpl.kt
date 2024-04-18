package io.github.jhdcruz.memo.ui.screens.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jhdcruz.memo.MainActivity
import io.github.jhdcruz.memo.data.auth.AuthenticationRepository
import io.github.jhdcruz.memo.domain.response.AuthResponseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModelImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
) : LoginViewModel() {

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

    override fun onSignIn(context: Context) {
        viewModelScope.launch {
            authenticationRepository.signIn(
                context = context,
            ).apply {
                when (this) {
                    is AuthResponseUseCase.Success -> {
                        navigateToMain(context)
                    }

                    is AuthResponseUseCase.Error -> _status.value =
                        this.exception.message.toString()

                    is AuthResponseUseCase.Failure -> _status.value =
                        this.exception.message.toString()

                    is AuthResponseUseCase.NotFound -> _status.value = "User not found"
                    else -> {}
                }
            }
        }
    }

    override fun onGoogleSignIn(context: Context) {
        viewModelScope.launch {
            authenticationRepository.googleSignIn(
                context = context,
            ).apply {
                when (this) {
                    is AuthResponseUseCase.Success -> {
                        navigateToMain(context)
                    }

                    is AuthResponseUseCase.Error -> _status.value =
                        this.exception.message.toString()

                    is AuthResponseUseCase.Failure -> _status.value =
                        this.exception.message.toString()

                    is AuthResponseUseCase.NotFound -> _status.value = "User not found"
                    else -> {}
                }
            }
        }
    }

    override fun onSignUp(context: Context, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.signUp(
                context = context,
                email = email,
                password = password,
            ).apply {
                when (this) {
                    is AuthResponseUseCase.Success -> {
                        navigateToMain(context)
                    }

                    is AuthResponseUseCase.Error -> _status.value =
                        this.exception.message.toString()

                    is AuthResponseUseCase.Failure -> _status.value =
                        this.exception.message.toString()

                    else -> {}
                }
            }
        }
    }

    override fun onSignOut() {
        viewModelScope.launch {
            authenticationRepository.signOut()
        }
    }

    private fun navigateToMain(context: Context) {
        viewModelScope.launch {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
    }
}
