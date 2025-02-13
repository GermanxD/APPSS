package app.cui.ro.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.cui.ro.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(context: Context) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = bottomNavController)
        },
        content = { paddingValues ->
            // Aplicar el paddingValues al contenido para evitar superposiciones
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Asegura que el contenido no se superponga con el BottomNavigationBar
            ) {
                BottomNavHost(navController = bottomNavController, context = context)
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            name = "Perfil",
            route = "profile_route",
            icon = painterResource(id = R.drawable.ic_profile)
        ),
        BottomNavItem(
            name = "Contactos",
            route = "contacts_route",
            icon = painterResource(id = R.drawable.ic_contacts)
        ),
        BottomNavItem(
            name = "Inicio",
            route = "home_route",
            icon = painterResource(id = R.drawable.ic_home)
        ),
        BottomNavItem(
            name = "Foro",
            route = "foro_route",
            icon = painterResource(id = R.drawable.ic_foro)
        ),
        BottomNavItem(
            name = "Mensajes",
            route = "messages_route",
            icon = painterResource(id = R.drawable.ic_messages)
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigation(
        backgroundColor = Color(0xFFF4A0C0), // Cambiado a blanco
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = item.icon,
                        contentDescription = item.name,
                        modifier = Modifier.size(30.dp),
                        tint = if (currentRoute == item.route) Color.Black else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.name,
                        color = if (currentRoute == item.route) Color.Black else Color.Gray,
                        fontSize = 10.sp
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                alwaysShowLabel = true
            )
        }
    }
}

@Composable
fun BottomNavHost(navController: NavHostController, context: Context) {
    NavHost(navController, startDestination = "home_route") {
        composable("home_route") { HomeNavBarScreen() }
        composable("profile_route") { ProfileNavBarScreen() }
        composable("contacts_route") { ContactsNavBarScreen() }
        composable("foro_route") { ForoNavBarScreen() }
        composable("messages_route") { MessagesNavBarScreen() }
    }
}

@Composable
fun HomeNavBarScreen() {
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
                    .padding(16.dp)
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
                            text = "Olivia Wilson",
                            textAlign = TextAlign.Left,
                            style = MaterialTheme.typography.body1
                        )

                        Text(
                            text = "@reallygreatsite",
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
                        fontWeight = FontWeight.Normal,
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
                        imageResId = R.drawable.img_logo_login,
                        text = "Datos clinicos"
                    )
                    DataColumn(
                        imageResId = R.drawable.img_logo_login,
                        text = "Efectos del tratamiento"
                    )
                    DataColumn(
                        imageResId = R.drawable.img_logo_login,
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
                        fontWeight = FontWeight.Normal,
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
                        imageResId = R.drawable.img_logo_login,
                        text = "Datos clinicos"
                    )
                    DataColumn(
                        imageResId = R.drawable.img_logo_login,
                        text = "Efectos del tratamiento"
                    )
                    DataColumn(
                        imageResId = R.drawable.img_logo_login,
                        text = "Medicamentos"
                    )
                }
            }

// Sección modificada
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp) // Altura fija para la Row
                        .background(color = Color(0xFFFFDCDA))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(20.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            "Medicamentos",
                            fontSize = 20.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "Olivia, el siguiente medicamento es:",
                            fontSize = 12.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "Tamoxifeno (Nolvadex):",
                            fontSize = 12.sp,
                            color = Color.Black,
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
                        Text(
                            "Informacion del medicamento aqui",
                            fontSize = 12.sp,
                            color = Color.Black,
                        )
                    }

                    Divider(
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(Color.Black)
                                .weight(1f)
                        ) { }

                        Divider(
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                        )

                        Row(
                            modifier = Modifier
                                .background(Color.White)
                                .weight(1f)
                        ) { }
                    }
                }
            }
        }
    }
}

@Composable
fun DataColumn(imageResId: Int, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center, // Center content vertically
        modifier = Modifier.width(100.dp) // Fixed width for consistent layout
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = text,
            modifier = Modifier
                .size(100.dp)
                .background(color = Color(0xFFF6A1C8))
                .clip(CircleShape) // Optional: If you want the image to be a circle
        )
        Spacer(modifier = Modifier.height(4.dp)) // Add some space between image and text
        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = Color.Black,
        )
    }
}

@Composable
fun CustomTopAppBar(
    onMenuClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    title: String
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "Menu",
                            modifier = Modifier.size(30.dp),
                        )
                    }
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = "Notificaciones",
                            modifier = Modifier.size(30.dp),
                        )
                    }
                }
                Text(
                    text = title,
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
                Image(
                    painter = painterResource(R.drawable.img_logo_login),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }
        },
        backgroundColor = Color(0xFFF4A0C0),
        contentColor = Color.Black,
        navigationIcon = null,
        actions = {}
    )
}

@Composable
fun ProfileNavBarScreen() {
    CenteredText("Perfil")
}

@Composable
fun ContactsNavBarScreen() {
    CenteredText("Contactos")
}

@Composable
fun ForoNavBarScreen() {
    CenteredText("Foro")
}

@Composable
fun MessagesNavBarScreen() {
    CenteredText("Mensajes")
}

@Composable
fun CenteredText(text: String) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text(
            text = text,
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        )
    }
}


