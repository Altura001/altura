package com.example.ultra.rent_a_car.domain.usecase

import com.example.ultra.rent_a_car.data.repository.RentACarRepository

data class RentACarUseCases(
    val getVehicles: GetVehiclesUseCase,
    val bookVehicle: BookVehicleUseCase,
    val getBookings: GetVehicleBookingsUseCase
)

class GetVehiclesUseCase(private val repo: RentACarRepository) {
    suspend operator fun invoke() = repo.getVehicles()
}

class BookVehicleUseCase(private val repo: RentACarRepository) {
    suspend operator fun invoke(vehicleId: String, startDate: String, endDate: String) =
        repo.bookVehicle(vehicleId, startDate, endDate)
}

class GetVehicleBookingsUseCase(private val repo: RentACarRepository) {
    suspend operator fun invoke() = repo.getBookings()
}
