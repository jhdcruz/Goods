package io.github.jhdcruz.memo.data.auth

import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInResult

interface AuthenticationRepository {
    suspend fun manualSignIn(email: String, password: String): Boolean
    suspend fun googleSignIn(context: Context, reqId: Int): BeginSignInResult

    suspend fun signIn(context: Context): Boolean
    suspend fun signUp(context: Context, email: String, password: String): Boolean
}
