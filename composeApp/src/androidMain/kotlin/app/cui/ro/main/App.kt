package app.cui.ro.main

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.cui.ro.auth.AuthService
import app.cui.ro.models.Screen
import app.cui.ro.navigation.NavBarScreenStart
import app.cui.ro.ui.session.LoginScreen
import app.cui.ro.ui.session.RegisterScreen
import app.cui.ro.ui.theme.CuiroTheme

@Composable
fun App(context: Context) {
    CuiroTheme {
        val navController: NavHostController = rememberNavController()
        val authService = AuthService()

        val startDestination = if (authService.isUserLoggedIn()) {
            Screen.Home.route
        } else {
            Screen.Login.route
        }

        Scaffold(
            contentWindowInsets = WindowInsets.systemBars
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
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
                            onBackClicked = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(Screen.Home.route) {
                        NavBarScreenStart()
                    }
                }
            }
        }
    }
}

