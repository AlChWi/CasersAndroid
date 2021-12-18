package com.nure.caserskernel.database

import androidx.room.*
import com.nure.caserskernel.database.entities.CarEntity
import com.nure.caserskernel.database.entities.CarServiceCallEntity
import com.nure.caserskernel.database.entities.CarTrailerEntity
import com.nure.caserskernel.database.entities.SealedCargoEntity
import com.nure.caserskernel.service.cars.Car
import kotlinx.coroutines.flow.Flow

@Dao
interface DAO {
    @Query("SELECT * FROM cargo WHERE carID LIKE :carID")
    suspend fun getCarCargo(carID: String): List<SealedCargoEntity>
    @Query("SELECT * FROM cargo WHERE trailerID LIKE :trailerID")
    suspend fun getTrailerCargo(trailerID: String): List<SealedCargoEntity>
    @Query("SELECT * FROM car_trailers WHERE carID LIKE :carID LIMIT 1")
    suspend fun getCarTrailer(carID: String): CarTrailerEntity?
    @Query("SELECT * FROM cars WHERE id LIKE :id LIMIT 1")
    suspend fun getCar(id: String): CarEntity?
    @Query("SELECT * FROM cars")
    suspend fun getCars(): List<CarEntity>
    @Query("SELECT * FROM cargo where number LIKE :cargoNumber LIMIT 1")
    suspend fun getCargo(cargoNumber: String): SealedCargoEntity?

    @Query("SELECT * FROM requests ORDER BY id")
    suspend fun getRequests(): List<CarServiceCallEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg cars: CarEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg trailers: CarTrailerEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg cargo: SealedCargoEntity)
    @Insert
    suspend fun insert(vararg requests: CarServiceCallEntity)

    @Delete
    suspend fun delete(car: CarEntity)
    @Delete
    suspend fun delete(trailer: CarTrailerEntity)
    @Delete
    suspend fun delete(cargo: SealedCargoEntity)
    @Query("DELETE FROM cargo WHERE number LIKE :cargoNumber")
    suspend fun delete(cargoNumber: String)
    @Query("DELETE FROM cars WHERE id LIKE :carID")
    suspend fun deleteCar(carID: String)
    @Query("DELETE FROM requests WHERE id LIKE :id")
    suspend fun deleteRequest(id: Int)
}