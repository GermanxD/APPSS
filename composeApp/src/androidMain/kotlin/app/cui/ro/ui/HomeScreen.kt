package app.cui.ro.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
        }
    ) {
        BottomNavHost(navController = bottomNavController, context = context)
    }
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
    Column {
        CustomTopAppBar(
            onMenuClick = { /* Lógica para el clic del menú */ },
            onNotificationsClick = { /* Lógica para el clic de notificaciones */ },
            title = "\"Cuidarte es luchar, resistir y vencer al cáncer de mama\""
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Contenido de la pantalla Home",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1
            )
        }
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


