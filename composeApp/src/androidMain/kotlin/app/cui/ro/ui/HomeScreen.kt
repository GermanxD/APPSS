package app.cui.ro.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.cui.ro.auth.AuthService

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(context: Context, authService: AuthService, navController: NavHostController) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = bottomNavController)
        }
    ) {
        BottomNavHost(navController = bottomNavController, context = context)
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("home", Icons.Default.Home),
        BottomNavItem( "information_navbar", Icons.Default.Create),
        BottomNavItem( "foro_navbar", Icons.Default.Face),
        BottomNavItem( "settings_navbar", Icons.Default.Settings)
    )

    BottomNavigation(
        backgroundColor = Color(0xFFBBDEFB),
        modifier = Modifier.fillMaxWidth()
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        Row(
            modifier = Modifier
                .fillMaxWidth(), // Sin padding extra
            horizontalArrangement = Arrangement.SpaceBetween // Espacio ajustado entre íconos
        ) {
            items.forEach { item ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = "",
                            modifier = Modifier.size(30.dp), // Reducir tamaño del ícono
                            tint = Color.Black
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    alwaysShowLabel = true // Forzar mostrar el texto siempre
                )
            }
        }
    }
}


@Composable
fun BottomNavHost(navController: NavHostController, context: Context) {
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeNavBarScreen() }
        composable("information_navbar") { InfoNavBarScreen() }
        composable("foro_navbar") { ForoNavBarScreen() }
        composable("settings_navbar") { SettingsNavBarScreen(context) }
    }
}

@Composable
fun HomeNavBarScreen() {
    // Estructura principal de la pantalla
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Fondo claro para contraste
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Dashboard Principal",
                style = MaterialTheme.typography.h6.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Mostrar las tarjetas con los datos y gráficos
            DashboardGrid()
        }
    }
}

@Composable
fun DashboardGrid() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            DashboardCard(
                title = "Presión Arterial",
                value = "120/80",
                description = "Última lectura",
                chartColor = Color(0xFF76C7C0)
            )
            DashboardCard(
                title = "Ritmo Cardíaco",
                value = "72 BPM",
                description = "Promedio Diario",
                chartColor = Color(0xFFFFB74D)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            DashboardCard(
                title = "Peso Corporal",
                value = "68 kg",
                description = "Última medición",
                chartColor = Color(0xFF64B5F6)
            )
            DashboardCard(
                title = "Pasos Diarios",
                value = "10,000",
                description = "Meta alcanzada",
                chartColor = Color(0xFF81C784)
            )
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    description: String,
    chartColor: Color
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.h5.copy(
                    color = chartColor,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.body2.copy(
                    color = Color.Gray
                )
            )
        }
    }
}

@Composable
fun InfoNavBarScreen() {
    CenteredText("Informacion")
}

@Composable
fun ForoNavBarScreen() {
    CenteredText("Foro")
}

@Composable
fun SettingsNavBarScreen(context: Context) {
    CenteredText("Configuraciones")
}

@Composable
fun CenteredText(text: String) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text(
            text = text,
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        )
    }
}


