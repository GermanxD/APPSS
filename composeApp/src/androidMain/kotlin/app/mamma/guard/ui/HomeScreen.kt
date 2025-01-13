package app.mamma.guard.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import app.mamma.guard.auth.AuthService

@Composable
fun HomeScreen(context: Context, navController: NavController) {
    // Interceptar el botón de retroceso
    BackHandler {
        // Bloquear la acción de volver atrás
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the app!", modifier = Modifier.padding(bottom = 24.dp))

        val menuOptions = listOf(
            "Presión Arterial" to Screen.BloodPressure.route,
            "Ritmo Cardíaco" to Screen.HeartRate.route,
            "Historial" to Screen.History.route,
            "Configuraciones" to Screen.Settings.route
        )

        menuOptions.forEach { (label, route) ->
            MenuOptionCard(label) {
                navController.navigate(route)
            }
        }

        Button(
            onClick = {
                AuthService().logout(context) // Cerrar sesión
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
fun MenuOptionCard(label: String, onClick: () -> Unit) {
    Card(
        backgroundColor = Color(0xFFBBDEFB),
        elevation = 4.dp,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(16.dp)
        )
    }
}

