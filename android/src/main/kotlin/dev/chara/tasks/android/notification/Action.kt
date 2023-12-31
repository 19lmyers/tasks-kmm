package dev.chara.tasks.android.notification

enum class Action {
    ADD_TASK,
    EDIT_TASK,
    REMOVE_TASK,
    COMPLETE_TASK,
    STAR_TASK,
    PREDICT_TASK_CATEGORY,
    CLEAR_COMPLETED_TASKS,
    JOIN_LIST;

    fun getFriendlyName(taskLabel: String?, actorName: String?): String {
        return when (this) {
            ADD_TASK -> "$taskLabel"
            EDIT_TASK -> "$taskLabel"
            REMOVE_TASK -> "$taskLabel"
            COMPLETE_TASK -> "$taskLabel"
            STAR_TASK -> "$taskLabel"
            PREDICT_TASK_CATEGORY -> "Category assigned"
            CLEAR_COMPLETED_TASKS -> "Completed tasks cleared"
            JOIN_LIST -> "$actorName joined"
        }
    }
}
