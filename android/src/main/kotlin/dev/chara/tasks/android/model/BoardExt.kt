package dev.chara.tasks.android.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import dev.chara.tasks.model.board.BoardSection

val BoardSection.Type.icon: ImageVector
    get() = when (this) {
        BoardSection.Type.OVERDUE -> Icons.Filled.Schedule
        BoardSection.Type.STARRED -> Icons.Filled.Star
        BoardSection.Type.UPCOMING -> Icons.Filled.Event
    }