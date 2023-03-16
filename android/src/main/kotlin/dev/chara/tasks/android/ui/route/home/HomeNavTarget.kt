package dev.chara.tasks.android.ui.route.home

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.parcelize.Parcelize

sealed class HomeNavTarget : Parcelable {

    abstract val label: String
    abstract val icon: ImageVector

    @Parcelize
    object Board : HomeNavTarget() {
        override val label: String
            get() = "Dashboard"
        override val icon: ImageVector
            get() = Icons.Filled.Dashboard
    }

    @Parcelize
    object Lists : HomeNavTarget() {
        override val label: String
            get() = "Lists"
        override val icon: ImageVector
            get() = Icons.Filled.Checklist
    }

    @Parcelize
    object Shared : HomeNavTarget() {
        override val label: String
            get() = "Shared"
        override val icon: ImageVector
            get() = Icons.Filled.People
    }

}
