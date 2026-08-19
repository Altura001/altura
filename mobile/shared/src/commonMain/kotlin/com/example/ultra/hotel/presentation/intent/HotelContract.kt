package com.example.ultra.hotel.presentation.intent

import com.example.ultra.core.presentation.UiText
import com.example.ultra.hotel.domain.model.Property
import com.example.ultra.hotel.domain.model.PropertyBooking
import com.example.ultra.hotel.domain.model.PropertyFilter

data class HotelState(
    val isLoading: Boolean = false,
    val properties: List<Property> = emptyList(),
    val selectedProperty: Property? = null,
    val bookings: List<PropertyBooking> = emptyList(),
    val filter: PropertyFilter = PropertyFilter(),
    val error: UiText? = null
)

sealed interface HotelAction {
    data object LoadProperties : HotelAction
    data class SelectProperty(val property: Property) : HotelAction
    data class ApplyFilter(val filter: PropertyFilter) : HotelAction
    data class BookProperty(val checkIn: String, val checkOut: String) : HotelAction
    data object LoadBookings : HotelAction
    data object ClearError : HotelAction
}

sealed interface HotelEvent {
    data class ShowError(val message: UiText) : HotelEvent
    data object BookingPlaced : HotelEvent
}
