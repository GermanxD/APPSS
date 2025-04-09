package app.cui.ro.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class VMRegister : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _state = mutableStateOf(RegisterState())
    val state get() = _state.value

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.Register -> registerUser()
            is RegisterEvent.UsernameChanged -> _state.value = state.copy(username = event.username)
            is RegisterEvent.FullNameChanged -> _state.value = state.copy(fullname = event.fullname)
            is RegisterEvent.GenderChanged -> _state.value = state.copy(gender = event.gender)
            is RegisterEvent.BirthDateChanged -> _state.value = state.copy(birthDate = event.birthDate)

            is RegisterEvent.EmailChanged -> _state.value = state.copy(email = event.email)
            is RegisterEvent.PasswordChanged -> _state.value = state.copy(password = event.password)
            is RegisterEvent.ConfirmPasswordChanged -> _state.value = state.copy(confirmPassword = event.confirmPassword)

            is RegisterEvent.TogglePasswordVisibility -> _state.value = state.copy(showPassword = !state.showPassword)

            is RegisterEvent.ClearForm -> _state.value = RegisterState()
        }
    }

    private fun registerUser() {
        when {
            state.username.isBlank() -> {
                _state.value = state.copy(errorMessage = "El nombre de usuario es obligatorio")
                return
            }

            state.fullname.isBlank() -> {
                _state.value = state.copy(errorMessage = "El nombre completo es obligatorio")
                return
            }

            state.birthDate.isBlank() -> {
                _state.value = state.copy(errorMessage = "La fecha de nacimiento es obligatoria")
                return
            }

            state.gender.isBlank() -> {
                _state.value = state.copy(errorMessage = "El género es obligatorio")
                return
            }

            state.email.isBlank() -> {
                _state.value = state.copy(errorMessage = "El correo electrónico es obligatorio")
                return
            }

            !isValidEmail(state.email) -> {
                _state.value = state.copy(errorMessage = "El correo electrónico no tiene un formato válido")
                return
            }

            state.password.isBlank() -> {
                _state.value = state.copy(errorMessage = "La contraseña es obligatoria")
                return
            }

            state.confirmPassword.isBlank() -> {
                _state.value = state.copy(errorMessage = "La confirmación de la contraseña es obligatoria")
                return
            }

            state.password != state.confirmPassword -> {
                _state.value = state.copy(errorMessage = "Las contraseñas no coinciden")
                return
            }
        }

        _state.value = state.copy(isLoading = true)

        auth.createUserWithEmailAndPassword(state.email, state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userData = hashMapOf(
                            "username" to state.username,
                            "fullname" to state.fullname,
                            "email" to state.email,
                            "birthDate" to state.birthDate,
                            "gender" to state.gender,
                        )

                        firestore.collection("users")
                            .document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                _state.value = state.copy(
                                    isRegistered = true,
                                    isLoading = false,
                                    errorMessage = null
                                )
                            }
                            .addOnFailureListener { exception ->
                                _state.value = state.copy(
                                    errorMessage = exception.message,
                                    isLoading = false
                                )
                            }
                    }
                } else {
                    _state.value = state.copy(
                        errorMessage = task.exception?.message,
                        isLoading = false
                    )
                }
            }
    }
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z](.*)(@)(.+)(\\.)(.+)")
    return emailRegex.matches(email)
}
