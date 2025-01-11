package app.mamma.guard.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import app.mamma.guard.R
import app.mamma.guard.models.RegisterEvent
import app.mamma.guard.models.RegisterViewModel
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
        modifier = Modifier.fillMaxSize().background(Color(0xFF0288D1))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier
                    .align(Alignment.Start) // Asegura la posición en la esquina
                    .zIndex(1f)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .fillMaxSize()
                )
            }

            Image(
                painter = painterResource(R.drawable.img_logo_login),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp).background(Color.White, shape = CircleShape).padding(12.dp)
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Divider(
                color = Color.White,
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Datos personales",
                style = MaterialTheme.typography.h6,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                color = Color.White,
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Usuario
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

            // Nombre(s)
            CustomTextField(
                value = state.firstName,
                onValueChange = { viewModel.onEvent(RegisterEvent.FirstNameChanged(it)) },
                label = "Nombre(s)",
                maxLength = 50,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Apellido Paterno
            CustomTextField(
                value = state.lastName,
                onValueChange = { viewModel.onEvent(RegisterEvent.LastNameChanged(it)) },
                label = "Apellido Paterno",
                maxLength = 50,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Apellido Materno
            CustomTextField(
                value = state.middleName,
                onValueChange = { viewModel.onEvent(RegisterEvent.MiddleNameChanged(it)) },
                label = "Apellido Materno",
                maxLength = 50,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha de Nacimiento
            CustomTextField(
                value = state.birthDate,
                onValueChange = { viewModel.onEvent(RegisterEvent.BirthDateChanged(it)) },
                maxLength = 10,
                label = "Fecha de Nacimiento",
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                }
            )

            // Botones de Género
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GenderButton(
                    gender = "Masculino",
                    isSelected = selectedGender == "Masculino",
                    onClick = {
                        selectedGender = "Masculino"
                        viewModel.onEvent(RegisterEvent.GenderChanged("Masculino"))
                    }
                )
                GenderButton(
                    gender = "Femenino",
                    isSelected = selectedGender == "Femenino",
                    onClick = {
                        selectedGender = "Femenino"
                        viewModel.onEvent(RegisterEvent.GenderChanged("Femenino"))
                    }
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Divider(
                color = Color.White,
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Registra tu cuenta",
                style = MaterialTheme.typography.h6,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                color = Color.White,
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(top = 32.dp))

            // Correo Electrónico
            CustomTextField(
                value = state.email,
                onValueChange = { viewModel.onEvent(RegisterEvent.EmailChanged(it)) },
                label = "Correo Electrónico",
                maxLength = 100,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Contraseña
            CustomTextField(
                value = state.password,
                onValueChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) },
                label = "Contraseña",
                maxLength = 16,
                visualTransformation =
                if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon =
                {
                    TextButton(onClick =
                    { viewModel.onEvent(RegisterEvent.TogglePasswordVisibility) }) {
                        Text(if (state.showPassword) "Ocultar" else "Mostrar", fontSize =
                        12.sp)
                    }
                },
                modifier =
                Modifier.fillMaxWidth()
            )

            Spacer(modifier =
            Modifier.height(8.dp))

            // Repetir Contraseña
            CustomTextField(
                value =
                state.confirmPassword,
                onValueChange =
                { viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
                label =
                "Repetir Contraseña",
                maxLength =
                16,
                visualTransformation =
                if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions =
                KeyboardOptions(keyboardType =
                KeyboardType.Password),
                modifier =
                Modifier.fillMaxWidth()
            )

            Spacer(modifier =
            Modifier.height(16.dp))

            // Mostrar errores
            if (state.errorMessage != null) {
                Text(
                    text =
                    state.errorMessage,
                    color =
                    Color.Red,
                    textAlign =
                    TextAlign.Center,
                    modifier =
                    Modifier.fillMaxWidth()
                )
                Spacer(modifier =
                Modifier.height(8.dp))
            }

            // Botón de Registro
            Button(
                onClick =
                { viewModel.onEvent(RegisterEvent.Register) },
                modifier =
                Modifier.fillMaxWidth().height(45.dp),
                shape =
                RoundedCornerShape(8.dp),
                enabled =
                !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color =
                    Color.White)
                } else {
                    Text("Registrarse")
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
            backgroundColor = if (isSelected) MaterialTheme.colors.primary else Color.White,
            contentColor = if (isSelected) Color.White else Color.Gray // Cambiar color del texto.
        ),
        border = if (!isSelected) BorderStroke(1.dp, Color.Gray) else null // Opcional: agregar un borde para resaltar.
    ) {
        Text(text = gender)
    }
}







@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier=Modifier,
    readOnly: Boolean=false,
    visualTransformation: VisualTransformation=VisualTransformation.None,
    keyboardOptions: KeyboardOptions=KeyboardOptions.Default,
    keyboardActions: KeyboardActions=KeyboardActions.Default,
    trailingIcon: @Composable (() -> Unit)?=null,
    maxLength: Int?=null // Nuevo parámetro para definir el límite de caracteres.
) {
    Column(modifier=modifier) {
        TextField(
            value=value,
            onValueChange={
                if(maxLength==null || it.length <= maxLength) {
                    onValueChange(it)
                }
            },
            label={Text(label)},
            shape=RoundedCornerShape(12.dp),
            colors=TextFieldDefaults.textFieldColors(
                backgroundColor=Color(0xFFF5F5F5),
                focusedIndicatorColor=Color.Transparent,
                unfocusedIndicatorColor=Color.Transparent),
            readOnly=readOnly,
            visualTransformation=visualTransformation,
            keyboardOptions=keyboardOptions,
            keyboardActions=keyboardActions,
            trailingIcon=trailingIcon,
            modifier=Modifier.fillMaxWidth()
        )

        Spacer(modifier=Modifier.height(4.dp))

        if(maxLength!=null){
            Text(text="${value.length} / $maxLength", style=
            MaterialTheme.typography.caption.copy(fontSize=
            12.sp), color=
            if(value.length>maxLength) Color.Red else Color.White, modifier=
            Modifier.align(Alignment.End))
        }
    }
}


fun navigateBack(navController: NavController) {
    navController.popBackStack()
}
