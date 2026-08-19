package com.example.ultra.service_shell.presentation

import androidx.compose.runtime.Composable
import com.example.ultra.service_shell.presentation.intent.ServiceShellAction
import com.example.ultra.service_shell.presentation.intent.ServiceShellState
import com.example.ultra.services.Service

@Composable
fun ServiceTopBar(
    activeService: Service,
    onSwitchClick: () -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    // TODO: UI/UX builds this — show service name + accent color + switch button
}

@Composable
fun ServiceSwitcherSheet(
    services: List<Service>,
    onSelectService: (Service) -> Unit,
    onDismiss: () -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    // TODO: UI/UX builds this — bottom sheet or modal with service grid
}

@Composable
fun ServiceShellScreen(
    state: ServiceShellState,
    onAction: (ServiceShellAction) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    content: @Composable () -> Unit
) {
    // TODO: UI/UX builds this — TopBar + content host + optional switcher sheet
    // content() renders the active service's UI
    content()
}
