package dev.chara.tasks.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val email: String,
    val emailVerified: Boolean = false,
    val displayName: String,
    val profilePhotoUri: String? = null,
)
