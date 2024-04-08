package io.github.jhdcruz.memo.domain

import android.util.Base64
import java.security.SecureRandom
import kotlin.random.Random


/**
 * Generate nonces for HTTP/API requests
 */
fun generateNonce(size: Int): String {
    val nonce = ByteArray(size)
    SecureRandom().nextBytes(nonce)

    return Base64.encodeToString(nonce, Base64.DEFAULT)
}

/**
 * Generate a random hash
 * Use for non-intensive/security purposes
 */
fun generateHash(length: Int): String {
    val hash = ByteArray(length)
    Random.nextBytes(hash)

    return Base64.encodeToString(hash, Base64.DEFAULT)
}
