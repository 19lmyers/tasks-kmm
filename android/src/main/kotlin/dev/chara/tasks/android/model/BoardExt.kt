package dev.chara.tasks.android.model

import androidx.annotation.DrawableRes
import dev.chara.tasks.android.R
import dev.chara.tasks.shared.model.board.BoardSection

val BoardSection.Type.drawable: Int
    @DrawableRes
    get() =
        when (this) {
            BoardSection.Type.OVERDUE -> R.drawable.schedule_48px
            BoardSection.Type.STARRED -> R.drawable.star_48px
            BoardSection.Type.UPCOMING -> R.drawable.event_48px
        }
