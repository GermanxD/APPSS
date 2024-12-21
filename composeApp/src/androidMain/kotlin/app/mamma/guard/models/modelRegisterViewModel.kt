package app.mamma.guard.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

// Estado de la pantalla de registro
data class RegisterState(
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val errorMessage: String? = null
)

// Eventos que pueden ocurrir en la pantalla de registro
sealed class RegisterEvent {
    data class UsernameChanged(val username: String) : RegisterEvent()
    data class FirstNameChanged(val firstName: String) : RegisterEvent()
    data class LastNameChanged(val lastName: String) : RegisterEvent()
    data class MiddleNameChanged(val middleName: String) : RegisterEvent()
    data class GenderChanged(val gender: String) : RegisterEvent()
    data class BirthDateChanged(val birthDate: String) : RegisterEvent()
    data class EmailChanged(val email: String) : RegisterEvent()
    data class PasswordChanged(val password: String) : RegisterEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()
    object TogglePasswordVisibility : RegisterEvent()
    object Register : RegisterEvent()
    object ClearForm : RegisterEvent()
}

// ViewModel de registro
class RegisterViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Estado mutable
    private val _state = mutableStateOf(RegisterState())
    val state get() = _state.value

    // Llamado desde el UI para registrar un nuevo usuario
    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.Register -> {
                registerUser()
            }
            // Manejo de otros eventos (cambio de texto, etc.)
            is RegisterEvent.UsernameChanged -> _state.value = state.copy(username = event.username)
            is RegisterEvent.FirstNameChanged -> _state.value = state.copy(firstName = event.firstName)
            is RegisterEvent.LastNameChanged -> _state.value = state.copy(lastName = event.lastName)
            is RegisterEvent.MiddleNameChanged -> _state.value = state.copy(middleName = event.middleName)
            is RegisterEvent.GenderChanged -> _state.value = state.copy(gender = event.gender)
            is RegisterEvent.BirthDateChanged -> _state.value = state.copy(birthDate = event.birthDate)
            is RegisterEvent.EmailChanged -> _state.value = state.copy(email = event.email)
            is RegisterEvent.PasswordChanged -> _state.value = state.copy(password = event.password)
            is RegisterEvent.ConfirmPasswordChanged -> _state.value = state.copy(confirmPassword = event.confirmPassword)
            is RegisterEvent.TogglePasswordVisibility -> _state.value = state.copy(showPassword = !state.showPassword)
            is RegisterEvent.ClearForm -> _state.value = RegisterState() // Limpiar formulario
        }
    }

    // Método para registrar al usuario en Firebase Auth y Firestore
    private fun registerUser() {
        // Verificar si las contraseñas coinciden
        if (state.password != state.confirmPassword) {
            _state.value = state.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }

        // Mostrar que estamos en proceso de carga
        _state.value = state.copy(isLoading = true)

        // Realizamos el registro en Firebase Auth
        auth.createUserWithEmailAndPassword(state.email, state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Si el registro es exitoso, obtenemos el usuario
                    val user = auth.currentUser
                    user?.let {
                        // Agregar los detalles del usuario a Firestore
                        val userData = hashMapOf(
                            "username" to state.username,
                            "firstName" to state.firstName,
                            "lastName" to state.lastName,
                            "email" to state.email,
                            "birthDate" to state.birthDate,
                            "gender" to state.gender
                        )

                        // Referencia a la colección "users"
                        firestore.collection("users")
                            .document(user.uid)  // Usamos el UID del usuario para la clave del documento
                            .set(userData)
                            .addOnSuccessListener {
                                // Registro exitoso
                                _state.value = state.copy(isRegistered = true, isLoading = false)
                            }
                            .addOnFailureListener { exception ->
                                // Maneja el error al guardar en Firestore
                                _state.value = state.copy(errorMessage = exception.message, isLoading = false)
                            }
                    }
                } else {
                    // Maneja el error en Firebase Auth
                    _state.value = state.copy(errorMessage = task.exception?.message, isLoading = false)
                }
            }
    }
}
