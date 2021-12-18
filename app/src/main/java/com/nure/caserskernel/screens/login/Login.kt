package com.nure.caserskernel.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nure.caserskernel.Screen

@Composable
fun LoginContent(
    loginViewModel: LoginActivityViewModel = viewModel(),
    navController: NavController
) {
    val username = loginViewModel.username.observeAsState("")
    val password = loginViewModel.password.observeAsState("")
    val hasErrors = loginViewModel.hasErrors.observeAsState(false)
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = username.value,
            onValueChange = { loginViewModel.onUsernameChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Username") },
            singleLine = true
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = password.value,
            onValueChange = { loginViewModel.onPasswordChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        if (hasErrors.value) {
            Text(text = "Сталася помилка", color = Color.Red)
        }
        Spacer(Modifier.height(64.dp))
        Button(
            onClick = {
                loginViewModel.onLoginButtonClick()
                navController.navigate(route = Screen.MainScreen.route)
                      },
            modifier = Modifier.fillMaxWidth(0.75F),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 12.dp,
                end = 20.dp,
                bottom = 12.dp
            )
        ) {
            Text("УВІЙТИ")
        }
    }
}