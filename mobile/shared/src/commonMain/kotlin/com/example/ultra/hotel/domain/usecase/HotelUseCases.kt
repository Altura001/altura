package com.example.ultra.hotel.domain.usecase

import com.example.ultra.hotel.data.repository.HotelRepository

data class HotelUseCases(
    val getProperties: GetPropertiesUseCase,
    val bookProperty: BookPropertyUseCase,
    val getBookings: GetPropertyBookingsUseCase
)

class GetPropertiesUseCase(private val repo: HotelRepository) {
    suspend operator fun invoke() = repo.getProperties()
}

class BookPropertyUseCase(private val repo: HotelRepository) {
    suspend operator fun invoke(propertyId: String, checkIn: String, checkOut: String) =
        repo.bookProperty(propertyId, checkIn, checkOut)
}

class GetPropertyBookingsUseCase(private val repo: HotelRepository) {
    suspend operator fun invoke() = repo.getBookings()
}
