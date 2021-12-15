package com.nure.caserskernel.service.auth

import android.preference.PreferenceManager
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("/api/users/login")
    suspend fun loginUser(@Header("Authorization") authorization: String): Response<Token>

    companion object {
        private var _token: Token? = null

        val shared: AuthService by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(AuthService::class.java)
        }

        fun setToken(token: Token?) {
            _token = token
        }

        fun getToken(): Token? {
            return _token
        }
    }
}