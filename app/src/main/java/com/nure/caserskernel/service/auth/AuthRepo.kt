package com.nure.caserskernel.service.auth

class AuthRepo(private val authService: AuthService) {
    suspend fun login(authorization: String) = authService.loginUser(authorization)
}