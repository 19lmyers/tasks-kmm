package dev.chara.tasks.data.preference

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import dev.chara.tasks.model.BoardSection
import dev.chara.tasks.model.Profile
import dev.chara.tasks.model.Theme
import dev.chara.tasks.model.TokenPair
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

expect class DataStorePath {
    fun get(fileName: String): String
}

class PreferenceDataSource(private val dataStorePath: DataStorePath) {

    private val dataStore = PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = null,
        migrations = listOf(ProfileMigration()),
        scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        produceFile = {
            dataStorePath.get("tasks.preferences_pb").toPath()
        }
    )

    fun getUserProfile() = dataStore.data.map {
        val email = it[KEY_AUTH_USER_EMAIL] ?: return@map null

        Profile(
            email = email,
            displayName = it[KEY_AUTH_USER_DISPLAY_NAME] ?: "",
            profilePhotoUri = it[KEY_AUTH_USER_PROFILE_PHOTO_URI],
        )
    }

    suspend fun setUserProfile(profile: Profile) {
        dataStore.edit {
            it[KEY_AUTH_USER_EMAIL] = profile.email
            it[KEY_AUTH_USER_DISPLAY_NAME] = profile.displayName

            if (profile.profilePhotoUri.isNullOrBlank()) {
                it.remove(KEY_AUTH_USER_PROFILE_PHOTO_URI)
            } else {
                it[KEY_AUTH_USER_PROFILE_PHOTO_URI] = profile.profilePhotoUri
            }
        }
    }

    fun getApiTokens() = dataStore.data.map {
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

    suspend fun clearAuthFields() = dataStore.edit {
        // TODO do we need to empty the strings first

        it.remove(KEY_AUTH_USER_EMAIL)
        it.remove(KEY_AUTH_USER_DISPLAY_NAME)
        it.remove(KEY_AUTH_USER_PROFILE_PHOTO_URI)

        it.remove(KEY_API_ACCESS_TOKEN)
        it.remove(KEY_API_REFRESH_TOKEN)
    }

    fun getAppTheme() = dataStore.data.map {
        Theme.valueOf(it[KEY_APP_THEME] ?: Theme.SYSTEM_DEFAULT.name)
    }

    suspend fun setAppTheme(theme: Theme) {
        dataStore.edit {
            it[KEY_APP_THEME] = theme.name
        }
    }

    fun useVibrantColors() = dataStore.data.map {
        it[KEY_USE_VIBRANT_COLORS] ?: false
    }

    suspend fun setVibrantColors(useVibrantColors: Boolean) {
        dataStore.edit {
            it[KEY_USE_VIBRANT_COLORS] = useVibrantColors
        }
    }

    /**
     * This is the reverse of the Repository.
     * Why? So newly added sections are enabled by default!
     */
    fun getDisabledBoardSections() = dataStore.data.map {
        it[KEY_BOARD_DISABLED_SECTIONS] ?: emptySet()
    }

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

    @Suppress("DEPRECATION")
    class ProfileMigration : DataMigration<Preferences> {
        override suspend fun shouldMigrate(currentData: Preferences): Boolean {
            return currentData.contains(KEY_AUTH_USER_ID)
        }

        override suspend fun migrate(currentData: Preferences): Preferences {
            val preferences = currentData.toMutablePreferences()

            val userId = preferences[KEY_AUTH_USER_ID]

            if (userId != null) {
                preferences[KEY_AUTH_USER_EMAIL] = userId
            }

            return preferences.toPreferences()
        }

        override suspend fun cleanUp() {}
    }

    companion object {
        @Deprecated("Replaced with Profile composite")
        private val KEY_AUTH_USER_ID = stringPreferencesKey("auth_user_id")

        private val KEY_AUTH_USER_EMAIL = stringPreferencesKey("auth_user_email")
        private val KEY_AUTH_USER_DISPLAY_NAME = stringPreferencesKey("auth_user_display_name")
        private val KEY_AUTH_USER_PROFILE_PHOTO_URI =
            stringPreferencesKey("auth_user_profile_photo_uri")

        private val KEY_API_ACCESS_TOKEN = stringPreferencesKey("api_access_token")
        private val KEY_API_REFRESH_TOKEN = stringPreferencesKey("api_refresh_token")

        private val KEY_APP_THEME = stringPreferencesKey("app_theme")
        private val KEY_USE_VIBRANT_COLORS = booleanPreferencesKey("use_vibrant_colors")

        private val KEY_START_SCREEN = stringPreferencesKey("start_screen")

        private val KEY_BOARD_DISABLED_SECTIONS = stringSetPreferencesKey("board_disabled_sections")
    }
}