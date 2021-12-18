package com.nure.caserskernel.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nure.caserskernel.Screen
import com.nure.caserskernel.screens.carDetails.CarDetailsContent

@Composable
fun Profile(
    profileViewModel: ProfileViewModel = viewModel(),
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профіль") },
                backgroundColor = Color.White,
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Image(imageVector = Icons.Default.Close, contentDescription = "")
                    }
                }
            )
        },
    ) {
        ProfileContent {
            profileViewModel.logOut()
            navController.popBackStack(route = Screen.Start.route, inclusive = false)
        }
    }
}

@Composable
fun ProfileContent(
    onLogOut: () -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onLogOut,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Вийти")
        }
    }
}