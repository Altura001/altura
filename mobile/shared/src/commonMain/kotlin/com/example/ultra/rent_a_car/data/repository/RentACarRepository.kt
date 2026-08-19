package com.example.ultra.rent_a_car.data.repository

import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result
import com.example.ultra.rent_a_car.domain.model.Vehicle
import com.example.ultra.rent_a_car.domain.model.VehicleBooking

interface RentACarRepository {
    suspend fun getVehicles(): Result<List<Vehicle>, DataError.Network>
    suspend fun bookVehicle(vehicleId: String, startDate: String, endDate: String): Result<VehicleBooking, DataError.Network>
    suspend fun getBookings(): Result<List<VehicleBooking>, DataError.Network>
}
