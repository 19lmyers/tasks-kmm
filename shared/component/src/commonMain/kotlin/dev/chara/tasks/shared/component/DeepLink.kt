package dev.chara.tasks.shared.component

sealed interface DeepLink {
    object None : DeepLink

    data class ViewList(val id: String) : DeepLink

    data class ViewTask(val id: String) : DeepLink

    object CreateTask : DeepLink

    data class VerifyEmail(val token: String) : DeepLink

    data class ResetPassword(val token: String) : DeepLink
}
