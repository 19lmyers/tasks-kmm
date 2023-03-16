package dev.chara.tasks.android.model

import dev.chara.tasks.android.ui.route.home.HomeNavTarget
import dev.chara.tasks.model.StartScreen

val StartScreen.navTarget: HomeNavTarget
    get() = when (this) {
        StartScreen.BOARD -> HomeNavTarget.Board
        StartScreen.LISTS -> HomeNavTarget.Lists
        // StartScreen.SHARED -> HomeNavTarget.Shared
    }