package dev.chara.tasks.model

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

data class PinnedList(
    val taskList: TaskList,
    val topTasks: List<Task>,
    val totalTaskCount: Int
)
