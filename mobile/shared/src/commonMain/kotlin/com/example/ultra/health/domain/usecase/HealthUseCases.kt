package com.example.ultra.health.domain.usecase

import com.example.ultra.health.data.repository.HealthRepository

data class HealthUseCases(
    val getServices: GetHealthServicesUseCase,
    val bookAppointment: BookAppointmentUseCase,
    val getAppointments: GetAppointmentsUseCase
)

class GetHealthServicesUseCase(private val repo: HealthRepository) {
    suspend operator fun invoke() = repo.getServices()
}

class BookAppointmentUseCase(private val repo: HealthRepository) {
    suspend operator fun invoke(serviceId: String, date: String, time: String) =
        repo.bookAppointment(serviceId, date, time)
}

class GetAppointmentsUseCase(private val repo: HealthRepository) {
    suspend operator fun invoke() = repo.getAppointments()
}
