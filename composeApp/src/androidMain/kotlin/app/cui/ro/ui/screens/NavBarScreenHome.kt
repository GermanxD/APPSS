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
import androidx.compose.ui.draw.shadow
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
import app.cui.ro.ui.DataColumn
import app.cui.ro.ui.theme.CuiroColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

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
    val hasPermissions by vmHealthConnect.hasHealthConnectPermissions.collectAsState()
    val availability by vmHealthConnect.healthConnectAvailability.collectAsState()

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

    LaunchedEffect(userId) {
        if (userId != null) {
            authService.getAllUserData(userId) { data ->
                data["fullname"]?.let { userFullNameDB = it }
                data["username"]?.let { usernameDB = it }
            }
        }
    }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {

            // Banner de ayuda
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = 4.dp,
                backgroundColor = CuiroColors.SecondaryRose
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Icono de Búsqueda",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )

                    Text(
                        text = "¿Necesitas ayuda?",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }

            // Secciones
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
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
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
                                Text("Ir a Configuración", fontSize = 16.sp, color = CuiroColors.FontBrown)
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

            // Registro Diario
            SeccionRegistroDiario(
                modifier = Modifier.fillMaxWidth(),
                authService = authService,
                requestPermissionLauncher = requestPermissionLauncher,
                vmHealthConnect = vmHealthConnect
            )
        }

        // Snackbar
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
        SeccionPasosEHidratacion(
            userFirstName = userFirstName,
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Registro de información",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = CuiroColors.FontBrown
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onVerMasClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Ver más",
                    fontSize = 14.sp,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DataColumn(
                    imageResId = R.drawable.ic_datos_clinicos,
                    text = "Datos clínicos"
                )
                DataColumn(
                    imageResId = R.drawable.ic_efectos_del_tratamiento,
                    text = "Efectos",
                )
                DataColumn(
                    imageResId = R.drawable.ic_medicamentos,
                    text = "Medicamentos"
                )
            }
        }
    }
}

@Composable
fun SeccionRecomendaciones(
    onVerMasClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Recomendaciones sobre...",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = CuiroColors.FontBrown
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onVerMasClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Ver más",
                    fontSize = 14.sp,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DataColumn(
                    imageResId = R.drawable.ic_informacion,
                    text = "Información"
                )
                DataColumn(
                    imageResId = R.drawable.ic_quimioterapia,
                    text = "Quimioterapia"
                )
                DataColumn(
                    imageResId = R.drawable.ic_nutricion,
                    text = "Nutrición"
                )
            }
        }
    }
}

@Composable
fun SeccionInformacion2(
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Registro de información",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = CuiroColors.FontBrown,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDismiss() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Ver menos",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
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
    }
}

@Composable
fun SeccionRecomendaciones2(
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Recomendaciones sobre...",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = CuiroColors.FontBrown,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDismiss() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Ver menos",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DataColumn(
                    imageResId = R.drawable.ic_ejercicio_fisico,
                    text = "Ejercicio físico"
                )
                DataColumn(
                    imageResId = R.drawable.ic_sexualidad,
                    text = "Sexualidad"
                )
            }
        }
    }
}

@Composable
fun SeccionMedicamentos(userFirstName: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
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
fun SeccionPasosEHidratacion(
    userFirstName: String,
    requestPermissionLauncher: ActivityResultLauncher<Set<String>>,
    vmHealthConnect: VMHealthConnect,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "HidratacioDB"
        ).build()
    }

    Column(modifier = modifier) {
        SeccionPasos(userFirstName, requestPermissionLauncher, vmHealthConnect)
        Spacer(modifier = Modifier.height(8.dp))
        SeccionHidratacion(userFirstName, db)
    }
}


@Composable
fun SeccionPasos(
    userFirstName: String,
    requestPermissionLauncher: ActivityResultLauncher<Set<String>>,
    vmHealthConnect: VMHealthConnect
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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

    LaunchedEffect(Unit) {
        val progreso = dao.getProgreso()
        cantidadMl = progreso?.cantidadMl ?: 0
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
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
                    fadeIn(tween(500)) with fadeOut(tween(500))
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

@Composable
fun ProfileScreen(
    userId: String,
    vmProfileImage: VMProfileImage,
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val base64Image by vmProfileImage.profileImage

    val context = LocalContext.current
    var uploadState by remember { mutableStateOf(VMProfileImage.ImageUploadState.IDLE) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

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
                dialogMessage = errorMessage
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
                                dialogMessage = errorMessage
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

        if (uploadState == VMProfileImage.ImageUploadState.UPLOADING) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center),
                strokeWidth = 4.dp,
                color = MaterialTheme.colors.primary
            )
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





