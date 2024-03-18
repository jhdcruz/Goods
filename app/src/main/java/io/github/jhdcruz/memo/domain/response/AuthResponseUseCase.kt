package io.github.jhdcruz.memo.domain.response

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser

/**
 * Base class for returning authentication response.
 */
sealed class AuthResponseUseCase : ResponseUseCase() {

    data class Success(val user: FirebaseUser? = null) : AuthResponseUseCase()
    data class Failure(val exception: FirebaseAuthException) : AuthResponseUseCase()

    data class Cancelled(val message: String) : AuthResponseUseCase()
    data class Error(val exception: Throwable) : AuthResponseUseCase()

    data class Invalid(val message: String) : AuthResponseUseCase()
    data class NotFound(val message: String) : AuthResponseUseCase()
}
