package com.nure.caserskernel

sealed class Screen(val route: String) {
    object MainScreen: Screen("Home")
    object CarDetailsScreen: Screen("CarDetails")
    object Profile: Screen("Profile")
    object Start: Screen("Start")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}
