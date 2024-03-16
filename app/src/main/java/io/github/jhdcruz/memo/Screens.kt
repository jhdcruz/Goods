package io.github.jhdcruz.memo

import androidx.annotation.DrawableRes
import androidx.navigation.NavType
import androidx.navigation.navArgument

interface Destination {
    val route: String
    val title: String
}

interface RootDestination : Destination {
    @get:DrawableRes
    val activeIcon: Int

    @get:DrawableRes
    val inactiveIcon: Int
}

// TASKS
object TasksDestination : RootDestination {
    override val route = "tasks"
    override val title = "Tasks"
    override val activeIcon = R.drawable.baseline_tasks_filled_24
    override val inactiveIcon = R.drawable.baseline_tasks_24
}

object TaskDetailsDestination : Destination {
    override val route = "task_details"
    override val title = "Task Details"

    const val taskId = "id"

    val args = listOf(navArgument(name = taskId) {
        type = NavType.StringType
    })

    fun createRouteWithParam(taskId: String) = "$route/${taskId}"
}

// CALENDAR
object CalendarDestination : RootDestination {
    override val route = "calendar"
    override val title = "Calendar"

    override val activeIcon = R.drawable.baseline_calendar_filled_24
    override val inactiveIcon = R.drawable.baseline_calendar_24
}
