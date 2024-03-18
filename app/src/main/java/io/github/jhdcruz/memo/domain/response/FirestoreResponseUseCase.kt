package io.github.jhdcruz.memo.domain.response

import com.google.firebase.firestore.FirebaseFirestoreException

/**
 * Base class for firebase's firestore operations.
 */
sealed class FirestoreResponseUseCase : ResponseUseCase() {
    data class Success(val result: Any? = null) : FirestoreResponseUseCase()
    data class Failure(val e: FirebaseFirestoreException) : FirestoreResponseUseCase()
    data class Error(val e: Throwable) : FirestoreResponseUseCase()
}
