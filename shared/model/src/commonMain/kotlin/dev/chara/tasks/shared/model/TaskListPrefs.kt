package dev.chara.tasks.shared.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TaskListPrefs(
    val listId: String,
    val showIndexNumbers: Boolean = false,
    val sortType: SortType = SortType.ORDINAL,
    val sortDirection: SortDirection = SortDirection.ASCENDING,
    val ordinal: Int = -1,
    val lastModified: Instant = Clock.System.now()
) {

    @Serializable
    enum class SortType(private val friendlyName: String) {
        ORDINAL("My order"),
        LABEL("Label"),
        CATEGORY("Category"),
        DATE_CREATED("Date created"),
        UPCOMING("Upcoming"),
        STARRED("Starred recently");

        override fun toString(): String {
            return friendlyName
        }
    }

    @Serializable
    enum class SortDirection(private val friendlyName: String) {
        ASCENDING("ASC"),
        DESCENDING("DESC");

        override fun toString(): String {
            return friendlyName
        }
    }
}
