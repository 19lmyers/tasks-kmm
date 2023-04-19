package dev.chara.tasks.model

class ProfileEditor(private val profile: Profile) {
    private var displayName = profile.displayName
    private var profilePhotoUri = profile.profilePhotoUri

    fun displayName(value: String) = apply {
        displayName = value
    }

    fun profilePhotoUri(value: String?) = apply {
        profilePhotoUri = value
    }

    fun build() = profile.copy(
        displayName = displayName,
        profilePhotoUri = profilePhotoUri
    )
}

fun Profile.edit() = ProfileEditor(this)