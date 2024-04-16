package io.github.jhdcruz.memo.ui.navigation

import androidx.annotation.DrawableRes
import io.github.jhdcruz.memo.R

interface RootDestination : Destination {
    @get:DrawableRes
    val activeIcon: Int

    @get:DrawableRes
    val inactiveIcon: Int
}

interface Destination {
    val route: String
    val title: String
}

object RootScreens {
    object Tasks : RootDestination {
        override val route = "tasks"
        override val title = "Tasks"
        override val activeIcon = R.drawable.baseline_tasks_filled_24
        override val inactiveIcon = R.drawable.baseline_tasks_24
    }

    object Calendar : RootDestination {
        override val route = "calendar"
        override val title = "Calendar"

        override val activeIcon = R.drawable.baseline_calendar_filled_24
        override val inactiveIcon = R.drawable.baseline_calendar_24
    }

    object Settings : RootDestination {
        override val route = "settings"
        override val title = "Settings"

        override val activeIcon = R.drawable.baseline_settings_24
        override val inactiveIcon = R.drawable.baseline_settings_filled_24
    }
}
