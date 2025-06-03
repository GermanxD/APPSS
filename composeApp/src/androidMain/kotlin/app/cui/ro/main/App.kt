package app.cui.ro.main

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.cui.ro.R
import app.cui.ro.auth.AuthService
import app.cui.ro.models.Screen
import app.cui.ro.models.VMProfileImage
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

        val showAppBars =
            currentRoute != Screen.Login.route && currentRoute != Screen.Register.route

        val vmProfileImage: VMProfileImage = viewModel()

        // Aquí recoges el estado actual de la imagen
        val profileImage by vmProfileImage.profileImage

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(navController, drawerState, scope, context, authService, profileImage)
            }
        ) {
            if (showAppBars) {
                Scaffold(
                    contentWindowInsets = WindowInsets.systemBars,
                    topBar = {
                        CustomTopAppBar(
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                    val userId = authService.getUserId()
                                    userId?.let {
                                        vmProfileImage.loadProfileImageFromFirestore(it)
                                    }
                                }
                            },
                            onNotificationsClick = { /* Lógica para notificaciones */ },
                            title = "\"Cuidarte es luchar, resistir y vencer al cáncer de mama\""
                        )
                    },
                    bottomBar = {
                        if (currentRoute?.startsWith("home_route") == true) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
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
            } else {
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
    authService: AuthService,
    profileImageBase64: String?
) {
    val userId = remember { authService.getUserId() }
    var userFirstName by remember { mutableStateOf("Usuario") }

    LaunchedEffect(userId) {
        userId?.let {
            authService.getUserFirstName(it) { name ->
                name?.let { userFirstName = it }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .fillMaxHeight()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .shadow(4.dp)
            .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
            .background(Color.White)
            .padding(vertical = 10.dp, horizontal = 16.dp)
    ) {
        Column {
            Image(
                painter = painterResource(R.drawable.img_cuiro_letras),
                contentDescription = "Logo CUIRO",
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                // Mostrar imagen de perfil desde base64 o icono por defecto
                if (profileImageBase64 != null) {
                    val imageBytes = Base64.decode(profileImageBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Imagen de perfil no disponible",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )
                }

                Text(
                    text = userFirstName,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Divider(color = Color.Gray.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

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



