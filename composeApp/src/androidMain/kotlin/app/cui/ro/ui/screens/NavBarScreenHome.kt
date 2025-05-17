package app.cui.ro.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.room.Room
import app.cui.ro.R
import app.cui.ro.auth.AuthService
import app.cui.ro.db.AppDatabase
import app.cui.ro.db.HidratacionEntity
import app.cui.ro.models.VMHealthConnect
import app.cui.ro.models.VMProfileImage
import app.cui.ro.ui.CustomTopAppBar
import app.cui.ro.ui.DataColumn
import app.cui.ro.ui.theme.CuiroColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.util.Locale

@Composable
fun NavBarScreenHome(
    onMenuClick: () -> Unit,
    authService: AuthService,
    vmHealthConnect: VMHealthConnect = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val userId = remember { authService.getUserId() }
    var userFullNameDB by remember { mutableStateOf("Usuario") }
    var usernameDB by remember { mutableStateOf("Usuario") }
    var showSeccionInformacion2 by remember { mutableStateOf(false) }
    var showSeccionRecomendaciones2 by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showPermissionExplanationDialog by remember { mutableStateOf(false) }

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

    LaunchedEffect(Unit) {
        vmHealthConnect.checkHealthConnectAvailability(context)
    }

    LaunchedEffect(userId) {
        if (userId != null) {
            authService.getAllData(userId) { userFullName, username ->
                if (userFullName != null && username != null) {
                    userFullNameDB = userFullName
                    usernameDB = username
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomTopAppBar(
            onMenuClick = onMenuClick,
            onNotificationsClick = { /* Lógica para el clic de notificaciones */ },
            title = "\"Cuidarte es luchar, resistir y vencer al cáncer de mama\"",
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    userId?.let {
                        ProfileScreen(
                            userId = it,
                            vmProfileImage = VMProfileImage(),
                        )
                    }
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = userFullNameDB,
                            textAlign = TextAlign.Left,
                            style = MaterialTheme.typography.body1
                        )
                        Text(
                            text = "@$usernameDB".lowercase(Locale.getDefault()),
                            textAlign = TextAlign.Left,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .background(color = CuiroColors.SecondaryRose)
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = "Icono de Busqueda",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(36.dp),
                        tint = Color.White,
                    )

                    Text(
                        text = "¿Necesitas ayuda?",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                if (!showSeccionInformacion2) {
                    SeccionInformacion(
                        onVerMasClick = {
                            showSeccionInformacion2 = true
                        }
                    )
                }

                if (showSeccionInformacion2) {
                    SeccionInformacion2(
                        onDismiss = {
                            showSeccionInformacion2 = false
                        }
                    )
                }

                if (!showSeccionRecomendaciones2) {
                    SeccionRecomendaciones(
                        onVerMasClick = {
                            showSeccionRecomendaciones2 = true
                        }
                    )
                }

                if (showSeccionRecomendaciones2) {
                    SeccionRecomendaciones2(
                        onDismiss = {
                            showSeccionRecomendaciones2 = false
                        }
                    )
                }

                if (showPermissionExplanationDialog) {
                    AlertDialog(
                        modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                        onDismissRequest = { showPermissionExplanationDialog = false },
                        title = { Text("Permisos Requeridos") },
                        text = { Text("Para acceder a tus datos de pasos, necesitamos permisos de Health Connect. Por favor, habilítalos en la configuración de la aplicación.") },
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
                                                data = Uri.fromParts(
                                                    "package",
                                                    context.packageName,
                                                    null
                                                )
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Log.e(
                                                "NavBarScreenHome",
                                                "Could not open app settings",
                                                e
                                            )
                                        }
                                        showPermissionExplanationDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = CuiroColors.ObjectsPink),
                                ) {
                                    Text(
                                        "Ir a Configuración",
                                        fontSize = 16.sp,
                                        color = CuiroColors.FontBrown,
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { showPermissionExplanationDialog = false },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = CuiroColors.ObjectsPink),
                                ) {
                                    Text(
                                        "Cancelar",
                                        fontSize = 16.sp,
                                        color = CuiroColors.FontBrown
                                    )
                                }
                            }
                        }
                    )
                }
            }

            SeccionRegistroDiario(
                modifier = Modifier
                    .fillMaxWidth(),
                authService = authService,
                requestPermissionLauncher = requestPermissionLauncher,
                vmHealthConnect = vmHealthConnect
            )

        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.BottomCenter)
        ) { snackbarData ->
            Snackbar(snackbarData)
        }
    }
}

