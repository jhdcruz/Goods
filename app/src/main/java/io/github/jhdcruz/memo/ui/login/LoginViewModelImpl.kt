package io.github.jhdcruz.memo.ui.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jhdcruz.memo.MainActivity
import io.github.jhdcruz.memo.data.auth.AuthenticationRepository
import io.github.jhdcruz.memo.domain.response.AuthResponseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    override fun onSignIn(context: Context): LiveData<AuthResponseUseCase> {
        val result = MutableLiveData<AuthResponseUseCase>()

        viewModelScope.launch {
            result.value = async {
                authenticationRepository.signIn(
                    context = context,
                ).also { response ->
                    if (response is AuthResponseUseCase.Success) {
                        navigateToMain(context)
                    }
                }
            }.await()
        }

        return result
    }

    override fun onGoogleSignIn(context: Context): LiveData<AuthResponseUseCase> {
        val result = MutableLiveData<AuthResponseUseCase>()

        viewModelScope.launch {
            result.value = async {
                authenticationRepository.googleSignIn(
                    context = context,
                ).also { response ->
                    if (response is AuthResponseUseCase.Success) {
                        navigateToMain(context)
                    }
                }
            }.await()
        }

        return result
    }

    override fun onSignUp(context: Context): LiveData<AuthResponseUseCase> {
        val result = MutableLiveData<AuthResponseUseCase>()

        viewModelScope.launch(Dispatchers.IO) {
            result.value = async {
                authenticationRepository.signUp(
                    context = context,
                    email = _email.value,
                    password = _password.value
                ).also { response ->
                    if (response is AuthResponseUseCase.Success) {
                        navigateToMain(context)
                    }
                }
            }.await()
        }

        return result
    }

    override fun onSignOut() {
        viewModelScope.launch {
            authenticationRepository.signOut()
        }
    }

    private fun navigateToMain(context: Context) {
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(this)
        }
    }
}
