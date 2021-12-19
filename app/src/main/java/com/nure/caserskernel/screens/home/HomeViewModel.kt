package com.nure.caserskernel.screens.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.nure.caserskernel.DelayedRequestExecuter
import com.nure.caserskernel.database.AppDatabase
import com.nure.caserskernel.database.CarsRepository
import com.nure.caserskernel.service.ConnectivityChecker
import com.nure.caserskernel.service.auth.AuthService
import com.nure.caserskernel.service.cars.Car
import com.nure.caserskernel.service.cars.CarsRepo
import com.nure.caserskernel.service.cars.CarsService
import com.nure.caserskernel.service.cars.VerifiedCar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val carsRepo: CarsRepo by lazy {
        val carsService = CarsService.shared
        CarsRepo(carsService)
    }

    val carsDBRepository: CarsRepository by lazy {
        val db = AppDatabase.getDatabase(getApplication<Application>().applicationContext)
        CarsRepository(db.dao())
    }

    private val _listItems: MutableLiveData<List<VerifiedCar>> = MutableLiveData(mutableListOf())
    val listItems: LiveData<List<VerifiedCar>> = _listItems

    fun onAppear() {
        GlobalScope.launch {
            if (ConnectivityChecker.isOnline(getApplication<Application>().applicationContext)) {
                carsDBRepository.getRequests().forEach { request ->
                    DelayedRequestExecuter.execute(request)
                    carsDBRepository.deleteRequest(request.id)
                }
                downloadDataFromInternet()
            } else {
                fetchDataFromDB()
            }
        }
    }

    private suspend fun fetchDataFromDB() {
        var carEntities = carsDBRepository.getAllCars()
        val cars = carEntities.map { car ->
            val trailer = carsDBRepository.getCarTrailer(car.id)
            val carCargo = carsDBRepository.getCarCargo(car.id).map { it.toVerifiedModel() }
            val trailerCargo = trailer?.let {
                carsDBRepository.getTrailerCargo(it.id)
            }?.map { it.toVerifiedModel() } ?: listOf()
            val verifiedTrailer = trailer?.toVerifiedModel(cargo = trailerCargo)
            car.toVerifiedModel(verifiedTrailer, carCargo)
        }
        withContext(Dispatchers.Main) {
            _listItems.value = cars
        }
    }
    private suspend fun downloadDataFromInternet() {
        val result = carsRepo.getCars()
        Log.i("Home_screen", "${result.body()}")
        if (result.isSuccessful) {
            result.body()?.let { cars ->
                saveData(cars)
            }
            withContext(Dispatchers.Main) {
                _listItems.value = result.body()?.map { it.toVerifiedCar() }
            }
        }
    }

    private suspend fun saveData(cars: List<Car>) {
        cars.forEach { car ->
            if(carsDBRepository.getCar(car.id)?.equals(null) ?: true) {
                carsDBRepository.insert(car.toEntity())
                car.trailer?.let { trailer ->
                    carsDBRepository.insert(trailer.toEntity(car.id))
                    trailer.sealedCargo.forEach { cargo ->
                        carsDBRepository.insert(cargo.toTrailerCargoEntity(trailer.id))
                    }
                }
                car.sealedCargo.forEach { cargo ->
                    carsDBRepository.insert(cargo.toCarCargoEntity(car.id))
                }
            }
        }
    }
}