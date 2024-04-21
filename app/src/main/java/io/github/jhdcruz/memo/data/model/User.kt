package io.github.jhdcruz.memo.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String = "",
    val name: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
) : Parcelable
