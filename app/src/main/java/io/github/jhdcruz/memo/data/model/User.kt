package io.github.jhdcruz.memo.data.model

import java.io.Serializable

data class User(
    val uid: String = "",
    val name: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
) : Serializable