@Composable
fun SeccionRegistroDiario(
    modifier: Modifier = Modifier, // Ahora solo recibe .fillMaxWidth() desde el padre
    authService: AuthService,
    requestPermissionLauncher: ActivityResultLauncher<Set<String>>,
    vmHealthConnect: VMHealthConnect
) {
    val userId = remember { authService.getUserId() }
    var userFirstName by remember { mutableStateOf("Usuario") }
    LaunchedEffect(userId) {
        if (userId != null) {
            authService.getUserFirstName(userId) { name ->
                if (name != null) {
                    userFirstName = name
                }
            }
        }
    }

    Row(
        modifier = modifier // Este modifier ya tiene fillMaxWidth()
            .background(color = Color.Black)
            .height(IntrinsicSize.Min) // La Row será tan alta como su hijo más alto.
    ) {
        SeccionMedicamentos(
            userFirstName = userFirstName,
            modifier = Modifier
                .fillMaxHeight() // Se expandirá a la altura de la Row (IntrinsicSize.Min).
                .weight(1f)      // Para la distribución del ancho.
                .background(CuiroColors.SectionsPink)
                .padding(5.dp)
        )
        VerticalDivider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier.fillMaxHeight() // El divisor llenará la altura de la Row.
        )
        SeccionPasosEHidratacion(
            userFirstName = userFirstName,
            requestPermissionLauncher = requestPermissionLauncher,
            vmHealthConnect = vmHealthConnect,
            modifier = Modifier
                .fillMaxHeight() // Se expandirá a la altura de la Row.
                .weight(1f)      // Para la distribución del ancho.
                .background(CuiroColors.SectionsPink)
                .padding(5.dp)
        )
    }
}

@Composable
fun SeccionInformacion(
    onVerMasClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Registro de información",
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onVerMasClick() }
        ) {
            Text(
                text = "Ver más...",
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color.Gray,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = "",
                modifier = Modifier.size(20.dp),
                tint = Color.Gray,
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        DataColumn(
            imageResId = R.drawable.ic_datos_clinicos,
            text = "Datos clinicos"
        )
        DataColumn(
            imageResId = R.drawable.ic_efectos_del_tratamiento,
            text = "Efectos del tratamiento"
        )
        DataColumn(
            imageResId = R.drawable.ic_medicamentos,
            text = "Medicamentos"
        )
    }
}

@Composable
fun SeccionRecomendaciones(
    onVerMasClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Recomendaciones sobre...",
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onVerMasClick() }
        ) {
            Text(
                text = "Ver más...",
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color.Gray,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = "",
                modifier = Modifier.size(20.dp),
                tint = Color.Gray,
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        DataColumn(
            imageResId = R.drawable.ic_informacion,
            text = "Informacion"
        )
        DataColumn(
            imageResId = R.drawable.ic_quimioterapia,
            text = "Quimioterapia"
        )
        DataColumn(
            imageResId = R.drawable.ic_nutricion,
            text = "Nutricion"
        )
    }
}

