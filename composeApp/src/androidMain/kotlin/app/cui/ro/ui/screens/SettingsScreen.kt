package app.cui.ro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.cui.ro.ui.theme.CuiroColors

@Composable
fun SettingsScreen(
    onSignOut: () -> Unit = {}
) {
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var languageSelected by remember { mutableStateOf("Español") }
    var showLanguageDialog by remember { mutableStateOf(false) }

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
            Spacer(modifier = Modifier.height(16.dp))

            // Sección de Cuenta
            SettingsSection(title = "Cuenta") {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Editar Perfil",
                    subtitle = "Cambiar información personal",
                    onClick = { /* Navegar a editar perfil */ }
                )

                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Cambiar Contraseña",
                    subtitle = "Actualizar tu contraseña",
                    onClick = { /* Navegar a cambiar contraseña */ }
                )

                SettingsItem(
                    icon = Icons.Default.Email,
                    title = "Verificar Email",
                    subtitle = "Confirmar tu dirección de correo",
                    onClick = { /* Verificar email */ }
                )
            }

            // Sección de Preferencias
            SettingsSection(title = "Preferencias") {
                SettingsItemWithSwitch(
                    icon = Icons.Default.Notifications,
                    title = "Notificaciones",
                    subtitle = "Recibir notificaciones push",
                    isChecked = notificationsEnabled,
                    onCheckedChange = {
                        notificationsEnabled = it
                        showNotificationDialog = true
                    }
                )

                SettingsItemWithSwitch(
                    icon = Icons.Default.Edit,
                    title = "Modo Oscuro",
                    subtitle = "Tema oscuro para la aplicación",
                    isChecked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it }
                )

                SettingsItem(
                    icon = Icons.Default.Edit,
                    title = "Idioma",
                    subtitle = languageSelected,
                    onClick = { showLanguageDialog = true }
                )
            }

            // Sección de Privacidad
            SettingsSection(title = "Privacidad y Seguridad") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Privacidad",
                    subtitle = "Controlar tu información personal",
                    onClick = { /* Navegar a privacidad */ }
                )

                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Usuarios Bloqueados",
                    subtitle = "Gestionar usuarios bloqueados",
                    onClick = { /* Navegar a usuarios bloqueados */ }
                )
            }

            // Sección de Soporte
            SettingsSection(title = "Soporte") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Centro de Ayuda",
                    subtitle = "Preguntas frecuentes y guías",
                    onClick = { /* Navegar a ayuda */ }
                )

                SettingsItem(
                    icon = Icons.Default.Email,
                    title = "Enviar Comentarios",
                    subtitle = "Comparte tu opinión con nosotros",
                    onClick = { /* Enviar feedback */ }
                )

                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Acerca de",
                    subtitle = "Información de la aplicación",
                    onClick = { /* Mostrar información de la app */ }
                )
            }

            // Sección de Cuenta - Acciones críticas
            SettingsSection(title = "Sesión") {
                SettingsItem(
                    icon = Icons.Default.ExitToApp,
                    title = "Cerrar Sesión",
                    subtitle = "Salir de tu cuenta",
                    onClick = { showSignOutDialog = true },
                    textColor = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Diálogo de confirmación para cerrar sesión
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = {
                Text("Cerrar Sesión")
            },
            text = {
                Text("¿Estás seguro de que quieres cerrar sesión?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        onSignOut()
                    }
                ) {
                    Text("Cerrar Sesión", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSignOutDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de notificaciones
    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = {
                Text("Notificaciones")
            },
            text = {
                Text(
                    if (notificationsEnabled)
                        "Las notificaciones han sido activadas"
                    else
                        "Las notificaciones han sido desactivadas"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showNotificationDialog = false }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Diálogo de selección de idioma
    if (showLanguageDialog) {
        val languages = listOf("Español", "English", "Français", "Português")

        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text("Seleccionar Idioma")
            },
            text = {
                Column {
                    languages.forEach { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    languageSelected = language
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = languageSelected == language,
                                onClick = {
                                    languageSelected = language
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(language)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showLanguageDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
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
                text = title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (textColor == Color.Red) Color.Red else CuiroColors.ObjectsPink,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Ir",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun SettingsItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
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
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CuiroColors.ObjectsPink,
                checkedTrackColor = CuiroColors.ObjectsPink.copy(alpha = 0.5f)
            )
        )
    }
}
