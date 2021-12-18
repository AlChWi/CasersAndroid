package com.nure.caserskernel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nure.caserskernel.screens.carDetails.CarDetails
import com.nure.caserskernel.screens.home.Home
import com.nure.caserskernel.screens.login.LoginContent
import com.nure.caserskernel.screens.profile.Profile

@ExperimentalFoundationApi
@Composable
fun Navigation(
    startDestination: String = Screen.Start.route,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.MainScreen.route) {
            Home(navController = navController)
        }
        composable(route = Screen.CarDetailsScreen.route + "/{name}") {
            CarDetails(
                carID = it.arguments?.getString("name") ?: "",
                navController = navController
            )
        }
        composable(route = Screen.Profile.route) {
            Profile(navController = navController)
        }
        composable(route = Screen.Start.route) {
            AppContent(navController = navController)
        }
    }
}