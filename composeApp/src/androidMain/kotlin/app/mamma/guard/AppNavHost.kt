package app.mamma.guard

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.mamma.guard.ui.LoginScreen
import app.mamma.guard.ui.RegisterScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController() // Crear controlador de navegación
) {
    NavHost(
        navController = navController,
        startDestination = "login" // Pantalla inicial
    ) {
        // Pantalla de Inicio de Sesión
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // Navegar a la pantalla principal tras el inicio de sesión
                    navController.navigate("home") {
                        // Limpia el stack para evitar volver al login
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClicked = {
                    // Navegar a la pantalla de registro
                    navController.navigate("register")
                }
            )
        }

        // Pantalla de Registro
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    // Navegar a la pantalla principal tras el registro
                    navController.navigate("home") {
                        // Limpia el stack para evitar volver al registro
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackClicked = { navController.popBackStack() }
            )
        }

        // Pantalla Principal (Home)
        composable("home") {
            HomeScreen()
        }
    }
}
