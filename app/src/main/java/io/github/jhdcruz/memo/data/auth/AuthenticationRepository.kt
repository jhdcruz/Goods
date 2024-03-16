package io.github.jhdcruz.memo.data.auth

import android.content.Context

interface AuthenticationRepository {
    suspend fun passwordSignIn(email: String, password: String): Boolean
    suspend fun googleSignIn(context: Context): Boolean

    suspend fun signIn(context: Context): Boolean
    suspend fun signUp(context: Context, email: String, password: String): Boolean
}
