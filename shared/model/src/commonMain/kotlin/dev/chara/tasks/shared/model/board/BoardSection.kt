package dev.chara.tasks.shared.model.board

import dev.chara.tasks.shared.model.Task

data class BoardSection(val type: Type, val tasks: List<Task>) {
    enum class Type(val title: String) {
        OVERDUE(title = "Overdue"),
        STARRED(title = "Starred"),
        UPCOMING(title = "Upcoming"),
    }
}
