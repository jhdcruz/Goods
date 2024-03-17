package io.github.jhdcruz.memo.data.response

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser

/**
 * Base class for returning authentication response.
 */
sealed class AuthResponse : Response() {

    data class Success(val user: FirebaseUser? = null) : AuthResponse()
    data class Failure(val exception: FirebaseAuthException) : AuthResponse()

    data class Cancelled(val message: String) : AuthResponse()
    data class Error(val exception: Throwable) : AuthResponse()

    data class Invalid(val message: String) : AuthResponse()
    data class NotFound(val message: String) : AuthResponse()
}
