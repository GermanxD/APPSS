package app.cui.ro.main

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.cui.ro.auth.AuthService
import app.cui.ro.models.Screen
import app.cui.ro.navigation.BottomNavigationBar
import app.cui.ro.navigation.NavBarScreenStart
import app.cui.ro.ui.CustomTopAppBar
import app.cui.ro.ui.screens.SettingsScreen
import app.cui.ro.ui.session.LoginScreen
import app.cui.ro.ui.session.RegisterScreen
import app.cui.ro.ui.theme.CuiroTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination?.route

        // Determinar si se debe mostrar el drawer y top bar
        val showAppBars = currentRoute != Screen.Login.route && currentRoute != Screen.Register.route

        if (showAppBars) {
            Scaffold(
                contentWindowInsets = WindowInsets.systemBars,
                topBar = {
                    CustomTopAppBar(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onNotificationsClick = { /* Lógica para notificaciones */ },
                        title =  "\"Cuidarte es luchar, resistir y vencer al cáncer de mama\""
                    )
                },
                bottomBar = {
                    if (currentRoute?.startsWith("home_route") == true) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            DrawerContent(navController, drawerState, scope, context, authService)
                        }
                    ) {
                        MainScaffold(
                            navController,
                            drawerState,
                            scope,
                            startDestination,
                            context,
                            authService
                        )
                    }
                }
            }
        } else {
            // Mostrar solo el contenido sin barras
            MainScaffold(navController, drawerState, scope, startDestination, context, authService)
        }
    }
}

@Composable
fun MainScaffold(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    startDestination: String,
    context: Context,
    authService: AuthService
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
            NavBarScreenStart(
                onMenuClick = {
                    scope.launch { drawerState.open() }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onMenuClick = {
                    scope.launch { drawerState.open() }
                }
            )
        }
    }

}


@Composable
fun DrawerContent(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    context: Context,
    authService: AuthService
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.6F)
            .fillMaxHeight()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0))
                )
            )
            .padding(vertical = 40.dp, horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil",
                tint = Color.DarkGray,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Bienvenida",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }

        Divider(color = Color.Gray.copy(alpha = 0.3f))

        DrawerItem(icon = Icons.Default.Home, label = "Inicio") {
            scope.launch { drawerState.close() }
            navController.navigate(Screen.Home.route)
        }

        DrawerItem(icon = Icons.Default.ExitToApp, label = "Cerrar sesión") {
            authService.logout(context)
            scope.launch { drawerState.close() }
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }

        DrawerItem(icon = Icons.Default.Settings, label = "Configuraciones") {
            scope.launch { drawerState.close() }
            navController.navigate(Screen.Settings.route)
        }
    }
}

@Composable
fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}



