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
import androidx.compose.foundation.lazy.items // Importante: items para Long id
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
import androidx.compose.material.Switch // Mantén esto si lo usas
import androidx.compose.material.SwitchDefaults // Mantén esto si lo usas
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications // Icono para Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf // Para settings si no están en VM
import androidx.compose.runtime.remember // Para settings si no están en VM
import androidx.compose.runtime.setValue // Para settings si no están en VM
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
import app.cui.ro.db.Notification // Asegúrate que la ruta sea correcta
import app.cui.ro.models.NotificationViewModel
import app.cui.ro.ui.theme.CuiroColors

@Composable
fun NotificationsScreen(
    authService: AuthService,
    notificationsViewModel: NotificationViewModel = viewModel()
) {
    val userId = remember { authService.getUserId() }
    var pushEnabled by remember { mutableStateOf(true) }
    var emailEnabled by remember { mutableStateOf(false) }
    val notifications by notificationsViewModel.notifications.collectAsState()
    val isLoading by notificationsViewModel.isLoading.collectAsState()
    val unreadCount by notificationsViewModel.unreadCount.collectAsState()

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
                        color = MaterialTheme.colors.onSurface
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
                                notificationsViewModel.markAllAsRead()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = CuiroColors.ObjectsPink
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = unreadCount > 0 // El ViewModel actualiza unreadCount
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
                                notificationsViewModel.clearAllNotifications()
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
                            items(notifications, key = { it.id }) { notification ->
                                NotificationItem(
                                    notification = notification,
                                    onMarkAsRead = { notificationId ->
                                        notificationsViewModel.markNotificationAsRead(notificationId)
                                    },
                                    onDelete = { notificationId ->
                                        notificationsViewModel.deleteNotification(notificationId)
                                    }
                                )
                            }
                        }
                    }
                }
            }

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
                        subtitle = "Recibir notificaciones en tiempo real",
                        isChecked = pushEnabled,
                        onCheckedChange = {
                            pushEnabled = it
                            if (it) {
                                notificationsViewModel.subscribeToTopic()
                            } else {
                                notificationsViewModel.unsubscribeFromTopic()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    NotificationSettingRow(
                        icon = Icons.Default.Email,
                        title = "Notificaciones por Email",
                        subtitle = "Recibir resumen diario por correo",
                        isChecked = emailEnabled,
                        onCheckedChange = { emailEnabled = it }
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
    onMarkAsRead: (Long) -> Unit,
    onDelete: (Long) -> Unit
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
    return when (type.toLowerCase()) {
        "daily_hydration", "info" -> Color(0xFF4CAF50)
        "warning" -> Color(0xFFFFC107)
        "error" -> Color(0xFFF44336)
        else -> Color.Gray
    }
}

fun getNotificationIconByType(type: String): ImageVector {
    return when (type.toLowerCase()) {
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
