package com.nure.caserskernel.screens.carDetails

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nure.caserskernel.R
import com.nure.caserskernel.service.cars.VerifiedCar

@ExperimentalFoundationApi
@Composable
fun CarDetails(
    carID: String,
    carDetailsViewModel: CarDetailsViewModel = viewModel(),
    navController: NavController
) {
    carDetailsViewModel.configure(carID)
    carDetailsViewModel.onAppear()
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
        TitledGrid(carDetailsViewModel = carDetailsViewModel)
    }
}

@ExperimentalFoundationApi
@Composable
fun TitledGrid(
    carDetailsViewModel: CarDetailsViewModel
) {
    val carInfo = carDetailsViewModel.carInfo.observeAsState()
    when (carInfo.value) {
        is VerifiedCar -> {
            Surface {
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
                                title = carInfo.value!!.sign
                            )
                        }
                        item { Spacer(Modifier.width(0.dp)) }
                        items(carInfo.value!!.sealedCargo) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                                    .clickable {
                                        // TODO: Implement verification
                                    },
                                elevation = 8.dp
                            ) {
                                Box(
                                    Modifier.padding(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(it.wrapped.number)
                                        VerificationStatus(isVerified = it.isVerified)
                                    }
                                }
                            }
                        }
                        if(carInfo.value!!.sealedCargo.size % 2 != 0) {
                            item { Spacer(Modifier.width(0.dp)) }
                        }
                        carInfo.value!!.trailer?.let { trailer ->
                            item {
                                TitledRow(
                                    icon = painterResource(id = R.drawable.ic_car_trailer_filled),
                                    title = trailer.sign
                                )
                            }
                            item { Spacer(Modifier.width(0.dp)) }
                            items(trailer.sealedCargo) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            // TODO: Implement verification
                                            },
                                    elevation = 8.dp
                                ) {
                                    Box(
                                        Modifier.padding(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(it.wrapped.number)
                                            VerificationStatus(isVerified = it.isVerified)
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
        null -> CircularProgressIndicator()
    }
}

@Composable
fun TitledRow(
    icon: Painter,
    title: String
) {
    Row(
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