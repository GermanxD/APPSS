package app.cui.ro.ui.session

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.navigation.compose.rememberNavController
import app.cui.ro.R
import app.cui.ro.models.RegisterEvent
import app.cui.ro.models.VMRegister
import app.cui.ro.ui.CustomTextField
import app.cui.ro.ui.GenderButton
import app.cui.ro.ui.theme.CuiroColors
import java.util.Calendar

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackClicked: () -> Unit,
    viewModel: VMRegister = viewModel()
) {
    val state = viewModel.state
    val calendar = Calendar.getInstance()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var selectedGender by remember { mutableStateOf<String?>(null) }
    val navController = rememberNavController()

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

    androidx.compose.material3.Scaffold(
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.35f)
                    .background(app.cui.ro.ui.theme.CuiroColors.PrimaryPink),
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_general),
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

                    TextButton(onClick = onBackClicked) {
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
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Seleccionar fecha"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        GenderButton(
                            gender = "Masculino",
                            isSelected = selectedGender == "Masculino"
                        ) {
                            selectedGender = "Masculino"
                            viewModel.onEvent(RegisterEvent.GenderChanged("Masculino"))
                        }
                        GenderButton(
                            gender = "Femenino",
                            isSelected = selectedGender == "Femenino"
                        ) {
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
                                Text(
                                    if (state.showPassword) "Ocultar" else "Mostrar",
                                    fontSize = 12.sp,
                                    color = app.cui.ro.ui.theme.CuiroColors.FontBrown
                                )

                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    CustomTextField(
                        value = state.confirmPassword,
                        onValueChange = {
                            viewModel.onEvent(
                                RegisterEvent.ConfirmPasswordChanged(
                                    it
                                )
                            )
                        },
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
                        onClick = {
                            viewModel.onEvent(RegisterEvent.Register)
                        },
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth().height(45.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = CuiroColors.ObjectsPink)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text(
                                text = "Registrarse",
                                fontSize = 16.sp,
                                color = CuiroColors.FontBrown
                            )
                        }
                    }
                }
            }
        }
    }
}