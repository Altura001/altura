package com.example.ultra.core.presentation.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationManager {

    private val _notification = MutableStateFlow<Notification?>(null)
    val notification: StateFlow<Notification?> = _notification.asStateFlow()

    fun show(
        message: String,
        type: NotificationType = NotificationType.Info,
        duration: NotificationDuration = NotificationDuration.Short
    ) {
        _notification.value = Notification(message, type, duration)
    }

    fun success(message: String, duration: NotificationDuration = NotificationDuration.Short) {
        show(message, NotificationType.Success, duration)
    }

    fun error(message: String, duration: NotificationDuration = NotificationDuration.Medium) {
        show(message, NotificationType.Error, duration)
    }

    fun info(message: String, duration: NotificationDuration = NotificationDuration.Short) {
        show(message, NotificationType.Info, duration)
    }

    fun warning(message: String, duration: NotificationDuration = NotificationDuration.Medium) {
        show(message, NotificationType.Warning, duration)
    }

    fun dismiss() {
        _notification.value = null
    }
}
