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
import app.cui.ro.auth.AuthService
import app.cui.ro.models.VMProfileImage
import app.cui.ro.ui.CenteredText
import java.util.Locale

@Composable
fun NavBarScreenProfile(
    authService: AuthService,
) {
    val userId = remember { authService.getUserId() }
    var userFullNameDB by remember { mutableStateOf("Usuario") }
    var usernameDB by remember { mutableStateOf("Usuario") }

    LaunchedEffect(userId) {
        if (userId != null) {
            authService.getAllData(userId) { userFullName, username ->
                if (userFullName != null && username != null) {
                    userFullNameDB = userFullName
                    usernameDB = username
                }
            }
        }
    }
    // Tarjeta de perfil
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 6.dp,
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            userId?.let {
                ProfileScreen(
                    userId = it,
                    vmProfileImage = VMProfileImage()
                )
            }

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = userFullNameDB,
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = "@$usernameDB".lowercase(Locale.getDefault()),
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}