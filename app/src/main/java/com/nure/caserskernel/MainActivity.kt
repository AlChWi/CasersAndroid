package com.nure.caserskernel

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
import com.nure.caserskernel.screens.login.LoginActivityViewModel
import com.nure.caserskernel.screens.login.LoginContent
import com.nure.caserskernel.ui.theme.CasersKernelTheme

class MainActivity: ComponentActivity() {
    val viewModel: LoginActivityViewModel by viewModels()

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContent {
            CasersKernelTheme {
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val token = sharedPref.getString("TOKEN_VALUE", null)
                val isAuthenticated = viewModel.isAuthenticated.observeAsState(initial = (token != null))
                if(!isAuthenticated.value) {
                    LoginContent(viewModel = viewModel)
                } else {
                    Navigation()
                }
            }
        }
    }
}