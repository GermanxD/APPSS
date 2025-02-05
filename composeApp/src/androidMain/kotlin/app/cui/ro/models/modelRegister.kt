package app.cui.ro.models

data class RegisterState(
    val username: String = "",
    val fullname: String = "",
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

sealed class RegisterEvent {
    data class UsernameChanged(val username: String) : RegisterEvent()
    data class FullNameChanged(val fullname: String) : RegisterEvent()
    data class GenderChanged(val gender: String) : RegisterEvent()
    data class BirthDateChanged(val birthDate: String) : RegisterEvent()
    data class EmailChanged(val email: String) : RegisterEvent()
    data class PasswordChanged(val password: String) : RegisterEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()
    data object TogglePasswordVisibility : RegisterEvent()
    data object Register : RegisterEvent()
    data object ClearForm : RegisterEvent()
}
