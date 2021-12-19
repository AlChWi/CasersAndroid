package com.nure.caserskernel.screens.carDetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nure.caserskernel.database.AppDatabase
import com.nure.caserskernel.database.CarsRepository
import com.nure.caserskernel.database.entities.CarServiceCallEntity
import com.nure.caserskernel.database.entities.SealedCargoEntity
import com.nure.caserskernel.database.entities.ServiceCallType
import com.nure.caserskernel.service.ConnectivityChecker
import com.nure.caserskernel.service.cars.Car
import com.nure.caserskernel.service.cars.CarsRepo
import com.nure.caserskernel.service.cars.CarsService
import com.nure.caserskernel.service.cars.VerifiedCar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CarDetailsViewModel(application: Application) : AndroidViewModel(application) {
    val carsRepo: CarsRepo by lazy {
        val carsService = CarsService.shared
        CarsRepo(carsService)
    }

    val carsDBRepository: CarsRepository by lazy {
        val db = AppDatabase.getDatabase(getApplication<Application>().applicationContext)
        CarsRepository(db.dao())
    }

    private var carID: String? = null

    private var _carInfo: MutableLiveData<VerifiedCar?> = MutableLiveData(null)
    var carInfo: LiveData<VerifiedCar?> = _carInfo

    fun onAppear(carID: String) {
        this.carID = carID
        GlobalScope.launch {
            if (ConnectivityChecker.isOnline(getApplication<Application>().applicationContext)) {
                downloadDataFromInternet(carID)
            } else {
                fetchDataFromDB(carID)
            }
        }
    }

    fun verify(cargoNumber: String) {
        GlobalScope.launch {
            carsDBRepository.getCargoByNumber(cargoNumber)?.let {
                it.isVerified = true
                carsDBRepository.insert(it)
            }
            withContext(Dispatchers.Main) {
                carInfo.value?.sealedCargo?.find {
                    it.wrapped.number == cargoNumber
                }?.let { cargo ->
                    cargo.isVerified = true
                }
                carInfo.value?.trailer?.sealedCargo?.find {
                    it.wrapped.number == cargoNumber
                }?.let { cargo ->
                    cargo.isVerified = true
                }
            }
        }
    }

    fun delete(cargoNumber: String) {
        GlobalScope.launch {
            carsDBRepository.deleteCargoByNumber(cargoNumber)
            withContext(Dispatchers.Main) {
                carInfo.value?.sealedCargo?.removeIf {
                    it.wrapped.number == cargoNumber
                }
                carInfo.value?.trailer?.sealedCargo?.removeIf {
                    it.wrapped.number == cargoNumber
                }
            }
            if (ConnectivityChecker.isOnline(getApplication<Application>().applicationContext)) {
                carsRepo.removeCargo(carID ?: "", cargoNumber)
            } else {
                val parameters = mapOf("carID" to (carID ?: ""), "cargoNumber" to cargoNumber)
                val request = CarServiceCallEntity(
                    type = ServiceCallType.RemoveCargo,
                    parameters = parameters
                )
                carsDBRepository.insert(request)
            }
        }
    }

    fun addToCar(cargoNumber: String) {
        GlobalScope.launch {
            if(carsDBRepository.getCargoByNumber(cargoNumber)?.equals(null) ?: true) {
                carsDBRepository.insert(SealedCargoEntity(id = UUID.randomUUID().toString(),
                    number = cargoNumber,
                    trailerID = null,
                    carID = carID ?: "",
                    isVerified = true))
                if (ConnectivityChecker.isOnline(getApplication<Application>().applicationContext)) {
                    carsRepo.addCarCargo(carID ?: "", cargoNumber)
                } else {
                    val parameters = mapOf("carID" to (carID ?: ""), "cargoNumber" to cargoNumber)
                    val request = CarServiceCallEntity(
                        type = ServiceCallType.AddCarCargo,
                        parameters = parameters
                    )
                    carsDBRepository.insert(request)
                }
            }
        }
    }

    fun addToTrailer(cargoNumber: String) {
        GlobalScope.launch {
            if(carsDBRepository.getCargoByNumber(cargoNumber)?.equals(null) ?: true) {
                carsDBRepository.insert(SealedCargoEntity(id = UUID.randomUUID().toString(),
                    number = cargoNumber,
                    trailerID = carInfo.value?.trailer?.id ?: "",
                    carID = null,
                    isVerified = true))
                if (ConnectivityChecker.isOnline(getApplication<Application>().applicationContext)) {
                    carsRepo.addTrailerCargo(carID ?: "", cargoNumber)
                } else {
                    val parameters = mapOf("trailerID" to (carInfo.value?.trailer?.id ?: ""), "cargoNumber" to cargoNumber)
                    val request = CarServiceCallEntity(
                        type = ServiceCallType.AddTrailerCargo,
                        parameters = parameters
                    )
                    carsDBRepository.insert(request)
                }
            }
        }
    }

    fun departCar() {
        GlobalScope.launch {
            if (ConnectivityChecker.isOnline(getApplication<Application>().applicationContext)) {
                carsRepo.departCar(carID ?: "")
            } else {
                val parameters = mapOf("carID" to (carID ?: ""))
                val request = CarServiceCallEntity(
                    type = ServiceCallType.DepartCar,
                    parameters = parameters
                )
                carsDBRepository.insert(request)
            }
            carsDBRepository.deleteCar(carID ?: "")
        }
    }

    private suspend fun downloadDataFromInternet(carID: String) {
        val result = carsRepo.getCar(carID)
        withContext(Dispatchers.Main) {
            if(result.isSuccessful) {
                _carInfo.value = result.body()?.toVerifiedCar()
                val carCargo = _carInfo.value?.sealedCargo?.map { cargo ->
                    val cargoFromBD = carsDBRepository.getCargoByNumber(cargo.wrapped.number)
                    cargo.isVerified = cargoFromBD?.isVerified ?: false
                    cargo
                }
                val trailerCargo = _carInfo.value?.trailer?.sealedCargo?.map { cargo ->
                    val cargoFromBD = carsDBRepository.getCargoByNumber(cargo.wrapped.number)
                    cargo.isVerified = cargoFromBD?.isVerified ?: false
                    cargo
                }
                _carInfo.value?.sealedCargo = carCargo?.toMutableList() ?: mutableListOf()
                _carInfo.value?.trailer?.sealedCargo = trailerCargo?.toMutableList() ?: mutableListOf()
            }
        }
    }

    private suspend fun fetchDataFromDB(carID: String) {
        val car = carsDBRepository.getCar(carID = carID)
        val verifiedCar = car?.let { car ->
            val cargo = carsDBRepository.getCarCargo(car.id).map { it.toVerifiedModel() }
            val trailer = carsDBRepository.getCarTrailer(car.id)
            val trailerCargo = trailer?.let {
                carsDBRepository.getTrailerCargo(it.id)
            }?.map { it.toVerifiedModel() }
            val verifiedCarTrailer = trailer?.toVerifiedModel(trailerCargo ?: listOf())
            car.toVerifiedModel(verifiedCarTrailer, cargo)
        }
        verifiedCar?.let { car ->
            withContext(Dispatchers.Main) {
                _carInfo.value = car
            }
        }
    }
}