package dev.chara.tasks.model.preference

enum class Theme(private val friendlyName: String) {
    SYSTEM_DEFAULT("System default"),
    LIGHT("Light"),
    DARK("Dark");

    override fun toString(): String {
        return friendlyName
    }
}