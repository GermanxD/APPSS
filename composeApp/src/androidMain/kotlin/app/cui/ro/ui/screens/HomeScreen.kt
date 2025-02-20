package app.cui.ro.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import app.cui.ro.R
import app.cui.ro.navigation.BottomNavHost
import app.cui.ro.navigation.BottomNavigationBar
import app.cui.ro.ui.CustomTopAppBar
import app.cui.ro.ui.DataColumn

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(context: Context) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = bottomNavController)
        },
        content = { paddingValues ->
            // Aplicar el paddingValues al contenido para evitar superposiciones
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Asegura que el contenido no se superponga con el BottomNavigationBar
            ) {
                BottomNavHost(navController = bottomNavController, context = context)
            }
        }
    )
}

@Composable
fun HomeNavBarScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            CustomTopAppBar(
                onMenuClick = { /* Lógica para el clic del menú */ },
                onNotificationsClick = { /* Lógica para el clic de notificaciones */ },
                title = "\"Cuidarte es luchar, resistir y vencer al cáncer de mama\""
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_profile),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape) // Recorta la imagen en forma circular
                            .background(
                                color = Color.Black,
                                shape = CircleShape
                            ) // Fondo circular (opcional)
                    )
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = "Olivia Wilson",
                            textAlign = TextAlign.Left,
                            style = MaterialTheme.typography.body1
                        )

                        Text(
                            text = "@reallygreatsite",
                            textAlign = TextAlign.Left,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .background(color = Color(0xFFF6A1C8))
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = "Icono de Busqueda",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(36.dp),
                        tint = Color.White,
                    )

                    Text(
                        text = "¿Necesitas ayuda?",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Registro de información",
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically, // Alinea el texto y la imagen verticalmente
                    ) {
                        Text(
                            text = "Ver más...",
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Color.Gray,
                        )
                        Spacer(modifier = Modifier.width(4.dp)) // Añade un pequeño espacio entre el texto y la imagen
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_right),
                            contentDescription = "",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray,
                        )
                    }
                }

                // Modified section to prevent text from pushing images up.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceAround // This is crucial!
                ) {
                    DataColumn(
                        imageResId = R.drawable.ic_datos_clinicos,
                        text = "Datos clinicos"
                    )
                    DataColumn(
                        imageResId = R.drawable.ic_efectos_del_tratamiento,
                        text = "Efectos del tratamiento"
                    )
                    DataColumn(
                        imageResId = R.drawable.ic_medicamentos,
                        text = "Medicamentos"
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Recomendaciones sobre...",
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically, // Alinea el texto y la imagen verticalmente
                    ) {
                        Text(
                            text = "Ver más...",
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Color.Gray,
                        )
                        Spacer(modifier = Modifier.width(4.dp)) // Añade un pequeño espacio entre el texto y la imagen
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_right),
                            contentDescription = "",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray,
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceAround // This is crucial!
                ) {
                    DataColumn(
                        imageResId = R.drawable.ic_informacion,
                        text = "Informacion"
                    )
                    DataColumn(
                        imageResId = R.drawable.ic_quimioterapia,
                        text = "Quimioterapia"
                    )
                    DataColumn(
                        imageResId = R.drawable.ic_nutricion,
                        text = "Nutricion"
                    )
                }
            }

// Sección modificada
// Sección modificada
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.Black)
                            .height(IntrinsicSize.Min) // Añadido para igualar la altura
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .background(Color(0xFFFFDCDA)), // importante mantener el mismo background
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Column( // Agregado un Column interno para el padding
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp) // Mover el padding aquí
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        "Medicamentos",
                                        fontSize = 20.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Image(
                                        painter = painterResource(R.drawable.ic_clock),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(start = 10.dp),
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()

                                ) {
                                    Text(
                                        "Olivia, el siguiente medicamento es:",
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        "Tamoxifeno (Nolvadex):",
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Hora: 12:00 hrs",
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                    )
                                    Text(
                                        "Recordarme: Si",
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                )
                                {
                                    Column(
                                        modifier = Modifier.weight(0.6f)
                                    ) {
                                        Text(
                                            "Informacion del medicamento aqui",
                                            fontSize = 14.sp,
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(0.3f)
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.ic_add),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .size(30.dp)
                                        )
                                    }

                                }
                            }
                        }

                        VerticalDivider(
                            color = Color.Black, // el mismo color del background de las columnas
                            thickness = 1.dp,
                            modifier = Modifier
                                .fillMaxHeight()
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .background(Color(0xFFFFDCDA)), // importante mantener el mismo background
                            horizontalAlignment = Alignment.Start,
                        ) {
                            // Sección de Pasos
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        "Pasos",
                                        fontSize = 20.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(
                                        modifier = Modifier
                                            .size(40.dp)
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                )
                                {
                                    Column(
                                        modifier = Modifier
                                            .weight(0.7f)
                                            .padding(vertical = 10.dp)
                                    ) {
                                        Text(
                                            "Hola Olivia, hoy has dado 0 pasos (0 min). Animo, tu puedes dar algunos.",
                                            fontSize = 12.sp,
                                            color = Color.Black,
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(0.3f)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.ic_pasos),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .size(50.dp)
                                        )
                                    }
                                }
                            }


                            // Divider entre Pasos e Hidratacion
                            Divider(
                                color = Color.Black,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                            )

                            // Sección de Hidratación
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        "Hidratacion",
                                        fontSize = 20.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(
                                        modifier = Modifier
                                            .size(50.dp)
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                )
                                {
                                    Column(
                                        modifier = Modifier.weight(0.7f)
                                    ) {
                                        Text(
                                            "Hola Olivia, hoy no has registrado tu consumo de agua, registralo.",
                                            fontSize = 12.sp,
                                            color = Color.Black,
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(0.3f)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.ic_persona_agua),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .size(50.dp)
                                        )
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterHorizontally)
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.ic_add),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(30.dp)
                                    )
                                }
                            }
                        }
                    }
        }
    }
}





