package app.cui.ro.models

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Actualizar el ViewModel
class VMNotifications(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLek", "StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext


    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading


    fun loadNotifications() {
        _isLoading.value = true
        // Simular carga asíncrona
        viewModelScope.launch {
            delay(500) // Simular delay de carga

            // Sincronizar contador con notificaciones no leídas reales
            }

            _isLoading.value = false
        }
    }

    fun markAsRead(notificationId: String) {
    }

    fun markAllAsRead() {
    }

    fun deleteNotification(notificationId: String) {
    }

    fun clearAllNotifications() {
    }





