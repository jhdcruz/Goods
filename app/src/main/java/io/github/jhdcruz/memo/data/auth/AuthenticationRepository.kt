package io.github.jhdcruz.memo.data.auth

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import io.github.jhdcruz.memo.domain.response.AuthResponseUseCase
import io.github.jhdcruz.memo.domain.response.FirestoreResponseUseCase

interface AuthenticationRepository {
    suspend fun passwordSignIn(email: String, password: String): AuthResponseUseCase
    suspend fun googleSignIn(context: Context): AuthResponseUseCase

    suspend fun signIn(context: Context): AuthResponseUseCase
    suspend fun signUp(context: Context, email: String, password: String): AuthResponseUseCase
    suspend fun saveUser(user: FirebaseUser): FirestoreResponseUseCase
}
