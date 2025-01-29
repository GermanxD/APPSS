package app.mamma.guard

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.mamma.guard.auth.AuthService
import app.mamma.guard.ui.HomeScreen
import app.mamma.guard.ui.LoginScreen
import app.mamma.guard.ui.RegisterScreen

@Composable
fun AppNavHost(
    context: android.content.Context,
    navController: NavHostController = rememberNavController()
) {
    val authService = remember { AuthService() }
    val isUserLoggedIn = authService.isUserLoggedIn(context)

    // Determina el destino de inicio dependiendo del estado de la sesión
    val startDestination = if (isUserLoggedIn) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    authService.saveLoginState(context, true) // Guardar estado de sesión
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClicked = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    authService.saveLoginState(context, true) // Guardar estado de sesión
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScreen(context = context, authService = authService, navController = navController)
        }
    }
}