@Composable
fun SeccionMedicamentos(userFirstName: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Medicamentos",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Image(
                painter = painterResource(R.drawable.ic_medicamentos),
                contentDescription = "",
                modifier = Modifier.size(50.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Text(
                text = "$userFirstName, el siguiente medicamento es:",
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Tamoxifeno (Nolvadex):",
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Hora: 12:00 hrs",
                fontSize = 12.sp,
                color = Color.Black
            )
            Text(
                "Recordarme: Si",
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Informacion del medicamento aqui",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = "",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun SeccionPasosEHidratacion(
    userFirstName: String,
    requestPermissionLauncher: ActivityResultLauncher<Set<String>>,
    vmHealthConnect: VMHealthConnect,
    modifier: Modifier = Modifier, // Este modifier incluye .fillMaxHeight() y .weight(1f para ancho)
) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "HidratacioDB"
        ).build()
    }

    Column(
        modifier = modifier, // Aplica el .fillMaxHeight() y .weight(1f para ancho)
        horizontalAlignment = Alignment.Start
    ) {
        SeccionPasos( // Tomará la altura que necesite su contenido.
            userFirstName = userFirstName,
            requestPermissionLauncher = requestPermissionLauncher,
            vmHealthConnect = vmHealthConnect
        )
        Divider(
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )
        SeccionHidratacion( // Tomará la altura que necesite su contenido.
            userFirstName = userFirstName,
            database = db,
            modifier = Modifier.fillMaxWidth() // Sin weight vertical.
        )
    }
}

@Composable
fun SeccionPasos(
    userFirstName: String,
    requestPermissionLauncher: ActivityResultLauncher<Set<String>>,
    vmHealthConnect: VMHealthConnect
){
    // Sección de Pasos
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pasos",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Image(
                painter = painterResource(R.drawable.ic_pasos),
                contentDescription = "",
                modifier = Modifier.size(50.dp)
            )
        }

        // Contenedor para MedicionPasos y lógica relacionada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.CenterStart // O el que prefieras
        ) {
            // El Composable MedicionPasos ahora maneja toda la lógica de UI de HC
            MedicionPasos(
                userFirstName = userFirstName,
                requestPermissionLauncher = requestPermissionLauncher,
                vmHealthConnect = vmHealthConnect
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SeccionHidratacion(
    userFirstName: String,
    database: AppDatabase,
    modifier: Modifier = Modifier
) {
    val dao = database.hidratacionDao()
    val scope = rememberCoroutineScope()
    var cantidadMl by remember { mutableStateOf(0) }

    // Leer el progreso al arrancar
    LaunchedEffect(Unit) {
        val progreso = dao.getProgreso()
        cantidadMl = progreso?.cantidadMl ?: 0
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hidratacion",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Image(
                painter = painterResource(R.drawable.ic_persona_agua),
                contentDescription = "",
                modifier = Modifier.size(50.dp)
            )
        }

        AnimatedContent(
            targetState = cantidadMl >= 1800,
            transitionSpec = {
                fadeIn(tween(500)) with fadeOut(tween(500))
            },
            label = "HidratacionContent",
            modifier = Modifier.padding(vertical = 8.dp) // Padding para el contenido animado
        ) { completo ->
            if (completo) {
                Text(
                    text = "¡Felicidades!, has completado tu día",
                    fontSize = 14.sp,
                    color = Color(0xFF388E3C),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp), // Espacio antes de esta fila
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$userFirstName, has tomado ${cantidadMl}ml de agua.",
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f) // Para que el texto ocupe el espacio y empuje el botón
            )
            Image(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = "Añadir agua", // Buena práctica: añadir contentDescription
                modifier = Modifier
                    .size(30.dp)
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


@Composable
fun SeccionInformacion2(
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Registro de información",
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Ver menos...",
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color.Gray,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "",
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onDismiss() },
                tint = Color.Gray,
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        DataColumn(
            imageResId = R.drawable.ic_hidratacion,
            text = "Hidratación"
        )
        DataColumn(
            imageResId = R.drawable.ic_signos_vitales,
            text = "Signos vitales"
        )
        DataColumn(
            imageResId = R.drawable.ic_reporte_salud,
            text = "Reporte de salud"
        )
    }
}

@Composable
fun SeccionRecomendaciones2(
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Recomendaciones sobre...",
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Ver menos...",
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color.Gray,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "",
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onDismiss() },
                tint = Color.Gray,
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        DataColumn(
            imageResId = R.drawable.ic_ejercicio_fisico,
            text = "Ejercicio fisico"
        )
        DataColumn(
            imageResId = R.drawable.ic_sexualidad,
            text = "Sexualidad"
        )
    }
}

