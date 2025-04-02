package app.cui.ro.ui.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import app.cui.ro.R
import app.cui.ro.auth.AuthService
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
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun NavBarScreenHome(
    authService: AuthService,
    vmHealthConnect: VMHealthConnect
) {
    val userId = remember { authService.getUserId() }
    var userFullNameDB by remember { mutableStateOf("Usuario") }
    var usernameDB by remember { mutableStateOf("Usuario") }
    var showSeccionInformacion2 by remember { mutableStateOf(false) }
    var showSeccionRecomendaciones2 by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var hasHealthConnectPermissions by remember { mutableStateOf(false) }
    var showPermissionExplanationDialog by remember { mutableStateOf(false) }

    // Inicializar el launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val allPermissionsGranted = permissions.all { it.value }
        hasHealthConnectPermissions = allPermissionsGranted

        if (!allPermissionsGranted) {
            // Verificar si el usuario ha denegado permanentemente los permisos
            val allPermissionsPreviouslyGranted = vmHealthConnect.PERMISSIONS.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }

            if (!allPermissionsPreviouslyGranted) {
                showPermissionExplanationDialog = true
            }
        }
    }

    LaunchedEffect(context) {
        vmHealthConnect.checkHealthConnectAvailability(context)
        vmHealthConnect.requestPermissions(context, requestPermissionLauncher)

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
            onMenuClick = { /* Lógica para el clic del menú */ },
            onNotificationsClick = { /* Lógica para el clic de notificaciones */ },
            title = "\"Cuidarte es luchar, resistir y vencer al cáncer de mama\"",
        )

        Column(
            modifier = Modifier
                .fillMaxWidth() // Ocupa todo el ancho
                .weight(1f) // Ocupa todo el espacio vertical restante bajo el TopAppBar
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
                    .verticalScroll(rememberScrollState())
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

                // Mostrar RegistroInformacion solo si showSeccionInformacion2 es false
                if (!showSeccionInformacion2) {
                    SeccionInformacion(
                        onVerMasClick = {
                            showSeccionInformacion2 = true
                        }
                    )
                }

                // Mostrar SeccionInformacion2 solo si showSeccionInformacion2 es true
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

                // Lógica del diálogo de permisos (no afecta el layout visible)
                if (showPermissionExplanationDialog) {
                    AlertDialog(
                        modifier = Modifier.clip(RoundedCornerShape(10)),
                        onDismissRequest = { showPermissionExplanationDialog = false },
                        title = { Text("Permisos Requeridos") },
                        text = { Text("Para acceder a tus datos de pasos, necesitamos permisos de Health Connect. Por favor, habilítalos en la configuración de la aplicación.") },
                        buttons = {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data = Uri.fromParts("package", context.packageName, null)
                                        }
                                        ContextCompat.startActivity(context, intent, null)
                                        showPermissionExplanationDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(CuiroColors.ObjectsPink)
                                ) {
                                    Text("Ir a Configuración")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { showPermissionExplanationDialog = false },
                                    colors = ButtonDefaults.buttonColors(CuiroColors.ObjectsPink)
                                ) {
                                    Text("Cancelar")
                                }
                            }
                        }
                    )
                }
            }

            SeccionRegistroDiario(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f),
                authService = authService,
                hasPermissions = hasHealthConnectPermissions,
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
    modifier: Modifier = Modifier,
    authService: AuthService,
    hasPermissions: Boolean,
    requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
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
        modifier = modifier
            .background(color = Color.Black)
            .height(IntrinsicSize.Min)
    ) {
        SeccionMedicamentos(
            userFirstName = userFirstName,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(CuiroColors.SectionsPink)
                .padding(5.dp)
        )

        VerticalDivider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier.fillMaxHeight()
        )

        SeccionPasosEHidratacion(
            userFirstName = userFirstName,
            hasPermissions = hasPermissions,
            requestPermissionLauncher = requestPermissionLauncher,
            vmHealthConnect = vmHealthConnect,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
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
            modifier = Modifier.clickable { onVerMasClick() } // Manejar el clic en "Ver más"
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

    // Modified section to prevent text from pushing images up.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceAround // This is crucial!
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
            modifier = Modifier.clickable { onVerMasClick() } // Manejar el clic en "Ver más"
        ) {
            Text(
                text = "Ver más...",
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color.Gray,
            )
            Spacer(modifier = Modifier.width(4.dp)) // Añade un pequeño espacio entre el texto y la imagen
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
        horizontalArrangement = Arrangement.SpaceAround // This is crucial!
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
    hasPermissions: Boolean,
    modifier: Modifier = Modifier,
    requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
    vmHealthConnect: VMHealthConnect
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
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

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            )
            {
                Column(
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(vertical = 10.dp)
                ) {
                    if (hasPermissions && vmHealthConnect.healthConnectAvailability.collectAsState().value == VMHealthConnect.HealthConnectAvailability.AVAILABLE) {
                        MedicionPasos(
                            authService = AuthService(),
                            requestPermissionLauncher = requestPermissionLauncher,
                            vmHealthConnect = vmHealthConnect
                        )
                    } else {
                        Text(
                            "Es necesario conceder permisos para ver este apartado",
                            fontSize = 12.sp,
                            color = Color.Black,
                        )
                    }
                }
            }
        }

        Divider(
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )

        // Sección de Hidratación
        Column(modifier = Modifier.fillMaxWidth()) {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$userFirstName, hoy no has registrado tu consumo de agua, registralo.",
                    fontSize = 12.sp,
                    color = Color.Black,
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
        horizontalArrangement = Arrangement.SpaceAround // This is crucial!
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
        horizontalArrangement = Arrangement.SpaceAround // This is crucial!
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
    authService: AuthService,
    requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
    vmHealthConnect: VMHealthConnect = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val userId = remember { authService.getUserId() }
    var userFirstName by remember { mutableStateOf("Usuario") }
    var stepsCount by remember { mutableIntStateOf(0) }
    val healthConnectAvailability by vmHealthConnect.healthConnectAvailability.collectAsState()
    val hasHealthConnectPermissions by vmHealthConnect.hasHealthConnectPermissions.collectAsState()

    LaunchedEffect(healthConnectAvailability) {
        if (healthConnectAvailability == VMHealthConnect.HealthConnectAvailability.AVAILABLE) {
            vmHealthConnect.requestPermissions(
                context,
                requestPermissionLauncher
            )
        }
    }

    LaunchedEffect(hasHealthConnectPermissions) {
        if (hasHealthConnectPermissions && healthConnectAvailability == VMHealthConnect.HealthConnectAvailability.AVAILABLE) {
            try {
                val healthClient = HealthConnectClient.getOrCreate(context)
                val today = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                val steps = vmHealthConnect.readStepsForDate(healthClient, today)
                stepsCount = steps.toInt()
            } catch (e: SecurityException) {
                Log.e("MedicionPasos", "Security exception: ${e.message}")
                stepsCount = 0
            } catch (e: IllegalStateException) {
                Log.e("MedicionPasos", "Service not available: ${e.message}")
                stepsCount = 0
            }
        }
    }

    LaunchedEffect(userId) {
        if (userId != null) {
            authService.getUserFirstName(userId) { name ->
                if (name != null) {
                    userFirstName = name
                }
            }
        }
    }

    when (healthConnectAvailability) {
        VMHealthConnect.HealthConnectAvailability.NOT_CHECKED -> {
            Text("Checking Health Connect availability...", color = Color.Black, fontSize = 12.sp)
        }

        VMHealthConnect.HealthConnectAvailability.NOT_INSTALLED -> {
            Text(
                text = "Health Connect is not installed. Please install it to view your steps.",
                color = Color.Black,
                fontSize = 12.sp
            )
        }

        VMHealthConnect.HealthConnectAvailability.UPDATE_REQUIRED -> {
            Text(
                text = "Health Connect requires an update. Please update it to view your steps.",
                color = Color.Black,
                fontSize = 12.sp
            )
        }

        VMHealthConnect.HealthConnectAvailability.AVAILABLE -> {
            if (hasHealthConnectPermissions) {
                Text(
                    "$userFirstName, hoy has dado $stepsCount pasos. ¡Ánimo, tú puedes dar algunos más!",
                    fontSize = 12.sp,
                    color = Color.Black,
                )
            } else {
                Text(
                    text = "Please grant Health Connect permissions to view your steps.",
                    color = Color.Black,
                    fontSize = 12.sp
                )
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
            val maxFileSizeInBytes = 3000 * 1024 // 1MB

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

