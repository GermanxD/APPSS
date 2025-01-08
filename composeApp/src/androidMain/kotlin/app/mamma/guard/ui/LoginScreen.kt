package app.mamma.guard.ui

import android.content.Context
import android.graphics.Rect
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0288D1),
                        Color(0xFFB3E5FC)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(logoOffset))

            GlideImageFromResource(
                resourceId = R.drawable.img_logo_login,
                modifier = Modifier
                    .size(170.dp)
                    .background(Color.White, shape = CircleShape)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFF5F5F5),
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
                    backgroundColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val iconText = if (showPassword) "Hide" else "Show"
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(iconText, fontSize = 12.sp)
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
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                Text("Iniciar sesión", fontSize = 16.sp)
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

            TextButton(onClick = { onRegisterClicked() }) {
                Text("Registrate ahora", color = MaterialTheme.colors.primary)
            }
        }
    }
}

@Composable
fun GlideImageFromResource(resourceId: Int, modifier: Modifier) {
    AndroidView(factory = { context: Context ->
        ImageView(context).apply {
            Glide.with(context)
                .load(resourceId)
                .circleCrop()
                .into(this)
        }
    },
        modifier = modifier
    )
}