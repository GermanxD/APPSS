package app.cui.ro.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.room.Room
import app.cui.ro.R
import app.cui.ro.auth.AuthService
import app.cui.ro.db.AppDatabase
import app.cui.ro.db.HidratacionEntity
import app.cui.ro.models.VMHealthConnect
import app.cui.ro.ui.theme.CuiroColors
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

@Composable
fun NavBarScreenContact(
    authService: AuthService,
    vmHealthConnect: VMHealthConnect = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val hasPermissions by vmHealthConnect.hasHealthConnectPermissions.collectAsState()
    val availability by vmHealthConnect.healthConnectAvailability.collectAsState()
    var showPermissionExplanationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions ->
        vmHealthConnect.updatePermissionsState(context)
        val allRequiredPermissionsGranted =
            vmHealthConnect.PERMISSIONS.all { it in grantedPermissions }
        if (!allRequiredPermissionsGranted) {
            showPermissionExplanationDialog = true
        }
    }

    LaunchedEffect(availability, hasPermissions) {
        if (availability == VMHealthConnect.HealthConnectAvailability.AVAILABLE && !hasPermissions) {
            vmHealthConnect.requestPermissions(context, requestPermissionLauncher)
        }
    }

    LaunchedEffect(Unit) {
        vmHealthConnect.checkHealthConnectAvailability(context)
    }

    // Registro Diario
    SeccionRegistroDiario(
        modifier = Modifier.fillMaxWidth(),
        authService = authService,
        requestPermissionLauncher = requestPermissionLauncher,
        vmHealthConnect = vmHealthConnect
    )
}

