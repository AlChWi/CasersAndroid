package com.nure.caserskernel.screens.carDetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nure.caserskernel.service.cars.Car
import com.nure.caserskernel.service.cars.CarsRepo
import com.nure.caserskernel.service.cars.CarsService
import com.nure.caserskernel.service.cars.VerifiedCar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarDetailsViewModel(application: Application) : AndroidViewModel(application) {
    val carsRepo: CarsRepo by lazy {
        val carsService = CarsService.shared
        CarsRepo(carsService)
    }

    private var carID: String? = null

    var carInfo: MutableLiveData<VerifiedCar?> = MutableLiveData(null)

    fun configure(carID: String) {
        this.carID = carID
    }

    fun onAppear() {
        GlobalScope.launch {
            val result = carsRepo.getCar(carID ?: "")
            withContext(Dispatchers.Main) {
                if(result.isSuccessful) {
                    carInfo.value = result.body()?.toVerifiedCar()
                }
            }
        }
    }
}