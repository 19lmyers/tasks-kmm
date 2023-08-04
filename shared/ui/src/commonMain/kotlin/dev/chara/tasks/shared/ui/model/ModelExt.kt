package dev.chara.tasks.shared.ui.model

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
import androidx.compose.ui.graphics.vector.ImageVector
import dev.chara.tasks.shared.model.TaskList
val TaskList.Color.seed: Color
    get() = when (this) {
        TaskList.Color.RED -> Color(0xFFAC322C)
        TaskList.Color.ORANGE -> Color(0xFF974800)
        TaskList.Color.YELLOW -> Color(0xFF7C5800)
        TaskList.Color.GREEN -> Color(0xFF356A22)
        TaskList.Color.BLUE -> Color(0xFF00639D)
        TaskList.Color.PURPLE -> Color(0xFF6750A4)
        TaskList.Color.PINK -> Color(0xFF95416E)
    }

val TaskList.Icon?.icon: ImageVector
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