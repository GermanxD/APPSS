package app.mamma.guard.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import app.mamma.guard.auth.AuthService

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(context: Context, navController: NavController) {
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
        BottomNavItem("Home", "home", Icons.Default.Home),
        BottomNavItem("Informacion", "information_navbar", Icons.Default.Info),
        BottomNavItem("Foro", "foro_navbar", Icons.Default.Face),
        BottomNavItem("Configuracion", "settings_navbar", Icons.Default.Settings)
    )

    BottomNavigation(backgroundColor = Color(0xFFBBDEFB)) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun BottomNavHost(navController: NavHostController, context: Context) {
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeNavBarScreen() }
        composable("information_navbar") { InfoNavBarScreen() }
        composable("foro_navbar") { ForoNavBarScreen() }
        composable("settings_navbar") { SettingsNavBarScreen(context) }
    }
}

@Composable
fun HomeNavBarScreen() {
    CenteredText("Pantalla Principal")
}

@Composable
fun InfoNavBarScreen() {
    CenteredText("Informacion")
}

@Composable
fun ForoNavBarScreen() {
    CenteredText("Foro")
}

@Composable
fun SettingsNavBarScreen(context: Context) {
    CenteredText("Configuraciones")
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


