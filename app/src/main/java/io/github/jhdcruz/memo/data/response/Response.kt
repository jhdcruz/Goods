package io.github.jhdcruz.memo.data.response

/**
 * Base class for returning response across classes and APIs.
 */
@Suppress("Unused")
sealed class Response {
    data object Success : Response()
    data object Failure : Response()
    data object Cancelled : Response()
    data object Error : Response()
}