@Composable
fun SeccionRegistroDiario(
    modifier: Modifier = Modifier,
    authService: AuthService,
    requestPermissionLauncher: ActivityResultLauncher<Set<String>>,
    vmHealthConnect: VMHealthConnect
) {
    val userId = remember { authService.getUserId() }
    var userFirstName by remember { mutableStateOf("Usuario") }
    val context = LocalContext.current

    LaunchedEffect(userId) {
        if (userId != null) {
            authService.getUserFirstName(userId) { name ->
                if (name != null) {
                    userFirstName = name
                }
            }
        }
    }

    val db = remember {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "HidratacioDB"
        ).build()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        SeccionMedicamentos(
            userFirstName = userFirstName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        SeccionPasos(
            userFirstName = userFirstName,
            requestPermissionLauncher = requestPermissionLauncher,
            vmHealthConnect = vmHealthConnect,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        SeccionHidratacion(
            userFirstName = userFirstName,
            database = db,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
    }
}

@Composable
fun SeccionMedicamentos(userFirstName: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Medicamentos",
                    fontSize = 16.sp,
                    color = Color(0xFF333333),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Image(
                    painter = painterResource(R.drawable.ic_medicamentos),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Text(
                    text = "$userFirstName, el siguiente medicamento es:",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tamoxifeno (Nolvadex)",
                    fontSize = 14.sp,
                    color = Color(0xFF222222),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hora: 12:00 hrs",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "Recordarme: Sí",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color(0xFFE0E0E0))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Información del medicamento aquí",
                    fontSize = 14.sp,
                    color = Color(0xFF555555),
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun SeccionPasos(
    userFirstName: String,
    requestPermissionLauncher: ActivityResultLauncher<Set<String>>,
    vmHealthConnect: VMHealthConnect,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Pasos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Image(
                    painter = painterResource(R.drawable.ic_pasos),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            MedicionPasos(
                userFirstName = userFirstName,
                requestPermissionLauncher = requestPermissionLauncher,
                vmHealthConnect = vmHealthConnect
            )
        }
    }
}

@Composable
fun SeccionHidratacion(
    userFirstName: String,
    database: AppDatabase,
    modifier: Modifier = Modifier
) {
    val dao = database.hidratacionDao()
    val scope = rememberCoroutineScope()
    var cantidadMl by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val progreso = dao.getProgreso()
        cantidadMl = progreso?.cantidadMl ?: 0
    }

    Card(
        modifier = modifier
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Hidratación",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Image(
                    painter = painterResource(R.drawable.ic_persona_agua),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedContent(
                targetState = cantidadMl >= 1800,
                transitionSpec = {
                    fadeIn(tween(500)) togetherWith fadeOut(tween(500))
                },
                label = "HidratacionContent"
            ) { completo ->
                if (completo) {
                    Text(
                        text = "¡Felicidades! Has completado tu día.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF388E3C),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    LinearProgressIndicator(
                        progress = { (cantidadMl / 1800f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = Color(0xFF4FC3F7),
                        trackColor = Color(0xFFE0E0E0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$userFirstName, has tomado ${cantidadMl}ml de agua.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Añadir agua",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            scope.launch {
                                val nuevoProgreso = (cantidadMl + 200).coerceAtMost(1800)
                                dao.insertarProgreso(HidratacionEntity(cantidadMl = nuevoProgreso))
                                cantidadMl = nuevoProgreso
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun MedicionPasos(
    userFirstName: String,
    requestPermissionLauncher: ActivityResultLauncher<Set<String>>,
    vmHealthConnect: VMHealthConnect
) {
    var stepsCount by remember { mutableStateOf<Long?>(null) }
    val healthConnectAvailability by vmHealthConnect.healthConnectAvailability.collectAsState()
    val hasPermissions by vmHealthConnect.hasHealthConnectPermissions.collectAsState()
    var showInstallDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showPermissionExplanationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vmHealthConnect.checkHealthConnectAvailability(context)
    }

    if (showPermissionExplanationDialog) {
        AlertDialog(
            modifier = Modifier.clip(RoundedCornerShape(10.dp)),
            onDismissRequest = { showPermissionExplanationDialog = false },
            title = { Text("Permisos Requeridos") },
            text = {
                Text("Para acceder a tus datos de pasos, necesitamos permisos de Health Connect. Por favor, habilítalos en la configuración de la aplicación.")
            },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data =
                                        Uri.fromParts("package", context.packageName, null)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Log.e("NavBarScreenHome", "Could not open app settings", e)
                            }
                            showPermissionExplanationDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = CuiroColors.ObjectsPink),
                    ) {
                        Text(
                            "Ir a Configuración",
                            fontSize = 16.sp,
                            color = CuiroColors.FontBrown
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { showPermissionExplanationDialog = false },
                        colors = ButtonDefaults.buttonColors(backgroundColor = CuiroColors.ObjectsPink),
                    ) {
                        Text("Cancelar", fontSize = 16.sp, color = CuiroColors.FontBrown)
                    }
                }
            }
        )
    }

    if (showInstallDialog) {
        AlertDialog(
            onDismissRequest = { showInstallDialog = false },
            title = { Text("Health Connect Requerido") },
            text = { Text("Para medir tus pasos necesitas instalar o actualizar Health Connect. ¿Quieres ir a la tienda?") },
            confirmButton = {
                Button(
                    onClick = {
                        showInstallDialog = false
                        vmHealthConnect.openHealthConnectSettings(context)
                    },
                ) {
                    Text("Ir a Tienda")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showInstallDialog = false },
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Efecto para leer los pasos cuando los permisos están concedidos y HC está disponible
    LaunchedEffect(hasPermissions, healthConnectAvailability) {
        if (hasPermissions && healthConnectAvailability == VMHealthConnect.HealthConnectAvailability.AVAILABLE) {
            isLoading = true
            try {
                val healthClient = HealthConnectClient.getOrCreate(context)
                val today = ZonedDateTime.now()
                stepsCount = vmHealthConnect.readStepsForDate(healthClient, today)
            } catch (e: Exception) {
                Log.e("MedicionPasos", "Failed to get HealthConnectClient or read steps", e)
                stepsCount = null
            } finally {
                isLoading = false
            }
        } else {
            // Si cambian los permisos o disponibilidad y ya no se cumplen, resetea los pasos
            stepsCount = null
            isLoading = false
        }
    }

    when (healthConnectAvailability) {
        VMHealthConnect.HealthConnectAvailability.NOT_CHECKED -> {
            Text("Verificando Health Connect...", color = Color.Gray, fontSize = 12.sp)
        }

        VMHealthConnect.HealthConnectAvailability.NOT_INSTALLED -> {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Instala Health Connect para ver tus pasos.",
                    color = Color.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Button(
                    onClick = { vmHealthConnect.openHealthConnectSettings(context) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Instalar", fontSize = 12.sp)
                }
            }
        }

        VMHealthConnect.HealthConnectAvailability.UPDATE_REQUIRED -> {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Health Connect necesita actualizarse.",
                    color = Color.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Button(
                    onClick = { vmHealthConnect.openHealthConnectSettings(context) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Actualizar", fontSize = 12.sp)
                }
            }
        }

        VMHealthConnect.HealthConnectAvailability.AVAILABLE -> {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else if (hasPermissions) {
                // Permisos concedidos -> Mostrar pasos
                val stepsText = stepsCount?.let { count ->
                    "$userFirstName, hoy has dado $count pasos. ¡Sigue así!"
                }
                    ?: "$userFirstName, no pudimos leer tus pasos hoy."
                Text(
                    text = stepsText,
                    fontSize = 12.sp,
                    color = Color.Black,
                )
            } else {
                // Disponible pero sin permisos -> Mostrar botón para solicitarlos
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(vertical = 20.dp)
                ) {
                    Text(
                        text = "Concede permisos para ver tus pasos.",
                        color = Color.Black,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Button(
                        onClick = {
                            vmHealthConnect.requestPermissions(context, requestPermissionLauncher)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = CuiroColors.ObjectsPink,
                            contentColor = CuiroColors.FontBrown
                        ),
                    ) {
                        Text(
                            "Conceder Permisos",
                            fontSize = 12.sp,
                            color = CuiroColors.FontBrown,
                            fontFamily = FontFamily.Default,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}