package com.example.ultra.health.presentation.intent

import com.example.ultra.core.presentation.UiText
import com.example.ultra.health.domain.model.Appointment
import com.example.ultra.health.domain.model.HealthService

data class HealthState(
    val isLoading: Boolean = false,
    val services: List<HealthService> = emptyList(),
    val selectedService: HealthService? = null,
    val selectedDate: String = "",
    val selectedTime: String = "",
    val appointments: List<Appointment> = emptyList(),
    val error: UiText? = null
)

sealed interface HealthAction {
    data object LoadServices : HealthAction
    data class SelectService(val service: HealthService) : HealthAction
    data class SetDate(val date: String) : HealthAction
    data class SetTime(val time: String) : HealthAction
    data object BookAppointment : HealthAction
    data object LoadAppointments : HealthAction
    data object ClearError : HealthAction
}

sealed interface HealthEvent {
    data class ShowError(val message: UiText) : HealthEvent
    data object AppointmentBooked : HealthEvent
}
