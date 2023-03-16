package dev.chara.tasks.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName
import dev.chara.tasks.data.cache.sql.Task as CacheTask
import dev.chara.tasks.data.cache.sql.TaskList as CacheTaskList

@Serializable
data class Profile(
    val email: String,
    val displayName: String,
    val profilePhotoUri: String? = null,
)

@Serializable
data class TaskList(
    val id: String,

    val title: String,

    val color: Color? = null,
    val icon: Icon? = null,
    val description: String? = null,

    val isPinned: Boolean = false,
    val showIndexNumbers: Boolean = false,

    val sortType: SortType = SortType.ORDINAL,
    val sortDirection: SortDirection = SortDirection.ASCENDING,

    val dateCreated: Instant = Clock.System.now(),
    val lastModified: Instant = Clock.System.now()
) {
    @Serializable
    enum class Color {
        RED,
        ORANGE,
        YELLOW,
        GREEN,
        BLUE,
        PURPLE,
        PINK,
    }

    @Serializable
    enum class Icon {
        BACKPACK,
        BOOK,
        BOOKMARK,
        BRUSH,
        CAKE,
        CALL,
        CAR,
        CELEBRATION,
        CLIPBOARD,
        FLIGHT,
        FOOD_BEVERAGE,
        FOOTBALL,
        FOREST,
        GROUP,
        HANDYMAN,
        HOME_REPAIR_SERVICE,
        LIGHT_BULB,
        MEDICAL_SERVICES,
        MUSIC_NOTE,
        PERSON,
        PETS,
        PIANO,
        RESTAURANT,
        ROCKET_LAUNCH,
        SCISSORS,
        SHOPPING_CART,
        SMILE,
        STORE,
        WORK
    }

    @Serializable
    enum class SortType(private val friendlyName: String) {
        ORDINAL("My order"),
        LABEL("Label"),
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

@JvmName("toTaskListModel")
fun Collection<CacheTaskList>.toModel() = map { it.toModel() }
fun CacheTaskList.toModel() = TaskList(
    this.id,

    this.title,

    this.color,
    this.icon,
    this.description,

    this.is_pinned,
    this.show_index_numbers,

    this.sort_type,
    this.sort_direction,

    this.date_created,
    this.last_modified
)

@Serializable
data class Task(
    val id: String,
    val listId: String,

    val label: String,
    val isCompleted: Boolean = false,
    val isStarred: Boolean = false,

    val details: String? = null,
    val reminderDate: Instant? = null,
    val dueDate: Instant? = null,

    val dateCreated: Instant = Clock.System.now(),
    val lastModified: Instant = Clock.System.now(),

    val ordinal: Int = -1
)

@JvmName("toTaskModel")
fun Collection<CacheTask>.toModel() = map { it.toModel() }
fun CacheTask.toModel() = Task(
    this.id,
    this.list_id,

    this.label,
    this.is_completed,
    this.is_starred,

    this.details,
    this.reminder_date,
    this.due_date,

    this.date_created,
    this.last_modified,

    this.ordinal.toInt()
)
