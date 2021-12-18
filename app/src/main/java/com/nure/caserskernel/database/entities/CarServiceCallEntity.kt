package com.nure.caserskernel.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "requests")
data class CarServiceCallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: ServiceCallType,
    val parameters: Map<String, String>
)

enum class ServiceCallType {
    DepartCar,
    RemoveCargo,
    AddCarCargo,
    AddTrailerCargo,
}