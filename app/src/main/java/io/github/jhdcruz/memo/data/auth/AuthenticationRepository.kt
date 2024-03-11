package io.github.jhdcruz.memo.data.auth

import android.content.Context

interface AuthenticationRepository {
    suspend fun manualSignIn(email: String, password: String): Boolean
    suspend fun signIn(context: Context): Boolean
    suspend fun signUp(context: Context, email: String, password: String): Boolean
}
