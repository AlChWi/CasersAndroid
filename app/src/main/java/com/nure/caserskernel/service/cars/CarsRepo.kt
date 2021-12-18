package com.nure.caserskernel.service.cars

import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

class CarsRepo(private val carsService: CarsService) {
    suspend fun getCars() = carsService.getCars()
    suspend fun getCar(carID: String) = carsService.getCar(carID = carID)
    suspend fun departCar(carID: String) = carsService.departCar(carID = carID)
    suspend fun removeCargo(
        carID: String,
        cargoNumber: String
    ) = carsService.removeCargo(carID, cargoNumber)
    suspend fun addCarCargo(
        carID: String,
        cargoNumber: String
    ) = carsService.addCarCargo(carID, cargoNumber)
    suspend fun addTrailerCargo(
        trailerID: String,
        cargoNumber: String
    ) = carsService.addTrailerCargo(trailerID, cargoNumber)
}