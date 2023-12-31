package dev.chara.tasks.shared.data.preference

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import dev.chara.tasks.shared.model.Profile
import dev.chara.tasks.shared.model.TokenPair
import dev.chara.tasks.shared.model.board.BoardSection
import dev.chara.tasks.shared.model.preference.Theme
import dev.chara.tasks.shared.model.preference.ThemeVariant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

expect class DataStorePath {
    fun get(fileName: String): String
}

class PreferenceDataSource(private val dataStorePath: DataStorePath) {

    private val dataStore =
        PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
            produceFile = { dataStorePath.get("tasks.preferences_pb").toPath() }
        )

    fun getUserProfile() =
        dataStore.data.map {
            val email = it[KEY_AUTH_USER_EMAIL] ?: return@map null

            Profile(
                id = it[KEY_AUTH_USER_ID] ?: "",
                email = email,
                emailVerified = it[KEY_AUTH_USER_EMAIL_VERIFIED] ?: false,
                displayName = it[KEY_AUTH_USER_DISPLAY_NAME] ?: "",
                profilePhotoUri = it[KEY_AUTH_USER_PROFILE_PHOTO_URI],
            )
        }

    suspend fun setUserProfile(profile: Profile) {
        dataStore.edit {
            it[KEY_AUTH_USER_ID] = profile.id
            it[KEY_AUTH_USER_EMAIL] = profile.email
            it[KEY_AUTH_USER_EMAIL_VERIFIED] = profile.emailVerified
            it[KEY_AUTH_USER_DISPLAY_NAME] = profile.displayName

            if (profile.profilePhotoUri.isNullOrBlank()) {
                it.remove(KEY_AUTH_USER_PROFILE_PHOTO_URI)
            } else {
                it[KEY_AUTH_USER_PROFILE_PHOTO_URI] = profile.profilePhotoUri!!
            }
        }
    }

    fun getApiTokens() =
        dataStore.data.map {
            if (it[KEY_API_ACCESS_TOKEN].isNullOrBlank()) return@map null
            if (it[KEY_API_REFRESH_TOKEN].isNullOrBlank()) return@map null

            TokenPair(
                it[KEY_API_ACCESS_TOKEN] ?: return@map null,
                it[KEY_API_REFRESH_TOKEN] ?: return@map null
            )
        }

    suspend fun setApiTokens(tokens: TokenPair) {
        dataStore.edit {
            it[KEY_API_ACCESS_TOKEN] = tokens.access
            it[KEY_API_REFRESH_TOKEN] = tokens.refresh
        }
    }

    suspend fun clearAuthFields() =
        dataStore.edit {
            it.remove(KEY_AUTH_USER_ID)
            it.remove(KEY_AUTH_USER_EMAIL)
            it.remove(KEY_AUTH_USER_EMAIL_VERIFIED)
            it.remove(KEY_AUTH_USER_DISPLAY_NAME)
            it.remove(KEY_AUTH_USER_PROFILE_PHOTO_URI)

            it.remove(KEY_API_ACCESS_TOKEN)
            it.remove(KEY_API_REFRESH_TOKEN)
        }

    fun getAppTheme() =
        dataStore.data.map { Theme.valueOf(it[KEY_APP_THEME] ?: Theme.SYSTEM_DEFAULT.name) }

    suspend fun setAppTheme(theme: Theme) {
        dataStore.edit { it[KEY_APP_THEME] = theme.name }
    }

    fun getAppThemeVariant() =
        dataStore.data.map {
            ThemeVariant.valueOf(it[KEY_APP_THEME_VARIANT] ?: ThemeVariant.TONAL_SPOT.name)
        }

    suspend fun setAppThemeVariant(variant: ThemeVariant) {
        dataStore.edit { it[KEY_APP_THEME_VARIANT] = variant.name }
    }

    /**
     * This is the reverse of the Repository. Why? So newly added sections are enabled by default!
     */
    fun getDisabledBoardSections() =
        dataStore.data.map { it[KEY_BOARD_DISABLED_SECTIONS] ?: emptySet() }

    suspend fun setDisabledForBoardSection(boardSection: BoardSection.Type, disabled: Boolean) {
        dataStore.edit {
            val mutableSet = it[KEY_BOARD_DISABLED_SECTIONS]?.toMutableSet() ?: mutableSetOf()

            if (disabled) {
                mutableSet.add(boardSection.name)
            } else {
                mutableSet.remove(boardSection.name)
            }

            it[KEY_BOARD_DISABLED_SECTIONS] = mutableSet
        }
    }

    companion object {
        private val KEY_AUTH_USER_ID = stringPreferencesKey("auth_user_id")
        private val KEY_AUTH_USER_EMAIL = stringPreferencesKey("auth_user_email")
        private val KEY_AUTH_USER_EMAIL_VERIFIED = booleanPreferencesKey("auth_user_email_verified")
        private val KEY_AUTH_USER_DISPLAY_NAME = stringPreferencesKey("auth_user_display_name")
        private val KEY_AUTH_USER_PROFILE_PHOTO_URI =
            stringPreferencesKey("auth_user_profile_photo_uri")

        private val KEY_API_ACCESS_TOKEN = stringPreferencesKey("api_access_token")
        private val KEY_API_REFRESH_TOKEN = stringPreferencesKey("api_refresh_token")

        private val KEY_APP_THEME = stringPreferencesKey("app_theme")
        private val KEY_APP_THEME_VARIANT = stringPreferencesKey("app_theme_variant")

        private val KEY_BOARD_DISABLED_SECTIONS = stringSetPreferencesKey("board_disabled_sections")
    }
}
