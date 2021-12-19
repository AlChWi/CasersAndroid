package com.nure.caserskernel.screens.cargoDeletion

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nure.caserskernel.R
import com.nure.caserskernel.screens.carDetails.CarDetailsViewModel

@Composable
fun CargoDeletion(
    carDetailsViewModel: CarDetailsViewModel = viewModel(),
    navController: NavController,
    vehId: String,
    itemId: String
) {
    carDetailsViewModel.onAppear(vehId)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Видалення ЗПУ") },
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
        MainContent(itemId = itemId) {
            carDetailsViewModel.delete(itemId)
            navController.popBackStack()
        }
    }
}

@Composable
fun MainContent(itemId: String,
    textRecognitionCompletion: () -> Unit) {
    val extractedText = remember { mutableStateOf("") }
    Column(
        Modifier.fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(16.dp))
        Image(painterResource(R.drawable.trash), "trash")
        Spacer(Modifier.height(16.dp))
        Text(
            text = StringBuilder().append("Ви впевненi, що хочете видалити цей(").append(itemId).append(") ЗПУ?").toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { textRecognitionCompletion() }) {
            Text("Пiдтвердити видалення",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.height(32.dp))
        }
        Spacer(Modifier.height(16.dp))
    }
}