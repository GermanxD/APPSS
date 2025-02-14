package app.cui.ro.ui.session

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import app.cui.ro.R
import app.cui.ro.models.RegisterEvent
import app.cui.ro.models.RegisterViewModel
import java.util.Calendar

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackClicked: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val state = viewModel.state
    val calendar = Calendar.getInstance()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var selectedGender by remember { mutableStateOf<String?>(null) }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                viewModel.onEvent(RegisterEvent.BirthDateChanged("$dayOfMonth/${monthOfYear + 1}/$year"))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    LaunchedEffect(key1 = state.isRegistered) {
        if (state.isRegistered) {
            onRegisterSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Fondo rosa con el logo y el botón de retroceso
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .background(Color(0xFFF2D3D0)),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(R.drawable.img_logo_login),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(vertical = 15.dp)
            )
        }

        IconButton(
            onClick = onBackClicked,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        // Contenedor blanco con scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp)
                .background(Color.White, shape = RoundedCornerShape(topStart = 70.dp))
                .clip(RoundedCornerShape(topStart = 70.dp))
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Crea una nueva cuenta",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "¿Ya estás registrado?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    style = TextStyle(),
                    )

                TextButton(onClick =  onBackClicked ) {
                    Text(
                        "Inicia sesión aquí",
                        fontSize = 16.sp,
                        color = Color.Black,
                        style = TextStyle(),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = state.username,
                    onValueChange = { viewModel.onEvent(RegisterEvent.UsernameChanged(it)) },
                    label = "Usuario",
                    maxLength = 20,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = state.fullname,
                    onValueChange = { viewModel.onEvent(RegisterEvent.FullNameChanged(it)) },
                    label = "Nombre completo",
                    maxLength = 50,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = state.birthDate,
                    onValueChange = {},
                    label = "Fecha de Nacimiento",
                    maxLength = 10,
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    GenderButton(gender = "Masculino", isSelected = selectedGender == "Masculino") {
                        selectedGender = "Masculino"
                        viewModel.onEvent(RegisterEvent.GenderChanged("Masculino"))
                    }
                    GenderButton(gender = "Femenino", isSelected = selectedGender == "Femenino") {
                        selectedGender = "Femenino"
                        viewModel.onEvent(RegisterEvent.GenderChanged("Femenino"))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(RegisterEvent.EmailChanged(it)) },
                    label = "Correo Electrónico",
                    maxLength = 100,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) },
                    label = "Contraseña",
                    maxLength = 16,
                    visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        TextButton(onClick = { viewModel.onEvent(RegisterEvent.TogglePasswordVisibility) }) {
                            Text(if (state.showPassword) "Ocultar" else "Mostrar", fontSize = 12.sp,  color = Color(0xFF594012))

                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = state.confirmPassword,
                    onValueChange = { viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
                    label = "Confirmar contraseña",
                    maxLength = 16,
                    visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = { viewModel.onEvent(RegisterEvent.Register) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4A0C0)),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("Registrarse",
                            fontSize = 16.sp,
                            color = Color(0xFF594012)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GenderButton(gender: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) Color(0xFFF4A0C0) else Color.White,
            contentColor = if (isSelected) Color.White else Color.Gray // Cambiar color del texto.
        ),
        border = if (!isSelected) BorderStroke(
            1.dp,
            Color.Gray
        ) else null // Opcional: agregar un borde para resaltar.
    ) {
        Text(text = gender)
    }
}


@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    maxLength: Int? = null // Nuevo parámetro para definir el límite de caracteres.
) {
    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = {
                if (maxLength == null || it.length <= maxLength) {
                    onValueChange(it)
                }
            },
            label = { Text(label) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xFFD4D2D3),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF594012),
                focusedLabelColor = Color(0xFF594012),
            ),
            readOnly = readOnly,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            trailingIcon = trailingIcon,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (maxLength != null) {
            Text(
                text = "${value.length} / $maxLength", style =
                MaterialTheme.typography.caption.copy(
                    fontSize =
                    12.sp
                ), color =
                if (value.length > maxLength) Color.Red else Color.White, modifier =
                Modifier.align(Alignment.End)
            )
        }
    }
}


fun navigateBack(navController: NavController) {
    navController.popBackStack()
}
