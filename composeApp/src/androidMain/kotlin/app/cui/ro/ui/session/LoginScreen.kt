package app.cui.ro.ui.session

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cui.ro.R
import app.cui.ro.models.VMLogin
import app.cui.ro.ui.theme.CuiroColors

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
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClicked: () -> Unit,
    viewModel: VMLogin = viewModel(),
) {
    val state = viewModel.state.collectAsState().value
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf(false) }

    keyboardAwarePadding()

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Fondo rosa que cubre toda la pantalla
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CuiroColors.PrimaryPink) // Aplica el color rosa a todo el fondo
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
                    painter = painterResource(R.drawable.ic_general),
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
                    color = CuiroColors.FontBrown,
                    fontSize = 30.sp
                )

                Text(
                    text = "Inicie sesión para continuar",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = CuiroColors.FontBrown,
                    fontSize = 16.sp
                )

                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario") },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = CuiroColors.PinkFields,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = CuiroColors.FontBrown,
                        focusedLabelColor = CuiroColors.FontBrown,
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
                        backgroundColor = CuiroColors.PinkFields,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = CuiroColors.FontBrown,
                        focusedLabelColor = CuiroColors.FontBrown,
                    ),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val iconText = if (showPassword) "Ocultar" else "Mostrar"
                        TextButton(onClick = { showPassword = !showPassword }) {
                            Text(iconText, fontSize = 12.sp, color = CuiroColors.FontBrown)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                val context = LocalContext.current

                Button(
                    onClick = {
                        viewModel.loginWithUsername(
                            username,
                            password,
                            context,
                            onLoginSuccess,
                            { loginError = true })
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = CuiroColors.ObjectsPink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text(
                            text = "Iniciar sesión",
                            fontSize = 16.sp,
                            color = CuiroColors.FontBrown,
                        )
                    }
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
                    text = "¿Has olvidado tu contraseña?",
                    style = TextStyle(),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    color = CuiroColors.FontBrown,
                    fontSize = 16.sp
                )

                TextButton(
                    onClick = {
                        onRegisterClicked()
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Regístrate ahora",
                            style = TextStyle(),
                            color = CuiroColors.FontBrown,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

