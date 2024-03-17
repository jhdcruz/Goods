package io.github.jhdcruz.memo.data.response

import com.google.firebase.firestore.FirebaseFirestoreException

/**
 * Base class for firebase's firestore operations.
 */
sealed class FirestoreResponse : Response() {
    data class Success(val result: Any? = null) : FirestoreResponse()
    data class Failure(val e: FirebaseFirestoreException) : FirestoreResponse()
    data class Error(val e: Throwable) : FirestoreResponse()
}
