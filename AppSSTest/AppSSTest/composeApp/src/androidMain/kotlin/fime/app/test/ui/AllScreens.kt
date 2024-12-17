package fime.app.test.ui

sealed class Screen {
    data object Login: Screen()
    data object Register : Screen()
    data object Home : Screen()
}
