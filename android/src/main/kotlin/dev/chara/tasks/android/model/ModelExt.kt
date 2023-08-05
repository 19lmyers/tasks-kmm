package dev.chara.tasks.android.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import dev.chara.tasks.android.R
import dev.chara.tasks.shared.model.TaskList

val TaskList.Color.res: Int
    @ColorRes
    get() =
        when (this) {
            TaskList.Color.RED -> R.color.red
            TaskList.Color.ORANGE -> R.color.orange
            TaskList.Color.YELLOW -> R.color.yellow
            TaskList.Color.GREEN -> R.color.green
            TaskList.Color.BLUE -> R.color.blue
            TaskList.Color.PURPLE -> R.color.purple
            TaskList.Color.PINK -> R.color.pink
        }

val TaskList.Icon?.drawable: Int
    @DrawableRes
    get() =
        when (this) {
            TaskList.Icon.BACKPACK -> R.drawable.backpack_48px
            TaskList.Icon.BOOK -> R.drawable.book_48px
            TaskList.Icon.BOOKMARK -> R.drawable.bookmark_48px
            TaskList.Icon.BRUSH -> R.drawable.brush_48px
            TaskList.Icon.CAKE -> R.drawable.cake_48px
            TaskList.Icon.CALL -> R.drawable.call_48px
            TaskList.Icon.CAR -> R.drawable.directions_car_48px
            TaskList.Icon.CELEBRATION -> R.drawable.celebration_48px
            TaskList.Icon.CLIPBOARD -> R.drawable.content_paste_48px
            TaskList.Icon.FLIGHT -> R.drawable.flight_takeoff_48px
            TaskList.Icon.FOOD_BEVERAGE -> R.drawable.emoji_food_beverage_48px
            TaskList.Icon.FOOTBALL -> R.drawable.sports_football_48px
            TaskList.Icon.FOREST -> R.drawable.forest_48px
            TaskList.Icon.GROUP -> R.drawable.group_48px
            TaskList.Icon.HANDYMAN -> R.drawable.handyman_48px
            TaskList.Icon.HOME_REPAIR_SERVICE -> R.drawable.home_repair_service_48px
            TaskList.Icon.LIGHT_BULB -> R.drawable.lightbulb_48px
            TaskList.Icon.MEDICAL_SERVICES -> R.drawable.medical_services_48px
            TaskList.Icon.MUSIC_NOTE -> R.drawable.music_note_48px
            TaskList.Icon.PERSON -> R.drawable.person_48px
            TaskList.Icon.PETS -> R.drawable.pets_48px
            TaskList.Icon.PIANO -> R.drawable.piano_48px
            TaskList.Icon.RESTAURANT -> R.drawable.restaurant_48px
            TaskList.Icon.SCISSORS -> R.drawable.content_cut_48px
            TaskList.Icon.SHOPPING_CART -> R.drawable.shopping_cart_48px
            TaskList.Icon.SMILE -> R.drawable.mood_48px
            TaskList.Icon.WORK -> R.drawable.work_48px
            else -> R.drawable.checklist_48px
        }
