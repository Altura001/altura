package com.example.ultra.service_shell.presentation.intent

import com.example.ultra.services.Service

data class ServiceShellState(
    val activeService: Service = Service.SHOPPING,
    val showSwitcher: Boolean = false
)

sealed interface ServiceShellAction {
    data class SelectService(val service: Service) : ServiceShellAction
    data object ToggleSwitcher : ServiceShellAction
    data object DismissSwitcher : ServiceShellAction
}

sealed interface ServiceShellEvent {
    data class NavigateToService(val service: Service) : ServiceShellEvent
}
