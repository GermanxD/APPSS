package app.mamma.guard.ui

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Menu : Screen("menu")
    data object BloodPressure : Screen("blood_pressure")
    data object HeartRate : Screen("heart_rate")
    data object History : Screen("history")
    data object Settings : Screen("settings")
}
