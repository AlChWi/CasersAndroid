package com.nure.caserskernel.service.cars

class CarsRepo(private val carsService: CarsService) {
    suspend fun getCars() = carsService.getCars()
    suspend fun getCar(carID: String) = carsService.getCar(carID = carID)
}