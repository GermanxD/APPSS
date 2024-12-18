package fime.app.test.ui

import GlideImageFromResource
import android.app.DatePickerDialog
import android.widget.DatePicker
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import fime.app.test.R
import fime.app.test.models.RegisterData
import java.util.Calendar


@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onBackClicked: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var registerError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0288D1))
    ) {
        // Botón de regresar
        IconButton(
            onClick = onBackClicked,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .zIndex(1f)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            GlideImageFromResource(
                resourceId = R.drawable.img_logo_login,
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.White, shape = CircleShape)
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campos de texto
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre(s)") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido Paterno") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = middleName,
                onValueChange = { middleName = it },
                label = { Text("Apellido Materno") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Género") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            val datePickerDialog = remember {
                DatePickerDialog(
                    context,
                    { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        birthDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }

            TextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("Fecha de Nacimiento") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color(0xFFF5F5F5)),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color(0xFFF5F5F5)),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(if (showPassword) "Ocultar" else "Mostrar", fontSize = 12.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Repetir Contraseña") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color(0xFFF5F5F5)),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val registerData = RegisterData(
                        username = username,
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        firstName = firstName,
                        lastName = lastName,
                        middleName = middleName,
                        gender = gender,
                        birthDate = birthDate
                    )

                    if (registerData.isValid() && registerData.isEmailValid()) {
                        onRegisterSuccess()
                    } else {
                        registerError = true
                        errorMessage = "Por favor, revisa tus datos. Asegúrate de que todos los campos sean válidos."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Registrarse")
            }

            if (registerError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colors.error,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

