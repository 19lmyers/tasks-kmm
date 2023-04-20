package dev.chara.tasks.model.board

import dev.chara.tasks.model.Task

data class BoardSection(
    val type: Type,
    val tasks: List<Task>
) {
    enum class Type(val title: String) {
        OVERDUE(title = "Overdue"),
        STARRED(title = "Starred"),
        UPCOMING(title = "Upcoming"),
    }
}

