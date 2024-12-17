package fime.app.test

import LoginScreen
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import fime.app.test.ui.RegisterScreen
import fime.app.test.ui.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

    when (currentScreen) {
        is Screen.Home -> {
            Text("Welcome to the app!")
            // Aquí puedes agregar un botón para cerrar sesión y volver a Login
        }
        is Screen.Register -> {
            RegisterScreen(
                onRegisterSuccess = {
                    currentScreen = Screen.Home
                },
                onBackClicked = { currentScreen = Screen.Login }
            )
        }
        is Screen.Login -> {
            LoginScreen(
                onLoginSuccess = { currentScreen = Screen.Home },
                onRegisterClicked = { currentScreen = Screen.Register }
            )
        }
    }
}
