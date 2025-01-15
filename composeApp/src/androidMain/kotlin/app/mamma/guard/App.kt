package app.mamma.guard

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.mamma.guard.auth.AuthService
import app.mamma.guard.ui.HomeScreen
import app.mamma.guard.ui.LoginScreen
import app.mamma.guard.ui.RegisterScreen
import app.mamma.guard.ui.Screen

@Composable
fun App(
    context: Context
) {
    val navController: NavHostController = rememberNavController()

    val authService = AuthService()
    val startDestination = if (authService.isUserLoggedIn(context)) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    authService.saveLoginState(context, true)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClicked = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                context = context,
                navController = navController
            )
        }
    }
}
