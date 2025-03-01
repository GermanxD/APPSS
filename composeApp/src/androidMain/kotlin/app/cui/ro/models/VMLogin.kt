package app.cui.ro.models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cui.ro.auth.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    // Función simulada de inicio de sesión
    fun loginWithUsername(username: String, password: String, context: Context, successCallback: () -> Unit, errorCallback: () -> Unit) {
        _state.value = _state.value.copy(isLoading = true) // Activar loading

        viewModelScope.launch {
            // Simulación de un proceso de login
            AuthService().loginWithUsername(username, password, context) { success, _ ->
                _state.value = _state.value.copy(isLoading = false) // Desactivar loading

                if (success) {
                    successCallback()
                } else {
                    errorCallback()
                }
            }
        }
    }

}

data class LoginState(
    val isLoading: Boolean = false,
)