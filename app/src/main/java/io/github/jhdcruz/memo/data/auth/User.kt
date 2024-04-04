package io.github.jhdcruz.memo.data.auth

data class User(
    val uid: String = "",
    val name: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
)
