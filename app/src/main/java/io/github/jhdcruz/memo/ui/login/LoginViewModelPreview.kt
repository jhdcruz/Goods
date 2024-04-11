package io.github.jhdcruz.memo.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.jhdcruz.memo.domain.response.AuthResponseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class LoginViewModelPreview : LoginViewModel() {
    override val email: Flow<String> = MutableStateFlow("")
    override val password: Flow<String> = MutableStateFlow("")
    override val status: Flow<String> = MutableStateFlow("")

    override fun onEmailChange(email: String) {}

    override fun onPasswordChange(password: String) {}

    override fun onSignIn(context: Context): LiveData<AuthResponseUseCase> {
        return MutableLiveData(null)
    }

    override fun onGoogleSignIn(context: Context): LiveData<AuthResponseUseCase> {
        return MutableLiveData(null)
    }

    override fun onSignUp(context: Context): LiveData<AuthResponseUseCase> {
        return MutableLiveData(null)
    }

    override fun onSignOut() {}
}
