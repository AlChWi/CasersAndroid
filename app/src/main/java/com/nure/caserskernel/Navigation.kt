package com.nure.caserskernel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nure.caserskernel.screens.carDetails.CarDetails
import com.nure.caserskernel.screens.cargoDeletion.CargoDeletion
import com.nure.caserskernel.screens.home.Home
import com.nure.caserskernel.screens.login.LoginContent
import com.nure.caserskernel.screens.profile.Profile
import com.nure.caserskernel.screens.textRecognition.TextRecognition

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
        composable(route = Screen.TextRecognition.route + "/{action}" + "/{vehType}" + "/{vehId}" + "/{itemId}") {
            TextRecognition(
                navController = navController,
                action = it.arguments?.getString("action") ?: "",
                vehType = it.arguments?.getString("vehType") ?: "",
                vehId = it.arguments?.getString("vehId") ?: "",
                itemId = it.arguments?.getString("itemId") ?: ""
            )
        }
        composable(route = Screen.CargoDeletion.route + "/{vehId}" + "/{itemId}") {
            CargoDeletion(
                navController = navController,
                vehId = it.arguments?.getString("vehId") ?: "",
                itemId = it.arguments?.getString("itemId") ?: ""
            )
        }
    }
}