package com.nure.caserskernel

import android.app.Service
import com.nure.caserskernel.database.entities.CarServiceCallEntity
import com.nure.caserskernel.database.entities.ServiceCallType
import com.nure.caserskernel.service.cars.CarsRepo
import com.nure.caserskernel.service.cars.CarsService

class DelayedRequestExecuter {
    companion object {
        private val carsRepo: CarsRepo by lazy {
            val carsService = CarsService.shared
            CarsRepo(carsService)
        }

        suspend fun execute(request: CarServiceCallEntity) {
            when (request.type) {
                ServiceCallType.DepartCar -> departCar(request)
                ServiceCallType.RemoveCargo -> removeCargo(request)
                ServiceCallType.AddCarCargo -> addCarCargo(request)
                ServiceCallType.AddTrailerCargo -> addTrailerCargo(request)
            }
        }

        private suspend fun departCar(request: CarServiceCallEntity) {
            request.parameters.get("carID")?.let {
                carsRepo.departCar(it)
            }
        }

        private suspend fun removeCargo(request: CarServiceCallEntity) {
            val carID = request.parameters.get("carID")
            val cargoNumber = request.parameters.get("cargoNumber")
            if (carID != null && cargoNumber != null) {
                carsRepo.removeCargo(carID, cargoNumber)
            }
        }

        private suspend fun addCarCargo(request: CarServiceCallEntity) {
            val carID = request.parameters.get("carID")
            val cargoNumber = request.parameters.get("cargoNumber")
            if (carID != null && cargoNumber != null) {
                carsRepo.addCarCargo(carID, cargoNumber)
            }
        }

        private suspend fun addTrailerCargo(request: CarServiceCallEntity) {
            val trailerID = request.parameters.get("trailerID")
            val cargoNumber = request.parameters.get("cargoNumber")
            if (trailerID != null && cargoNumber != null) {
                carsRepo.addCarCargo(trailerID, cargoNumber)
            }
        }
    }
}