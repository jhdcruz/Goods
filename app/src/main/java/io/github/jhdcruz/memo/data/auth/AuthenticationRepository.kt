package io.github.jhdcruz.memo.data.auth

import android.content.Context

interface AuthenticationRepository {
    suspend fun signIn(context: Context, email: String, password: String): Boolean
    suspend fun signUp(email: String, password: String): Boolean
}
