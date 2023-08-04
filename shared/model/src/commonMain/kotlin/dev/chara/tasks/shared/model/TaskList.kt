package dev.chara.tasks.shared.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TaskList(
    val id: String,

    val title: String,

    val color: Color? = null,
    val icon: Icon? = null,
    val description: String? = null,

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
        SCISSORS,
        SHOPPING_CART,
        SMILE,
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