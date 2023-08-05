package dev.chara.tasks.shared.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.board.BoardSection

val BoardSection.Type.color: Color
    get() =
        when (this) {
            BoardSection.Type.OVERDUE -> TaskList.Color.RED.seed
            BoardSection.Type.STARRED -> TaskList.Color.YELLOW.seed
            BoardSection.Type.UPCOMING -> TaskList.Color.BLUE.seed
        }

val BoardSection.Type.icon: ImageVector
    get() =
        when (this) {
            BoardSection.Type.OVERDUE -> Icons.Filled.Schedule
            BoardSection.Type.STARRED -> Icons.Filled.Star
            BoardSection.Type.UPCOMING -> Icons.Filled.Event
        }
