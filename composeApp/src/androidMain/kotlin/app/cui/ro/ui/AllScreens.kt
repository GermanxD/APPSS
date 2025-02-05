package app.cui.ro.ui

import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector
)
