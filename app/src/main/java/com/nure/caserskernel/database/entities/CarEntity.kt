package com.nure.caserskernel.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.nure.caserskernel.service.cars.*
import java.util.*

@Entity(tableName = "cars")
data class CarEntity(
    @PrimaryKey val id: String,
    val sign: String,
    val registeredAt: Date,
    @Embedded val driver: DriverEntity
) {
    fun toVerifiedModel(
        trailer: VerifiedCarTrailer?,
        cargo: List<VerifiedSealedCargo>
    ): VerifiedCar {
        return VerifiedCar(
            id,
            sign,
            registeredAt,
            driver.toModel(),
            trailer,
            cargo.toMutableList()
        )
    }
}

data class DriverEntity(
    val driverID: String,
    val name: String
) {
    fun toModel(): Driver {
        return Driver(driverID, name)
    }
}

@Entity(tableName = "car_trailers")
data class CarTrailerEntity(
    @PrimaryKey val id: String,
    val sign: String,
    val carID: String
) {
    fun toVerifiedModel(cargo: List<VerifiedSealedCargo>): VerifiedCarTrailer {
        return VerifiedCarTrailer(
            id = this.id,
            sign = this.sign,
            sealedCargo = cargo.toMutableList()
        )
    }
}

@Entity(tableName = "cargo")
data class SealedCargoEntity(
    @PrimaryKey val id: String,
    val number: String,
    val trailerID: String?,
    val carID: String?,
    var isVerified: Boolean
) {
    fun toVerifiedModel(): VerifiedSealedCargo {
        return VerifiedSealedCargo(
            wrapped = SealedCargo(id = this.id, number = this.number),
            isVerified = this.isVerified
        )
    }
}