package dev.chara.tasks.shared.model.preference

enum class Theme(private val friendlyName: String) {
    SYSTEM_DEFAULT("System default"),
    LIGHT("Light"),
    DARK("Dark");

    override fun toString(): String {
        return friendlyName
    }
}
