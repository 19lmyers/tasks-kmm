package dev.chara.tasks.android.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.SportsFootball
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import dev.chara.tasks.android.R
import dev.chara.tasks.model.TaskList
import material_color_utilities.hct.Hct

val TaskList.Color.res: Int
    @ColorRes
    get() = when (this) {
        TaskList.Color.RED -> R.color.red
        TaskList.Color.ORANGE -> R.color.orange
        TaskList.Color.YELLOW -> R.color.yellow
        TaskList.Color.GREEN -> R.color.green
        TaskList.Color.BLUE -> R.color.blue
        TaskList.Color.PURPLE -> R.color.purple
        TaskList.Color.PINK -> R.color.pink
    }

val TaskList.Color.hct: Hct
    get() = when (this) {
        TaskList.Color.RED -> Hct.fromInt(Color(0xFFAC322C).toArgb())
        TaskList.Color.ORANGE -> Hct.fromInt(Color(0xFF974800).toArgb())
        TaskList.Color.YELLOW -> Hct.fromInt(Color(0xFF7C5800).toArgb())
        TaskList.Color.GREEN -> Hct.fromInt(Color(0xFF356A22).toArgb())
        TaskList.Color.BLUE -> Hct.fromInt(Color(0xFF00639D).toArgb())
        TaskList.Color.PURPLE -> Hct.fromInt(Color(0xFF6750A4).toArgb())
        TaskList.Color.PINK -> Hct.fromInt(Color(0xFF95416E).toArgb())
    }

val TaskList.Icon?.vector: ImageVector
    get() = when (this) {
        TaskList.Icon.BACKPACK -> Icons.Filled.Backpack
        TaskList.Icon.BOOK -> Icons.Filled.Book
        TaskList.Icon.BOOKMARK -> Icons.Filled.Bookmark
        TaskList.Icon.BRUSH -> Icons.Filled.Brush
        TaskList.Icon.CAKE -> Icons.Filled.Cake
        TaskList.Icon.CALL -> Icons.Filled.Call
        TaskList.Icon.CAR -> Icons.Filled.DirectionsCar
        TaskList.Icon.CELEBRATION -> Icons.Filled.Celebration
        TaskList.Icon.CLIPBOARD -> Icons.Filled.ContentPaste
        TaskList.Icon.FLIGHT -> Icons.Filled.FlightTakeoff
        TaskList.Icon.FOOD_BEVERAGE -> Icons.Filled.EmojiFoodBeverage
        TaskList.Icon.FOOTBALL -> Icons.Filled.SportsFootball
        TaskList.Icon.FOREST -> Icons.Filled.Forest
        TaskList.Icon.GROUP -> Icons.Filled.Group
        TaskList.Icon.HANDYMAN -> Icons.Filled.Handyman
        TaskList.Icon.HOME_REPAIR_SERVICE -> Icons.Filled.HomeRepairService
        TaskList.Icon.LIGHT_BULB -> Icons.Filled.Lightbulb
        TaskList.Icon.MEDICAL_SERVICES -> Icons.Filled.MedicalServices
        TaskList.Icon.MUSIC_NOTE -> Icons.Filled.MusicNote
        TaskList.Icon.PERSON -> Icons.Filled.Person
        TaskList.Icon.PETS -> Icons.Filled.Pets
        TaskList.Icon.PIANO -> Icons.Filled.Piano
        TaskList.Icon.RESTAURANT -> Icons.Filled.Restaurant
        TaskList.Icon.SCISSORS -> Icons.Filled.ContentCut
        TaskList.Icon.SHOPPING_CART -> Icons.Filled.ShoppingCart
        TaskList.Icon.SMILE -> Icons.Filled.Mood
        TaskList.Icon.WORK -> Icons.Filled.Work
        else -> Icons.Filled.Checklist
    }

val TaskList.Icon?.drawable: Int
    @DrawableRes
    get() = when (this) {
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

val TaskList.SortType.icon: ImageVector
    get() = when (this) {
        TaskList.SortType.ORDINAL -> Icons.Filled.Sort
        TaskList.SortType.LABEL -> Icons.Filled.SortByAlpha
        TaskList.SortType.DATE_CREATED -> Icons.Filled.Event
        TaskList.SortType.UPCOMING -> Icons.Filled.Schedule
        TaskList.SortType.STARRED -> Icons.Filled.Star
    }

val TaskList.SortDirection.icon: ImageVector
    get() = when (this) {
        TaskList.SortDirection.ASCENDING -> Icons.Filled.ArrowUpward
        TaskList.SortDirection.DESCENDING -> Icons.Filled.ArrowDownward
    }