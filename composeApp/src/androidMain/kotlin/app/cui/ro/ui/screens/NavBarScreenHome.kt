package app.cui.ro.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cui.ro.R
import app.cui.ro.auth.AuthService
import app.cui.ro.models.VMHealthConnect
import app.cui.ro.ui.CustomTopAppBar
import app.cui.ro.ui.DataColumn
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun NavBarScreenHome(authService: AuthService) {
    val userId = remember { authService.getUserId() }
    var userFullNameDB by remember { mutableStateOf("Usuario") } // Estado para el nombre completo
    var usernameDB by remember { mutableStateOf("Usuario") } // Estado para el username

    LaunchedEffect(userId) {
        if (userId != null) {
            authService.getAllData(userId) { userFullName, username ->
                if (userFullName != null && username != null) {
                    userFullNameDB = userFullName // Actualiza el estado del nombre completo
                    usernameDB = username // Actualiza el estado del username
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
            )
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_profile),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape) // Recorta la imagen en forma circular
                            .background(
                                color = Color.Black,
                                shape = CircleShape
                            ) // Fondo circular (opcional)
                    )
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

                SeccionInformacion()
            }

            SeccionSeguimiento(authService = AuthService())
        }
    }
}

@Composable
fun SeccionInformacion() {
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
            verticalAlignment = Alignment.CenterVertically, // Alinea el texto y la imagen verticalmente
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
            verticalAlignment = Alignment.CenterVertically, // Alinea el texto y la imagen verticalmente
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
                    ){
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
                    ){
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
fun MedicionPasos(
    authService: AuthService,
    context: Context,
    vmHealthConnect: VMHealthConnect = viewModel()
) {
    val userId = remember { authService.getUserId() }
    var userFirstName by remember { mutableStateOf("Usuario") }
    var stepsCount by remember { mutableStateOf(0) }

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


