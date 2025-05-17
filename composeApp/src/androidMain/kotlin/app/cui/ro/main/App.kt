package app.cui.ro.main

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.cui.ro.auth.AuthService
import app.cui.ro.navigation.NavBarScreenStart
import app.cui.ro.ui.session.LoginScreen
import app.cui.ro.ui.session.RegisterScreen
import app.cui.ro.models.Screen

@Composable
fun App(
    context: Context
) {
    val navController: NavHostController = rememberNavController()

    val authService = AuthService()
    val startDestination = if (authService.isUserLoggedIn()) {
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
                        popUpTo(0)
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
                    authService.saveLoginState(context, true)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0)
                    }
                },
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) {
            NavBarScreenStart()
        }
    }
}
