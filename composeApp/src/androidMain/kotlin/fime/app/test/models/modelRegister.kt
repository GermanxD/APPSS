package fime.app.test.models

data class RegisterData(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val gender: String,
    val birthDate: String
) {
    fun isValid(): Boolean {
        return username.isNotBlank() &&
                email.isNotBlank() &&
                password == confirmPassword &&
                password.length >= 6 &&
                firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                gender.isNotBlank() &&
                birthDate.isNotBlank()
    }

    fun isEmailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
