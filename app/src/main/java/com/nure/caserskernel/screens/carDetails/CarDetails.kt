package com.nure.caserskernel.screens.carDetails

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nure.caserskernel.R
import com.nure.caserskernel.Screen
import com.nure.caserskernel.service.cars.VerifiedCar
import com.nure.caserskernel.service.cars.VerifiedSealedCargo

@ExperimentalFoundationApi
@Composable
fun CarDetails(
    carID: String,
    carDetailsViewModel: CarDetailsViewModel = viewModel(),
    navController: NavController
) {
    carDetailsViewModel.onAppear(carID)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Деталі") },
                backgroundColor = Color.White,
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Image(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                }
            )
        },
    ) {
        CarDetailsContent(
            carDetailsViewModel = carDetailsViewModel,
            onDepartCar = { navController.popBackStack() },
            navController = navController
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun CarDetailsContent(
    carDetailsViewModel: CarDetailsViewModel,
    onDepartCar: (String) -> Unit,
    navController: NavController
) {
    val carInfo = carDetailsViewModel.carInfo.observeAsState()
    val carInfoValue = carInfo.value
    if (carInfoValue != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            TitledGrid(
                carInfo = carInfoValue,
                onClick = { navController.navigate(Screen.TextRecognition.withArgs("verify", "any", carInfoValue.id, it)) },
                onDelete = { carDetailsViewModel.delete(it) },
                navController = navController
            )
            Button(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomCenter),
                enabled = false,
                onClick = {
                    carDetailsViewModel.departCar()
                    onDepartCar(carInfoValue.id)
                }
            ) {
                Text("Дозволити відправлення")
            }
        }
    } else {
        CircularProgressIndicator()
    }
}

@ExperimentalFoundationApi
@Composable
fun TitledGrid(
    carInfo: VerifiedCar,
    onClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    navController: NavController
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 16.dp,
                end = 12.dp,
                bottom = 16.dp
            ),
            content = {
                item {
                    TitledRow(
                        icon = painterResource(R.drawable.ic_car_filled),
                        title = carInfo.sign
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = {
                            navController.navigate(Screen.TextRecognition.withArgs("add", "car", carInfo.id, "-"))
                        }) {
                            Image(Icons.Default.Add, "")
                        }
                    }
                }
                items(carInfo.sealedCargo) {
                    CargoCard(
                        cargo = it,
                        onClick = onClick,
                        onDelete = onDelete
                    )
                }
                if(carInfo.sealedCargo.size % 2 != 0) {
                    item { Spacer(Modifier.width(0.dp)) }
                }
                carInfo.trailer?.let { trailer ->
                    item {
                        TitledRow(
                            icon = painterResource(id = R.drawable.ic_car_trailer_filled),
                            title = trailer.sign
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.End) {
                            IconButton(onClick = {
                                navController.navigate(Screen.TextRecognition.withArgs("add", "trailer", carInfo.id, trailer.id))
                            }) {
                                Image(Icons.Default.Add, "")
                            }
                        }
                    }
                    items(trailer.sealedCargo) {
                        CargoCard(
                            cargo = it,
                            onClick = onClick,
                            onDelete = onDelete
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun TitledRow(
    icon: Painter,
    title: String
) {
    Row(
        Modifier.height(45.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            icon,
            modifier = Modifier
                .height(26.dp)
                .width(26.dp),
            contentDescription = "",
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(10.dp))
        Text(
            title,
            fontSize = MaterialTheme.typography.subtitle1.fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun VerificationStatus(
    isVerified: Boolean
) {
    val text: String = if(isVerified) "Перевірено" else "Не перевірено"
    val color: Color = if(isVerified) Color.Green else Color.Red
    Text(
        text,
        color = color
    )
}

@Composable
fun CargoCard(
    cargo: VerifiedSealedCargo,
    onClick: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }
    if (openDialog.value) {
        DeleteAlertDialog(
            onDismiss = { openDialog.value = false },
            onConfirm = { onDelete(cargo.wrapped.number) }
        )
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                onClick(cargo.wrapped.number)
            },
        elevation = 8.dp
    ) {
        Box(
            Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(cargo.wrapped.number)
                    VerificationStatus(isVerified = cargo.isVerified)
                }
                Spacer(Modifier.width(12.dp))
                IconButton(
                    onClick = {
                        openDialog.value = true
                    }
                ) {
                    Image(
                        imageVector = Icons.Default.Delete,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Видалення ЗПУ")
        },
        text = {
            Text("Ви впевнені, що хочете видалите це ЗПУ зі списку?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("Видалити")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Скасувати")
            }
        }
    )
}