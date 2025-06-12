package app.cui.ro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cui.ro.R
import app.cui.ro.ui.DataColumn
import app.cui.ro.ui.theme.CuiroColors

@Composable
fun NavBarScreenHome() {
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
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = CuiroColors.FontBrown,
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
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
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = CuiroColors.FontBrown,
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
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











