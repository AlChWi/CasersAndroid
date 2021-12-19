package com.nure.caserskernel.screens.textRecognition

import android.app.Application
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import com.nure.caserskernel.service.auth.AuthRepo
import com.nure.caserskernel.service.auth.AuthService

class TextRecognitionViewModel(application: Application) : AndroidViewModel(application) {
    fun logOut() {
//        val context = getApplication<Application>().applicationContext
//        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
//        with (sharedPref.edit()) {
//            remove("TOKEN_VALUE")
//            remove("USER_ID")
//            apply()
//        }
//        AuthService.setToken(null)
    }
}