package com.nure.caserskernel.screens.home

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nure.caserskernel.R
import com.nure.caserskernel.Screen
import com.nure.caserskernel.service.cars.Car
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun Home(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Черга") },
                backgroundColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { /*TODO: navigate to profile*/ }) {
                        Image(rememberVectorPainter(image = Icons.Default.AccountBox), "")
                    }
                }
            )
        },
    ) {
        val items = homeViewModel.listItems.observeAsState(initial = listOf())
        homeViewModel.onAppear()
        Surface {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = 16.dp,
                    end = 12.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = items.value
                ) {
                    HomeScreenCard(
                        it,
                        onClick = { navController.navigate(Screen.CarDetailsScreen.withArgs(it.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreenCard(
    car: Car,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = 8.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column {
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val date = car.registeredAt.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                CarRow(
                    painterResource(id = R.drawable.ic_car_filled),
                    rowTitle = "Номер авто",
                    rowValue = car.sign,
                    trailingTitle = date.format(formatter)
                )
                car.trailer?.let {
                    Spacer(Modifier.height(10.dp))
                    CarRow(
                        painter = painterResource(id = R.drawable.ic_car_trailer_filled),
                        rowTitle = "Номер причепа",
                        rowValue = car.trailer.sign
                    )
                }
                Spacer(Modifier.height(10.dp))
                CarRow(
                    painter = rememberVectorPainter(image = Icons.Default.AccountCircle),
                    rowTitle = "Водій",
                    rowValue = car.driver.name
                )
            }
        }
    }
}

@Composable
fun CarRow(
    painter: Painter,
    rowTitle: String,
    rowValue: String,
    trailingTitle: String? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter,
            modifier = Modifier
                .height(22.dp)
                .width(22.dp),
            contentDescription = "",
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(10.dp))
        Text(
            rowTitle,
            fontSize = MaterialTheme.typography.subtitle2.fontSize,
            fontWeight = FontWeight.Medium
        )
        trailingTitle?.let {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                it,
                fontSize = 12.sp,
                fontWeight = FontWeight.Thin
            )
        }
    }
    Row {
        Text(rowValue)
    }
}