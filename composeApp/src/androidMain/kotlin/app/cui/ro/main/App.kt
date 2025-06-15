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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

        if (showAppBars) {

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    DrawerContent(
                        navController,
                        drawerState,
                        scope,
                        context,
                        authService,
                        profileImage
                    )
                }
            ) {
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
    var usernameDB by remember { mutableStateOf("Usuario") }

    LaunchedEffect(userId) {
        userId?.let { id ->
            authService.getAllUserData(id) { data ->
                data["username"]?.let { usernameDB = it }
            }
            authService.getUserFirstName(id) { name ->
                name?.let { userFirstName = it }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .fillMaxHeight()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .shadow(4.dp)
            .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
            .background(Color.White)
    ) {
        // Header Section - Logo y perfil de usuario
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4A90E2),
                            Color(0xFF357ABD)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Logo
            Image(
                painter = painterResource(R.drawable.img_cuiro_letras),
                contentDescription = "Logo CUIRO",
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 12.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )

            // Foto de perfil
            if (profileImageBase64 != null) {
                val imageBytes = Base64.decode(profileImageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color.White, CircleShape)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Imagen de perfil no disponible",
                    tint = Color.White,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(16.dp)
                )
            }

            // Información del usuario
            Text(
                text = userFirstName,
                style = MaterialTheme.typography.h6,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "@$usernameDB",
                style = MaterialTheme.typography.body2,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Navigation Section
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            item {
                // Navegación Principal
                DrawerSectionHeader(title = "Principal")

                DrawerItem(
                    icon = Icons.Default.Home,
                    label = "Inicio"
                ) {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Home.route)
                }

                DrawerItem(
                    icon = Icons.Default.Person,
                    label = "Mi Perfil"
                ) {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Profile.route)
                }

                DrawerItem(
                    icon = Icons.Default.Notifications,
                    label = "Notificaciones",
                    hasNotification = true,
                    notificationCount = 3
                ) {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Home.route)
                }
            }

            item {
                // Herramientas
                DrawerSectionHeader(title = "Herramientas")

                DrawerItem(
                    icon = Icons.Default.Search,
                    label = "Buscar"
                ) {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Home.route)
                }

                DrawerItem(
                    icon = Icons.Default.Info,
                    label = "Historial"
                ) {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Home.route)
                }

                DrawerItem(
                    icon = Icons.Default.Favorite,
                    label = "Guardados"
                ) {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Home.route)
                }
            }

            item {
                // Configuración y Soporte
                DrawerSectionHeader(title = "Configuración")

                DrawerItem(
                    icon = Icons.Default.Settings,
                    label = "Configuración"
                ) {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Settings.route)
                }

                DrawerItem(
                    icon = Icons.Default.Info,
                    label = "Ayuda y Soporte"
                ) {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Home.route)
                }

                DrawerItem(
                    icon = Icons.Default.Info,
                    label = "Acerca de"
                ) {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Home.route)
                }
            }
        }

        // Footer Section - Version y Logout
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Divider(
                color = Color.Gray.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Version info
            Text(
                text = "Versión 1.0.0",
                style = MaterialTheme.typography.caption,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp)
            )

            DrawerItem(
                icon = Icons.Default.ExitToApp,
                label = "Cerrar Sesión",
                isLogout = true
            ) {
                authService.logout(context)
                scope.launch { drawerState.close() }
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
}

@Composable
fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.overline,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    isLogout: Boolean = false,
    hasNotification: Boolean = false,
    notificationCount: Int = 0,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isLogout) Color.Red else Color(0xFF4A90E2),
                modifier = Modifier.size(24.dp)
            )

            // Badge de notificación
            if (hasNotification && notificationCount > 0) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-8).dp)
                ) {
                    Text(
                        text = if (notificationCount > 99) "99+" else notificationCount.toString(),
                        style = MaterialTheme.typography.caption,
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            fontSize = 16.sp,
            color = if (isLogout) Color.Red else Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        // Flecha indicadora
        if (!isLogout) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun Badge(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = Color.Red,
                shape = CircleShape
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}


