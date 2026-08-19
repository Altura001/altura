package com.example.ultra.health.data.repository

import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result
import com.example.ultra.health.domain.model.Appointment
import com.example.ultra.health.domain.model.HealthService

interface HealthRepository {
    suspend fun getServices(): Result<List<HealthService>, DataError.Network>
    suspend fun bookAppointment(serviceId: String, date: String, time: String): Result<Appointment, DataError.Network>
    suspend fun getAppointments(): Result<List<Appointment>, DataError.Network>
}
