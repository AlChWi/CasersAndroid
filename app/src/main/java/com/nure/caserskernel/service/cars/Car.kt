package com.nure.caserskernel.service.cars


import com.nure.caserskernel.database.entities.CarEntity
import com.nure.caserskernel.database.entities.CarTrailerEntity
import com.nure.caserskernel.database.entities.DriverEntity
import com.nure.caserskernel.database.entities.SealedCargoEntity
import java.util.*

data class Car(
    val id: String,
    val sign: String,
    val registeredAt: Date,
    val driver: Driver,
    val trailer: CarTrailer?,
    val sealedCargo: List<SealedCargo>
) {
    fun toVerifiedCar(): VerifiedCar {
        return VerifiedCar(
            id = this.id,
            sign = this.sign,
            registeredAt = this.registeredAt,
            driver = this.driver,
            trailer = this.trailer?.toVerifiedCarTrailer(),
            sealedCargo = this.sealedCargo.map { it.toVerifiedSealedCargo() }.toMutableList()
        )
    }

    fun toEntity(): CarEntity {
        return CarEntity(
            id = this.id,
            sign = this.sign,
            registeredAt = this.registeredAt,
            driver = this.driver.toEntity()
        )
    }
}

data class Driver(
    val id: String,
    val name: String
) {
    fun toEntity(): DriverEntity {
        return DriverEntity(
            driverID = this.id,
            name = this.name
        )
    }
}

data class CarTrailer(
    val id: String,
    val sign: String,
    val sealedCargo: List<SealedCargo>
) {
    fun toVerifiedCarTrailer(): VerifiedCarTrailer {
        return VerifiedCarTrailer(
            id = this.id,
            sign = this.sign,
            sealedCargo = this.sealedCargo.map { it.toVerifiedSealedCargo() }.toMutableList()
        )
    }

    fun toEntity(carID: String): CarTrailerEntity {
        return CarTrailerEntity(
            id = this.id,
            sign = this.sign,
            carID = carID
        )
    }
}

data class SealedCargo(
    val id: String,
    val number: String
) {
    fun toVerifiedSealedCargo(): VerifiedSealedCargo {
        return VerifiedSealedCargo(
            wrapped = this,
            isVerified = false
        )
    }

    fun toCarCargoEntity(carID: String): SealedCargoEntity {
        return SealedCargoEntity(
            id = this.id,
            number = this.number,
            carID = carID,
            trailerID = null,
            isVerified = false
        )
    }

    fun toTrailerCargoEntity(trailerID: String): SealedCargoEntity {
        return SealedCargoEntity(
            id = this.id,
            number = this.number,
            carID = null,
            trailerID = trailerID,
            isVerified = false
        )
    }
}

data class VerifiedCar(
    val id: String,
    val sign: String,
    val registeredAt: Date,
    val driver: Driver,
    val trailer: VerifiedCarTrailer?,
    var sealedCargo: MutableList<VerifiedSealedCargo>
) {
    fun toEntity(): CarEntity {
        return CarEntity(
            id = this.id,
            sign = this.sign,
            registeredAt = this.registeredAt,
            driver = this.driver.toEntity()
        )
    }
}

data class VerifiedCarTrailer(
    val id: String,
    val sign: String,
    var sealedCargo: MutableList<VerifiedSealedCargo>
) {
    fun toEntity(carID: String): CarTrailerEntity {
        return CarTrailerEntity(
            id = this.id,
            sign = this.sign,
            carID = carID
        )
    }
}

data class VerifiedSealedCargo(
    val wrapped: SealedCargo,
    var isVerified: Boolean
) {
    fun toCarCargoEntity(carID: String): SealedCargoEntity {
        return SealedCargoEntity(
            id = wrapped.id,
            number = wrapped.number,
            carID = carID,
            trailerID = null,
            isVerified = isVerified
        )
    }

    fun toTrailerCargoEntity(trailerID: String): SealedCargoEntity {
        return SealedCargoEntity(
            id = wrapped.id,
            number = wrapped.number,
            carID = null,
            trailerID = trailerID,
            isVerified = isVerified
        )
    }
}