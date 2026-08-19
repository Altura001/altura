package com.example.ultra.hotel.data.repository

import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result
import com.example.ultra.hotel.domain.model.Property
import com.example.ultra.hotel.domain.model.PropertyBooking

interface HotelRepository {
    suspend fun getProperties(): Result<List<Property>, DataError.Network>
    suspend fun bookProperty(propertyId: String, checkIn: String, checkOut: String): Result<PropertyBooking, DataError.Network>
    suspend fun getBookings(): Result<List<PropertyBooking>, DataError.Network>
}
