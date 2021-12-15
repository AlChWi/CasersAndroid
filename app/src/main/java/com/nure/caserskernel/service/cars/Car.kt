package com.nure.caserskernel.service.cars

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
            sealedCargo = this.sealedCargo.map { it.toVerifiedSealedCargo() }
        )
    }
}

data class Driver(
    val id: String,
    val name: String
)

data class CarTrailer(
    val id: String,
    val sign: String,
    val sealedCargo: List<SealedCargo>
) {
    fun toVerifiedCarTrailer(): VerifiedCarTrailer {
        return VerifiedCarTrailer(
            id = this.id,
            sign = this.sign,
            sealedCargo = this.sealedCargo.map { it.toVerifiedSealedCargo() }
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
}

data class VerifiedCar(
    val id: String,
    val sign: String,
    val registeredAt: Date,
    val driver: Driver,
    val trailer: VerifiedCarTrailer?,
    val sealedCargo: List<VerifiedSealedCargo>
)

data class VerifiedCarTrailer(
    val id: String,
    val sign: String,
    val sealedCargo: List<VerifiedSealedCargo>
)

data class VerifiedSealedCargo(
    val wrapped: SealedCargo,
    val isVerified: Boolean
)