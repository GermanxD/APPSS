package app.cui.ro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PermissionsRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionsRationaleScreen { finish() }
        }
    }
}

@Composable
fun PermissionsRationaleScreen(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Esta aplicación necesita permisos de Health Connect para acceder a tus datos de salud.",
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onClose) {
            Text(text = "Cerrar")
        }
    }
}
