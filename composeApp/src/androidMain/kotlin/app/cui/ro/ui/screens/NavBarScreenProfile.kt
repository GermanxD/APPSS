package app.cui.ro.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cui.ro.auth.AuthService
import app.cui.ro.models.VMProfileImage
import java.util.Locale

@Composable
fun NavBarScreenProfile(
    authService: AuthService,
) {
    val userId = remember { authService.getUserId() }
    var userData by remember {
        mutableStateOf<Map<String, String?>>(
            mapOf(
                "fullname" to "Usuario",
                "username" to "usuario",
                "email" to "",
                "gender" to "",
                "birthDate" to ""
            )
        )
    }

    val vmProfileImage: VMProfileImage = viewModel()

    LaunchedEffect(userId) {
        if (userId != null) {
            authService.getAllUserData(userId) { data ->
                userData = data
            }
            vmProfileImage.loadProfileImageFromFirestore(userId)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 6.dp,
        backgroundColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                userId?.let {
                    ProfileScreen(
                        userId = it,
                        vmProfileImage = vmProfileImage
                    )
                }

                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = userData["fullname"] ?: "Usuario",
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = "@${userData["username"]?.lowercase() ?: ""}",
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )
                }
            }

            // Información adicional
            Column(modifier = Modifier.padding(top = 16.dp)) {
                userData["email"]?.let {
                    Text("Correo: $it", style = MaterialTheme.typography.body2)
                }
                userData["gender"]?.let {
                    Text("Género: $it", style = MaterialTheme.typography.body2)
                }
                userData["birthDate"]?.let {
                    Text("Nacimiento: $it", style = MaterialTheme.typography.body2)
                }
            }
        }
    }
}

