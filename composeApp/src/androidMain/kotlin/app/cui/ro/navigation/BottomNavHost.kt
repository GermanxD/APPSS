package app.cui.ro.navigation

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import app.cui.ro.R
import app.cui.ro.auth.AuthService
import app.cui.ro.ui.BottomNavItem
import app.cui.ro.ui.screens.ContactsNavBarScreen
import app.cui.ro.ui.screens.ForoNavBarScreen
import app.cui.ro.ui.screens.HomeNavBarScreen
import app.cui.ro.ui.screens.MessagesNavBarScreen
import app.cui.ro.ui.screens.ProfileNavBarScreen

@Composable
fun BottomNavHost(navController: NavHostController, context: Context) {
    NavHost(navController, startDestination = "home_route") {
        composable("home_route") { HomeNavBarScreen(
            authService = AuthService()
        ) }
        composable("profile_route") { ProfileNavBarScreen() }
        composable("contacts_route") { ContactsNavBarScreen() }
        composable("foro_route") { ForoNavBarScreen() }
        composable("messages_route") { MessagesNavBarScreen() }
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
