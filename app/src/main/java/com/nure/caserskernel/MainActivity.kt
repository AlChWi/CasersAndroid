package com.nure.caserskernel

import android.app.Application
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nure.caserskernel.screens.home.Home
import com.nure.caserskernel.screens.login.LoginActivityViewModel
import com.nure.caserskernel.screens.login.LoginContent
import com.nure.caserskernel.service.auth.AuthService
import com.nure.caserskernel.service.auth.Token
import com.nure.caserskernel.ui.theme.CasersKernelTheme

class MainActivity: ComponentActivity() {
    val viewModel: LoginActivityViewModel by viewModels()

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContent {
            CasersKernelTheme {
                val navController = rememberNavController()
                Navigation(navController = navController)
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun AppContent(navController: NavHostController) {
    val startViewModel: MainActivityViewModel = viewModel()
    val isAuthenticated = startViewModel.isAuthenticated.observeAsState(initial = true)
    startViewModel.updateToken()
    if(!isAuthenticated.value) {
        LoginContent(navController = navController)
    } else {
        Home(navController = navController)
    }
}

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val _isAuthenticated = MutableLiveData(true)
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    fun updateToken() {
        val context = getApplication<Application>().applicationContext
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val token = sharedPref.getString("TOKEN_VALUE", null)
        val userID = sharedPref.getString("USER_ID", null)
        if(token != null && userID != null) {
            val token = Token(token, userID)
            AuthService.setToken(token)
            _isAuthenticated.value = true
        } else {
            AuthService.setToken(null)
        }
        AuthService.onChangeToken = {
            if (token != null && userID != null) {
                _isAuthenticated.value = true
            } else {
                _isAuthenticated.value = false
            }
        }
    }
}