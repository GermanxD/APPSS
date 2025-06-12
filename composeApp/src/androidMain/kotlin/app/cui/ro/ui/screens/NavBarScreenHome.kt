package app.cui.ro.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import app.cui.ro.R
import app.cui.ro.auth.AuthService
import app.cui.ro.models.VMHealthConnect
import app.cui.ro.ui.DataColumn
import app.cui.ro.ui.theme.CuiroColors

@Composable
fun NavBarScreenHome() {
    val snackbarHostState = remember { SnackbarHostState() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
    ) {

        // Banner de ayuda
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = 4.dp,
            backgroundColor = CuiroColors.SecondaryRose
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Icono de Búsqueda",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )

                Text(
                    text = "¿Necesitas ayuda?",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        // Secciones
        SeccionInformacion()

        SeccionRecomendaciones()

    }

    // Snackbar
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.BottomCenter)
    ) { snackbarData ->
        Snackbar(snackbarData)
    }
}

@Composable
fun DataGrid(items: List<Pair<Int, String>>) {
    Column(modifier = Modifier.padding(16.dp)) {
        items.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowItems.forEach { (imageResId, text) ->
                    DataColumn(imageResId = imageResId, text = text)
                }
            }
        }
    }
}

@Composable
fun SeccionInformacion() {
    val items = listOf(
        R.drawable.ic_datos_clinicos to "Datos clínicos",
        R.drawable.ic_efectos_del_tratamiento to "Efectos",
        R.drawable.ic_medicamentos to "Medicamentos",
        R.drawable.ic_hidratacion to "Hidratación",
        R.drawable.ic_signos_vitales to "Signos vitales",
        R.drawable.ic_reporte_salud to "Reporte de salud"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Registro de información",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = CuiroColors.FontBrown,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Card(
            modifier = Modifier
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = 4.dp,
            backgroundColor = Color.White
        ) {
            DataGrid(items)
        }
    }
}

@Composable
fun SeccionRecomendaciones() {
    val items = listOf(
        R.drawable.ic_informacion to "Información",
        R.drawable.ic_quimioterapia to "Quimioterapia",
        R.drawable.ic_nutricion to "Nutrición",
        R.drawable.ic_ejercicio_fisico to "Ejercicio físico",
        R.drawable.ic_sexualidad to "Sexualidad"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Recomendaciones sobre...",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = CuiroColors.FontBrown,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Card(
            modifier = Modifier
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = 4.dp,
            backgroundColor = Color.White
        ) {
            DataGrid(items)
        }
    }
}











