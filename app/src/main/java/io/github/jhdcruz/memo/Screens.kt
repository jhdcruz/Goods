package io.github.jhdcruz.memo

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface Destination {
    val route: String
    val title: String
}


object TaskListDestination : Destination {
    override val route = "product_list"
    override val title = "Product List"
}

object TaskDetailsDestination : Destination {
    override val route = "product_details"
    override val title = "Product Details"

    const val taskId = "product_id"
    val args = listOf(navArgument(name = taskId) {
        type = NavType.StringType
    })

    fun createRouteWithParam(taskId: String) = "$route/${taskId}"
}

object CalendarDestination : Destination {
    override val route = "add_product"
    override val title = "Add Product"
}

object AuthenticationDestination : Destination {
    override val route = "authentication"
    override val title = "Authentication"
}

object SignUpDestination : Destination {
    override val route = "signup"
    override val title = "Sign Up"
}
