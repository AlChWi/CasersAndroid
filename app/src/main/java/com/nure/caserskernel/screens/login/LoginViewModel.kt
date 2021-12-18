package com.nure.caserskernel.screens.login

import android.app.Application
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nure.caserskernel.service.auth.AuthRepo
import com.nure.caserskernel.service.auth.AuthService
import com.nure.caserskernel.service.auth.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials

class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {
    val authRepo: AuthRepo by lazy {
        val authService = AuthService.shared
        AuthRepo(authService)
    }

    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _hasErrors = MutableLiveData(false)
    val hasErrors: LiveData<Boolean> = _hasErrors

    private val _isAuthenticated = MutableLiveData(false)
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    init {
        updateToken()
    }

    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }

    fun onLoginButtonClick() {
        GlobalScope.launch {
            val credentials = Credentials.basic(
                username.value ?: "",
                password.value ?: ""
            )
            val result = authRepo.login(credentials)
            withContext(Dispatchers.Main) {
                if(result.isSuccessful) {
                    _hasErrors.value = false
                    val token = result.body()
                    AuthService.setToken(token)
                    if(token != null) {
                        saveToken(token)
                    }
                } else {
                    _hasErrors.value = true
                }
            }
        }
    }

    private fun saveToken(token: Token) {
        val context = getApplication<Application>().applicationContext
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        with (sharedPref.edit()) {
            putString("TOKEN_VALUE", token.value)
            putString("USER_ID", token.userID)
            apply()
        }
    }

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
    }
}