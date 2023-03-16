package dev.chara.tasks.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpCredentials(val email: String, val displayName: String, val password: String)

@Serializable
data class LoginCredentials(val email: String, val password: String)

@Serializable
data class TokenPair(val accessToken: String, val refreshToken: String)

@Serializable
data class PasswordChange(val currentPassword: String, val newPassword: String)

@Serializable
data class PasswordReset(val resetToken: String, val newPassword: String)