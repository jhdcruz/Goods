package io.github.jhdcruz.memo.domain.response

/**
 * Base class for returning response across classes and APIs.
 */
@Suppress("Unused")
sealed class ResponseUseCase {
    data object Success : ResponseUseCase()
    data object Failure : ResponseUseCase()
    data object Error : ResponseUseCase()
}
