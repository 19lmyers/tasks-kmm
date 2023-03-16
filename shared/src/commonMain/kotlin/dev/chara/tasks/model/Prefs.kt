package dev.chara.tasks.model

enum class Theme(private val friendlyName: String) {
    SYSTEM_DEFAULT("System default"),
    LIGHT("Light"),
    DARK("Dark");

    override fun toString(): String {
        return friendlyName
    }
}

enum class StartScreen(private val friendlyName: String) {
    BOARD("Dashboard"),
    LISTS("Lists");
    //SHARED("Shared");

    override fun toString(): String {
        return friendlyName
    }
}