@Composable
fun MedicionPasos(
    userFirstName: String,
    requestPermissionLauncher: ActivityResultLauncher<Set<String>>,
    vmHealthConnect: VMHealthConnect
) {
    val context = LocalContext.current
    var stepsCount by remember { mutableStateOf<Long?>(null) }
    val healthConnectAvailability by vmHealthConnect.healthConnectAvailability.collectAsState()
    val hasPermissions by vmHealthConnect.hasHealthConnectPermissions.collectAsState()
    var showInstallDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vmHealthConnect.checkHealthConnectAvailability(context)
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
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = CuiroColors.ObjectsPink,
                            contentColor = CuiroColors.FontBrown
                        ),
                    ) {
                        Text(
                            "Conceder Permiso",
                            fontSize = 12.sp,
                            color = CuiroColors.FontBrown,
                            fontFamily = FontFamily.Default,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    userId: String,
    vmProfileImage: VMProfileImage,
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    var uploadState by remember { mutableStateOf(VMProfileImage.ImageUploadState.IDLE) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        vmProfileImage.loadProfileImageFromFirestore(userId) { image ->
            base64Image = image
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            imageUri = selectedUri
            val fileSizeInBytes = vmProfileImage.getFileSize(selectedUri, context)
            val maxFileSizeInBytes = 3000 * 1024

            if (fileSizeInBytes > maxFileSizeInBytes) {
                uploadState = VMProfileImage.ImageUploadState.SIZE_EXCEEDED
                errorMessage = "La imagen excede el tamaño máximo permitido (3MB)."
                dialogMessage = "La imagen excede el tamaño máximo permitido (3MB)."
                isSuccess = false
                showDialog = true
                scope.launch {
                    delay(1000)
                    showDialog = false
                }
            } else {
                uploadState = VMProfileImage.ImageUploadState.UPLOADING
                errorMessage = ""
                CoroutineScope(Dispatchers.IO).launch {
                    val base64 = vmProfileImage.convertImageToBase64(selectedUri, context)
                    withContext(Dispatchers.Main) {
                        vmProfileImage.saveImageToFirestore(userId, base64,
                            onSuccess = {
                                base64Image = base64
                                uploadState = VMProfileImage.ImageUploadState.SUCCESS
                                dialogMessage = "Imagen actualizada con éxito!"
                                isSuccess = true
                                showDialog = true
                                scope.launch {
                                    delay(1000)
                                    showDialog = false
                                }
                            },
                            onFailure = {
                                uploadState = VMProfileImage.ImageUploadState.ERROR
                                errorMessage = "Error al guardar la imagen. Intenta de nuevo."
                                dialogMessage = "Error al guardar la imagen. Intenta de nuevo."
                                isSuccess = false
                                showDialog = true
                                scope.launch {
                                    delay(1000)
                                    showDialog = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.size(80.dp)) {
        if (!base64Image.isNullOrEmpty()) {
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .clickable { launcher.launch("image/*") }
            )

            if (uploadState == VMProfileImage.ImageUploadState.UPLOADING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(80.dp),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colors.primary
                )
            }
        } else {
            if (uploadState == VMProfileImage.ImageUploadState.UPLOADING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(80.dp),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colors.primary
                )
            } else {
                Image(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") }
                )
            }
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Card(
                    backgroundColor = CuiroColors.SecondaryRose,
                    contentColor = Color.Black,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(50)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val icon =
                                if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Close
                            val tint = if (isSuccess) Color.Green else Color.Red
                            Icon(
                                imageVector = icon,
                                contentDescription = if (isSuccess) "Éxito" else "Error",
                                tint = tint,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = dialogMessage,
                                fontSize = 16.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}


