package com.example.ultra.service_shell.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.service_shell.presentation.intent.ServiceShellAction
import com.example.ultra.service_shell.presentation.intent.ServiceShellEvent
import com.example.ultra.service_shell.presentation.intent.ServiceShellState
import com.example.ultra.services.Service
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServiceShellViewModel : ViewModel() {

    private val _state = MutableStateFlow(ServiceShellState())
    val state = _state.asStateFlow()

    private val _events = Channel<ServiceShellEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: ServiceShellAction) {
        when (action) {
            is ServiceShellAction.SelectService -> {
                _state.update { it.copy(activeService = action.service, showSwitcher = false) }
                viewModelScope.launch {
                    _events.send(ServiceShellEvent.NavigateToService(action.service))
                }
            }
            is ServiceShellAction.ToggleSwitcher -> {
                _state.update { it.copy(showSwitcher = !it.showSwitcher) }
            }
            is ServiceShellAction.DismissSwitcher -> {
                _state.update { it.copy(showSwitcher = false) }
            }
        }
    }
}
