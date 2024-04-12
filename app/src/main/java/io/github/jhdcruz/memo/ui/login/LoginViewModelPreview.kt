package io.github.jhdcruz.memo.ui.login

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class LoginViewModelPreview : LoginViewModel() {
    override val email: Flow<String> = MutableStateFlow("")
    override val password: Flow<String> = MutableStateFlow("")
    override val status: Flow<String> = MutableStateFlow("")

    override fun onEmailChange(email: String) {}

    override fun onPasswordChange(password: String) {}

    override fun onSignIn(context: Context) {}

    override fun onGoogleSignIn(context: Context) {}

    override fun onSignUp(context: Context, email: String, password: String) {}

    override fun onSignOut() {}
}
