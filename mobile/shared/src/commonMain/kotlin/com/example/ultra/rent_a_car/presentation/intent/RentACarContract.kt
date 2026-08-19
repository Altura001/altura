package com.example.ultra.rent_a_car.presentation.intent

import com.example.ultra.core.presentation.UiText
import com.example.ultra.rent_a_car.domain.model.Vehicle
import com.example.ultra.rent_a_car.domain.model.VehicleBooking
import com.example.ultra.rent_a_car.domain.model.VehicleFilter

data class RentACarState(
    val isLoading: Boolean = false,
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicle: Vehicle? = null,
    val bookings: List<VehicleBooking> = emptyList(),
    val filter: VehicleFilter = VehicleFilter(),
    val error: UiText? = null
)

sealed interface RentACarAction {
    data object LoadVehicles : RentACarAction
    data class SelectVehicle(val vehicle: Vehicle) : RentACarAction
    data class ApplyFilter(val filter: VehicleFilter) : RentACarAction
    data class BookVehicle(val startDate: String, val endDate: String) : RentACarAction
    data object LoadBookings : RentACarAction
    data object ClearError : RentACarAction
}

sealed interface RentACarEvent {
    data class ShowError(val message: UiText) : RentACarEvent
    data object BookingPlaced : RentACarEvent
}
