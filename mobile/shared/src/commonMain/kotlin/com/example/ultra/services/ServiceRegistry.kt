package com.example.ultra.services

object ServiceRegistry {

    val allServices: List<Service> = Service.entries

    fun getById(id: String): Service =
        allServices.first { it.id == id }

    fun homeRoute(service: Service): String =
        "${service.routePrefix}/home"
}
