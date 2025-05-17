package app.cui.ro.models

import androidx.compose.ui.graphics.painter.Painter

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
}

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: Painter
)
