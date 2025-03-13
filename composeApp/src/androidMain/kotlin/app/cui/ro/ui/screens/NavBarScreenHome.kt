package app.cui.ro.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cui.ro.R
import app.cui.ro.auth.AuthService
import app.cui.ro.models.VMHealthConnect
import app.cui.ro.ui.CustomTopAppBar
import app.cui.ro.ui.DataColumn
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun NavBarScreenHome(authService: AuthService) {
    val userId = remember { authService.getUserId() }
    var userFullNameDB by remember { mutableStateOf("Usuario") }
    var usernameDB by remember { mutableStateOf("Usuario") }
    var showSeccionInformacion2 by remember { mutableStateOf(false) } // Estado para controlar la visibilidad
    var showSeccionRecomendaciones2 by remember { mutableStateOf(false) }

    // Obtener los datos del usuario
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
        Column {
            CustomTopAppBar(
                onMenuClick = { /* Lógica para el clic del menú */ },
                onNotificationsClick = { /* Lógica para el clic de notificaciones */ },
                title = "\"Cuidarte es luchar, resistir y vencer al cáncer de mama\""
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mostrar la imagen de perfil
                    userId?.let {
                        ProfileScreen(
                            userId = it
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
                        .background(color = Color(0xFFF6A1C8))
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
                        } // Pasar el callback al hacer clic en "Ver más"
                    )
                }

                // Mostrar SeccionInformacion2 solo si showSeccionInformacion2 es true
                if (showSeccionInformacion2) {
                    SeccionInformacion2(
                        onDismiss = {
                            showSeccionInformacion2 = false
                        } // Ocultar la sección al cerrar
                    )
                }

                if (!showSeccionRecomendaciones2) {
                    SeccionRecomendaciones(
                        onVerMasClick = {
                            showSeccionRecomendaciones2 = true
                        } // Pasar el callback al hacer clic en "Ver más"
                    )
                }

                if(showSeccionRecomendaciones2){
                    SeccionRecomendaciones2(
                        onDismiss = {
                            showSeccionRecomendaciones2 = false
                        }
                    )
                }
            }

            SeccionSeguimiento(authService = AuthService())
        }
    }
}

@Composable
fun SeccionInformacion(
    onVerMasClick: () -> Unit // Callback para manejar el clic en "Ver más"
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
    onVerMasClick: () -> Unit // Callback para manejar el clic en "Ver más"
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
fun SeccionSeguimiento(authService: AuthService) {
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
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black)
            .height(IntrinsicSize.Min) // Añadido para igualar la altura
    ) {

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(Color(0xFFFFDCDA)), // importante mantener el mismo background
            horizontalAlignment = Alignment.Start,
        ) {
            Column( // Agregado un Column interno para el padding
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp) // Mover el padding aquí
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically, // Centra verticalmente los hijos de la Row
                ) {
                    // Texto
                    Text(
                        text = "Medicamentos",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f) // Ocupa el espacio restante
                            .align(Alignment.CenterVertically) // Alinea el texto verticalmente
                    )

                    // Imagen
                    Image(
                        painter = painterResource(R.drawable.ic_medicamentos),
                        contentDescription = "",
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterVertically) // Alinea la imagen verticalmente
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
                        color = Color.Black,
                    )
                    Text(
                        "Recordarme: Si",
                        fontSize = 12.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically, // Centra verticalmente los hijos de la Row
                ) {
                    // Texto
                    Text(
                        text = "Informacion del medicamento aqui",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f) // Ocupa el espacio restante
                            .align(Alignment.CenterVertically) // Alinea el texto verticalmente
                    )

                    // Imagen
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = "",
                            modifier = Modifier
                                .size(30.dp)// Alinea la imagen verticalmente
                        )
                    }

                }
            }
        }

        VerticalDivider(
            color = Color.Black, // el mismo color del background de las columnas
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxHeight()
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(Color(0xFFFFDCDA))
                .padding(5.dp), // importante mantener el mismo background
            horizontalAlignment = Alignment.Start,
        ) {
            // Sección de Pasos
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically, // Centra verticalmente los hijos de la Row
                ) {
                    // Texto
                    Text(
                        text = "Pasos",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f) // Ocupa el espacio restante
                            .align(Alignment.CenterVertically) // Alinea el texto verticalmente
                    )

                    // Imagen
                    Image(
                        painter = painterResource(R.drawable.ic_pasos),
                        contentDescription = "",
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterVertically) // Alinea la imagen verticalmente
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

                        val context = LocalContext.current
                        MedicionPasos(
                            authService = AuthService(),
                            context = context
                        )

                    }
                }
            }

            // Divider entre Pasos e Hidratacion
            Divider(
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )

            // Sección de Hidratación
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically, // Centra verticalmente los hijos de la Row
                ) {
                    // Texto
                    Text(
                        text = "Hidratacion",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f) // Ocupa el espacio restante
                            .align(Alignment.CenterVertically) // Alinea el texto verticalmente
                    )

                    // Imagen
                    Image(
                        painter = painterResource(R.drawable.ic_persona_agua),
                        contentDescription = "",
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterVertically)// Alinea la imagen verticalmente
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically, // Centra verticalmente los hijos de la Row
                ) {
                    // Texto
                    Text(
                        text = "$userFirstName, hoy no has registrado tu consumo de agua, registralo.",
                        fontSize = 12.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .weight(1f) // Ocupa el espacio restante
                            .align(Alignment.CenterVertically) // Alinea el texto verticalmente
                    )

                    // Imagen
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = "",
                            modifier = Modifier
                                .size(30.dp)// Alinea la imagen verticalmente
                        )
                    }

                }
            }
        }

    }
}

