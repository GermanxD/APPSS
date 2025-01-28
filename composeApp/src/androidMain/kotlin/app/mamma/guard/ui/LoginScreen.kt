package app.mamma.guard.ui

import android.content.Context
import android.graphics.Rect
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import app.mamma.guard.R
import com.bumptech.glide.Glide
import app.mamma.guard.auth.AuthService

@Composable
fun keyboardAwarePadding(): Dp {
    val view = LocalView.current
    val density = LocalDensity.current
    var keyboardHeight by remember { mutableIntStateOf(0) }

    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val visibleHeight = rect.height()
            val keyboardHeightPx = screenHeight - visibleHeight
            keyboardHeight = if (keyboardHeightPx > screenHeight * 0.15) keyboardHeightPx else 0
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(listener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    return with(density) { keyboardHeight.toDp() }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onRegisterClicked: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf(false) }

    val keyboardHeight = keyboardAwarePadding()
    val logoOffset by animateDpAsState(targetValue = if (keyboardHeight > 0.dp) 50.dp else 150.dp,
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Fondo rosa que cubre toda la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2D3D0)) // Aplica el color rosa a todo el fondo
        )

        // Parte superior con el logo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.3f), // Ajusta la altura de la parte superior
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.img_logo_login),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(180.dp)
                    .background(color = Color.Transparent, shape = CircleShape)
                    .padding(20.dp)
            )
        }

        // Parte inferior (Columna blanca con esquinas redondeadas)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.7f) // Ocupa la parte inferior
                .align(Alignment.BottomCenter) // Se coloca en la parte inferior del Box
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topEnd = 70.dp) // Esquinas redondeadas
                )
                .clip(RoundedCornerShape(topEnd = 70.dp))
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "INICIO",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp),
                color = Color(0xFF594012),
                fontSize = 30.sp
            )

            Text(
                text = "Inicie sesión para continuar",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color(0xFF594012),
                fontSize = 16.sp
            )

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFD4D2D3),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFD4D2D3),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val iconText = if (showPassword) "Hide" else "Mostrar"
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(iconText, fontSize = 12.sp, color = Color(0xFF594012))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    AuthService().login(username, password) { success ->
                        if (success) {
                            onLoginSuccess()
                        } else {
                            loginError = true
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4A0C0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                Text(
                    text = "Iniciar sesión",
                    fontSize = 16.sp,
                    color = Color(0xFF594012),
                )
            }

            if (loginError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Usuario o contraseña inválidos.",
                    color = MaterialTheme.colors.error,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¿Has olvidad tu contraseña?",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp),
                color = Color(0xFF594012)
            )

            TextButton(onClick = { onRegisterClicked() }) {
                Text("Registrate ahora", color = Color(0xFF594012))
            }
        }
    }

}

