package com.nure.caserskernel.screens.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nure.caserskernel.service.auth.AuthService
import com.nure.caserskernel.service.cars.Car
import com.nure.caserskernel.service.cars.CarsRepo
import com.nure.caserskernel.service.cars.CarsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val carsRepo: CarsRepo by lazy {
        val carsService = CarsService.shared
        CarsRepo(carsService)
    }

    private val _listItems: MutableLiveData<List<Car>> = MutableLiveData(mutableListOf())
    val listItems: LiveData<List<Car>> = _listItems

    fun onAppear() {
        GlobalScope.launch {
            val result = carsRepo.getCars()
            Log.i("Home_screen", "${result.body()}")
            withContext(Dispatchers.Main) {
                if(result.isSuccessful) {
                    _listItems.value = result.body()
                }
            }
        }
    }
}