package dev.chara.tasks.shared.model.preference

enum class ThemeVariant(private val friendlyName: String) {
    MONOCHROME("Monochrome"),
    NEUTRAL("Neutral"),
    TONAL_SPOT("Tonal spot (default)"),
    VIBRANT("Vibrant"),
    EXPRESSIVE("Expressive"),
    FRUIT_SALAD("Fruit salad");

    override fun toString(): String {
        return friendlyName
    }
}
