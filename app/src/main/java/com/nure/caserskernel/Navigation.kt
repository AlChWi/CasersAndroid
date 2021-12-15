package com.nure.caserskernel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nure.caserskernel.screens.carDetails.CarDetails
import com.nure.caserskernel.screens.home.Home

@ExperimentalFoundationApi
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.MainScreen.route
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
    }
}