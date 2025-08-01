package app.cui.ro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cui.ro.auth.AuthService
import app.cui.ro.db.Notification
import app.cui.ro.models.VMNotifications
import app.cui.ro.ui.theme.CuiroColors
import java.util.Locale

@Composable
fun NotificationsScreen(
    authService: AuthService,
    notificationsViewModel: VMNotifications = viewModel()
) {
    val userId = remember { authService.getUserId() }
    val pushEnabledState = remember { mutableStateOf(notificationsViewModel.isPushEnabled()) }
    val notifications by notificationsViewModel.notifications.collectAsState()
    val isLoading by notificationsViewModel.isLoading.collectAsState()
    val unreadCount by notificationsViewModel.unreadCount.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Acciones rápidas
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 6.dp,
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Acciones",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { notificationsViewModel.markAllAsRead() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(backgroundColor = CuiroColors.ObjectsPink),
                            shape = RoundedCornerShape(12.dp),
                            enabled = unreadCount > 0
                        ) {
                            Icon(Icons.Default.Check, "Marcar todas", Modifier.size(16.dp), Color.White)
                            Spacer(Modifier.width(4.dp))
                            Text("Marcar todas", color = Color.White, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { notificationsViewModel.clearAllNotifications() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Clear, "Limpiar", Modifier.size(16.dp), Color.White)
                            Spacer(Modifier.width(4.dp))
                            Text("Limpiar", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Recientes
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 6.dp,
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Recientes",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = CuiroColors.ObjectsPink)
                            }
                        }

                        notifications.isEmpty() -> {
                            EmptyNotificationsView()
                        }

                        else -> {
                            // LazyColumn scrollable solo dentro de la Card
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 400.dp), // Limita la altura máxima
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(notifications, key = { it.id }) { notification ->
                                    NotificationItem(
                                        notification = notification,
                                        onMarkAsRead = { notificationsViewModel.markNotificationAsRead(it) },
                                        onDelete = { notificationsViewModel.deleteNotification(it) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Configuración
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 6.dp,
                backgroundColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Configuración",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    NotificationSettingRow(
                        icon = Icons.Default.Notifications,
                        title = "Notificaciones Push",
                        subtitle = "Recibir notificaciones en tiempo real",
                        isChecked = pushEnabledState.value,
                        onCheckedChange = { enabled ->
                            pushEnabledState.value = enabled
                            if (enabled) {
                                notificationsViewModel.subscribeToTopic()
                            } else {
                                notificationsViewModel.unsubscribeFromTopic()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onMarkAsRead: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = if (notification.isRead) 2.dp else 4.dp,
        backgroundColor = if (notification.isRead) Color(0xFFF8F8F8) else Color.White,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {

            val notificationType = notification.type ?: "generic"

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = getNotificationColorByType(notificationType),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificationIconByType(notificationType),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Contenido de la notificación
            Column(
                modifier = Modifier.weight(1f)
            ) {
                notification.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.body1,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        color = if (notification.isRead) Color.Gray else Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                notification.body?.let {
                    Text(
                        text = it, // Cambiado de 'message' a 'body' según tu entidad
                        style = MaterialTheme.typography.body2,
                        color = if (notification.isRead) Color.DarkGray else Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Acciones del item
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (!notification.isRead) {
                    IconButton(onClick = { onMarkAsRead(notification.id.toLong()) }) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Marcar como leída",
                            tint = CuiroColors.ObjectsPink
                        )
                    }
                }
                IconButton(onClick = { onDelete(notification.id.toLong()) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar notificación",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}


fun getNotificationColorByType(type: String): Color {
    return when (type.lowercase(Locale.ROOT)) {
        "daily_hydration", "info" -> Color(0xFF4CAF50)
        "warning" -> Color(0xFFFFC107)
        "error" -> Color(0xFFF44336)
        else -> Color.Gray
    }
}

fun getNotificationIconByType(type: String): ImageVector {
    return when (type.lowercase(Locale.ROOT)) {
        "daily_hydration", "info" -> Icons.Filled.Info
        "warning" -> Icons.Filled.Warning
        "error" -> Icons.Filled.Close
        else -> Icons.Filled.Notifications
    }
}


@Composable
fun EmptyNotificationsView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "No hay notificaciones",
                modifier = Modifier.size(48.dp),
                tint = Color.LightGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No tienes notificaciones",
                style = MaterialTheme.typography.subtitle1,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun NotificationSettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CuiroColors.ObjectsPink,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CuiroColors.ObjectsPink,
                checkedTrackColor = CuiroColors.ObjectsPink.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )
    }
}
