package com.example.ultra.core.presentation.notification

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ultra.core.presentation.theme.AlturaTeal
import com.example.ultra.core.presentation.theme.AlturaRed
import com.example.ultra.core.presentation.theme.AlturaGold
import com.example.ultra.core.presentation.theme.AlturaBlue

enum class NotificationType(
    val backgroundColor: Color,
    val icon: ImageVector,
    val contentColor: Color = Color.White
) {
    Success(
        backgroundColor = AlturaTeal,
        icon = Icons.Filled.CheckCircle
    ),
    Error(
        backgroundColor = AlturaRed,
        icon = Icons.Filled.Error
    ),
    Info(
        backgroundColor = AlturaBlue,
        icon = Icons.Filled.Info
    ),
    Warning(
        backgroundColor = AlturaGold,
        icon = Icons.Filled.Warning,
        contentColor = Color.Black
    )
}

data class Notification(
    val message: String,
    val type: NotificationType = NotificationType.Info,
    val duration: NotificationDuration = NotificationDuration.Short
)

enum class NotificationDuration(val millis: Long) {
    Short(2000L),
    Medium(3500L),
    Long(5000L)
}
