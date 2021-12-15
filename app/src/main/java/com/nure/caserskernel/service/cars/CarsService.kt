package com.nure.caserskernel.service.cars

import com.nure.caserskernel.service.BasicAuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CarsService {
    @GET("/api/cars")
    suspend fun getCars(): Response<List<Car>>

    @GET("/api/cars/{carID}")
    suspend fun getCar(@Path("carID") carID: String): Response<Car>

    companion object {
        val shared: CarsService by lazy {
            val client =  OkHttpClient.Builder()
                .addInterceptor(BasicAuthInterceptor())
                .build()
            val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(CarsService::class.java)
        }
    }
}