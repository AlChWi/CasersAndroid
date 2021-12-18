package com.nure.caserskernel.database

import android.util.Log
import androidx.annotation.WorkerThread
import com.nure.caserskernel.database.entities.CarEntity
import com.nure.caserskernel.database.entities.CarServiceCallEntity
import com.nure.caserskernel.database.entities.CarTrailerEntity
import com.nure.caserskernel.database.entities.SealedCargoEntity
import kotlinx.coroutines.flow.Flow

class CarsRepository(private val dao: DAO) {
    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(car: CarEntity) {
        dao.insert(car)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(request: CarServiceCallEntity) {
        dao.insert(request)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(vararg trailers: CarTrailerEntity) {
        dao.insert(trailers = trailers)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(vararg cargo: SealedCargoEntity) {
        dao.insert(cargo = cargo)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getCar(carID: String): CarEntity? {
        return dao.getCar(carID)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllCars(): List<CarEntity> {
        return dao.getCars()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getCarTrailer(carID: String): CarTrailerEntity? {
        return dao.getCarTrailer(carID)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getCarCargo(carID: String): List<SealedCargoEntity> {
        return dao.getCarCargo(carID)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getTrailerCargo(trailerID: String): List<SealedCargoEntity> {
        return dao.getTrailerCargo(trailerID)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getCargoByNumber(number: String): SealedCargoEntity? {
        return dao.getCargo(cargoNumber = number)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteCargoByNumber(number: String) {
        return dao.delete(cargoNumber = number)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteCar(carID: String) {
        return dao.deleteCar(carID)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getRequests(): List<CarServiceCallEntity> {
        return dao.getRequests()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteRequest(id: Int) {
        return dao.deleteRequest(id)
    }
}