package app.cui.ro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cui.ro.auth.AuthService
import app.cui.ro.ui.theme.CuiroColors

@Composable
fun NotificationsScreen(
    authService: AuthService,
) {
    val userId = remember { authService.getUserId() }
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var unreadCount by remember { mutableStateOf(0) }

    LaunchedEffect(userId) {
        if (userId != null) {
            loadNotifications(userId) { notificationsList ->
                notifications = notificationsList
                unreadCount = notificationsList.count { !it.isRead }
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card header con título y badge
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 6.dp,
                backgroundColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notificaciones",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    if (unreadCount > 0) {
                        NotificationBadge(count = unreadCount)
                    }
                }
            }

            // Card de acciones rápidas
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
                            onClick = {
                                // Marcar todas como leídas
                                userId?.let { id ->
                                    markAllAsRead(id) {
                                        notifications = notifications.map { it.copy(isRead = true) }
                                        unreadCount = 0
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = CuiroColors.ObjectsPink
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = unreadCount > 0
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Marcar todas",
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Marcar todas",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = {
                                // Limpiar notificaciones
                                userId?.let { id ->
                                    clearAllNotifications(id) {
                                        notifications = emptyList()
                                        unreadCount = 0
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar",
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Limpiar",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Card de lista de notificaciones
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

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = CuiroColors.ObjectsPink
                            )
                        }
                    } else if (notifications.isEmpty()) {
                        EmptyNotificationsView()
                    } else {
                        LazyColumn(
                            modifier = Modifier.height(400.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(notifications) { notification ->
                                NotificationItem(
                                    notification = notification,
                                    onMarkAsRead = { notificationId ->
                                        userId?.let { id ->
                                            markNotificationAsRead(id, notificationId) {
                                                notifications = notifications.map {
                                                    if (it.id == notificationId) it.copy(isRead = true) else it
                                                }
                                                unreadCount = notifications.count { !it.isRead }
                                            }
                                        }
                                    },
                                    onDelete = { notificationId ->
                                        userId?.let { id ->
                                            deleteNotification(id, notificationId) {
                                                notifications =
                                                    notifications.filter { it.id != notificationId }
                                                unreadCount = notifications.count { !it.isRead }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Card de configuración
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = 6.dp,
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
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
                        subtitle = "Recibir notificaciones en tiempo real"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    NotificationSettingRow(
                        icon = Icons.Default.Email,
                        title = "Notificaciones por Email",
                        subtitle = "Recibir resumen diario por correo"
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationBadge(count: Int) {
    Box(
        modifier = Modifier
            .background(
                color = Color.Red,
                shape = CircleShape
            )
            .padding(
                horizontal = if (count > 99) 8.dp else 6.dp,
                vertical = 4.dp
            )
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onMarkAsRead: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = if (notification.isRead) 2.dp else 4.dp,
        backgroundColor = if (notification.isRead) Color(0xFFF8F8F8) else Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Icono de notificación
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = getNotificationColor(notification.type),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificationIcon(notification.type),
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
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.body1,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                    color = if (notification.isRead) Color.Gray else Color.Black
                )

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = formatTimeAgo(notification.timestamp),
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Indicador de no leído y acciones
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = CuiroColors.ObjectsPink,
                                shape = CircleShape
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    if (!notification.isRead) {
                        IconButton(
                            onClick = { onMarkAsRead(notification.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Marcar como leída",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { onDelete(notification.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationsView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Sin notificaciones",
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No tienes notificaciones",
            style = MaterialTheme.typography.h6,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Las notificaciones aparecerán aquí cuando las recibas",
            style = MaterialTheme.typography.body2,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun NotificationSettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    var isEnabled by remember { mutableStateOf(true) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = CuiroColors.ObjectsPink,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = { isEnabled = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = CuiroColors.ObjectsPink,
                checkedTrackColor = CuiroColors.ObjectsPink.copy(alpha = 0.5f)
            )
        )
    }
}

// Data class para las notificaciones
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long,
    val isRead: Boolean = false
)

enum class NotificationType {
    INFO, SUCCESS, WARNING, ERROR, MESSAGE
}

// Funciones auxiliares
fun getNotificationIcon(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.INFO -> Icons.Default.Info
        NotificationType.SUCCESS -> Icons.Default.CheckCircle
        NotificationType.WARNING -> Icons.Default.Warning
        NotificationType.ERROR -> Icons.Default.Close
        NotificationType.MESSAGE -> Icons.Default.Email
    }
}

fun getNotificationColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.INFO -> Color.Blue
        NotificationType.SUCCESS -> Color.Green
        NotificationType.WARNING -> Color(0xFFFF9800)
        NotificationType.ERROR -> Color.Red
        NotificationType.MESSAGE -> CuiroColors.ObjectsPink
    }
}

fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "Ahora"
        diff < 3600000 -> "${diff / 60000}m"
        diff < 86400000 -> "${diff / 3600000}h"
        diff < 604800000 -> "${diff / 86400000}d"
        else -> "${diff / 604800000}sem"
    }
}

// Funciones simuladas para interactuar con el backend
fun loadNotifications(userId: String, onResult: (List<Notification>) -> Unit) {
    // Aquí implementarías la lógica para cargar notificaciones desde Firestore
    // Por ahora, datos de ejemplo:
    val sampleNotifications = listOf(
        Notification(
            id = "1",
            title = "¡Bienvenido!",
            message = "Tu cuenta ha sido creada exitosamente",
            type = NotificationType.SUCCESS,
            timestamp = System.currentTimeMillis() - 3600000,
            isRead = false
        ),
        Notification(
            id = "2",
            title = "Nuevo mensaje",
            message = "Tienes un nuevo mensaje de un usuario",
            type = NotificationType.MESSAGE,
            timestamp = System.currentTimeMillis() - 7200000,
            isRead = true
        )
    )
    onResult(sampleNotifications)
}

fun markAllAsRead(userId: String, onComplete: () -> Unit) {
    // Implementar lógica para marcar todas como leídas
    onComplete()
}

fun clearAllNotifications(userId: String, onComplete: () -> Unit) {
    // Implementar lógica para limpiar todas las notificaciones
    onComplete()
}

fun markNotificationAsRead(userId: String, notificationId: String, onComplete: () -> Unit) {
    // Implementar lógica para marcar una notificación como leída
    onComplete()
}

fun deleteNotification(userId: String, notificationId: String, onComplete: () -> Unit) {
    // Implementar lógica para eliminar una notificación
    onComplete()
}