@Composable
fun SeccionInformacion2(
    onDismiss: () -> Unit // Callback para cerrar la sección
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
    onDismiss: () -> Unit // Callback para cerrar la sección
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
    context: Context,
    vmHealthConnect: VMHealthConnect = viewModel()
) {
    val userId = remember { authService.getUserId() }
    var userFirstName by remember { mutableStateOf("Usuario") }
    var stepsCount by remember { mutableIntStateOf(0) }

    // Obtener el nombre del usuario
    LaunchedEffect(userId) {
        if (userId != null) {
            authService.getUserFirstName(userId) { name ->
                if (name != null) {
                    userFirstName = name
                }
            }
        }
    }

    // Obtener pasos del día
    LaunchedEffect(Unit) {
        val healthClient = HealthConnectClient.getOrCreate(context)
        val today = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val steps = vmHealthConnect.readStepsForDate(healthClient, today)
        stepsCount = steps.toInt()
    }

    Text(
        "$userFirstName, hoy has dado $stepsCount pasos. ¡Ánimo, tú puedes dar algunos más!",
        fontSize = 12.sp,
        color = Color.Black,
    )
}

enum class ImageUploadState {
    IDLE,
    UPLOADING,
    SUCCESS,
    ERROR,
    SIZE_EXCEEDED // Nuevo estado
}

@Composable
fun ProfileScreen(userId: String) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    var uploadState by remember { mutableStateOf(ImageUploadState.IDLE) }
    var errorMessage by remember { mutableStateOf("") } // Mensaje de error específico

    LaunchedEffect(userId) {
        loadProfileImageFromFirestore(userId) { image ->
            base64Image = image
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            imageUri = selectedUri
            val fileSizeInBytes = getFileSize(selectedUri, context)
            val maxFileSizeInBytes = 500 * 1024 // 500KB (ajusta según tus necesidades)

            if (fileSizeInBytes > maxFileSizeInBytes) {
                uploadState = ImageUploadState.SIZE_EXCEEDED
                errorMessage = "La imagen excede el tamaño máximo permitido (500KB)." // Cambia el mensaje si es necesario
            } else {
                uploadState = ImageUploadState.UPLOADING
                errorMessage = "" // Limpiar el mensaje de error
                CoroutineScope(Dispatchers.IO).launch {
                    val base64 = convertImageToBase64(selectedUri, context) // Convertir y comprimir
                    withContext(Dispatchers.Main) { // Volver al hilo principal para actualizar la UI
                        saveImageToFirestore(userId, base64,
                            onSuccess = {
                                base64Image = base64
                                uploadState = ImageUploadState.SUCCESS
                            },
                            onFailure = {
                                uploadState = ImageUploadState.ERROR
                                errorMessage = "Error al guardar la imagen. Intenta de nuevo."
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

        // Mostrar mensaje de estado
        when (uploadState) {
            ImageUploadState.IDLE -> {}
            ImageUploadState.UPLOADING -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            ImageUploadState.SUCCESS -> {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Imagen actualizada",
                    tint = Color.Green,
                    modifier = Modifier.size(24.dp).align(Alignment.BottomEnd)
                )
                LaunchedEffect(Unit) {
                    delay(2000)
                    uploadState = ImageUploadState.IDLE
                }
            }
            ImageUploadState.SIZE_EXCEEDED,
            ImageUploadState.ERROR -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Error al actualizar",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = errorMessage.ifEmpty { "Intenta con otra imagen" }, // Usar el mensaje específico
                        style = MaterialTheme.typography.h1, // Usar un estilo más pequeño
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                LaunchedEffect(Unit) {
                    delay(3000)
                    uploadState = ImageUploadState.IDLE
                }
            }
        }
    }
}

fun getFileSize(uri: Uri, context: Context): Long {
    val contentResolver = context.contentResolver
    var fileSize = 0L
    contentResolver.openInputStream(uri)?.use { inputStream ->
        fileSize = inputStream.available().toLong()
    }
    return fileSize
}

fun convertImageToBase64(imageUri: Uri, context: Context): String {
    val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
    val bitmap = BitmapFactory.decodeStream(inputStream)

    val byteArrayOutputStream = ByteArrayOutputStream()
    // Comprime la imagen (ajusta la calidad según tus necesidades)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)  // Reduce la calidad a 70%
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun saveImageToFirestore(
    userId: String,
    base64Image: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val db: FirebaseFirestore = Firebase.firestore

    db.collection("users")
        .document(userId)
        .update("profileImage", base64Image)
        .addOnSuccessListener {
            println("Imagen actualizada correctamente en Firestore")
            onSuccess() // Ejecutar el callback después de guardar
        }
        .addOnFailureListener { e ->
            println("Error al actualizar la imagen: ${e.message}")
            onFailure()
        }
}

fun loadProfileImageFromFirestore(userId: String, onImageLoaded: (String?) -> Unit) {
    val db: FirebaseFirestore = Firebase.firestore

    db.collection("users")
        .document(userId)
        .get()
        .addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val image = document.getString("profileImage")
                onImageLoaded(image) // Pasar la imagen al callback
            } else {
                onImageLoaded(null) // No hay imagen
            }
        }
        .addOnFailureListener { e ->
            println("Error al recuperar la imagen: ${e.message}")
            onImageLoaded(null) // Manejar el error
        }
}