package io.github.jhdcruz.memo.data.auth

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import io.github.jhdcruz.memo.data.response.AuthResponse
import io.github.jhdcruz.memo.data.response.FirestoreResponse

interface AuthenticationRepository {
    suspend fun passwordSignIn(email: String, password: String): AuthResponse
    suspend fun googleSignIn(context: Context): AuthResponse

    suspend fun signIn(context: Context): AuthResponse
    suspend fun signUp(context: Context, email: String, password: String): AuthResponse
    suspend fun saveUser(user: FirebaseUser): FirestoreResponse
}
