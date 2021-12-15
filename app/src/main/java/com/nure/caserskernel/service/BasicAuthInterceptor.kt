package com.nure.caserskernel.service

import com.nure.caserskernel.service.auth.AuthService
import okhttp3.Credentials
import okhttp3.Interceptor

class BasicAuthInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()
        val token = AuthService.getToken()?.value ?: ""
        request = request.newBuilder().header("Authorization", "Bearer $token").build()

        return chain.proceed(request)
    }
}