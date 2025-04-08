package app.cui.ro.models
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class VMHealthConnect : ViewModel() {

    private val _healthConnectAvailability = MutableStateFlow(HealthConnectAvailability.NOT_CHECKED)
    val healthConnectAvailability: StateFlow<HealthConnectAvailability> = _healthConnectAvailability

    private val _hasHealthConnectPermissions = MutableStateFlow(false)
    val hasHealthConnectPermissions: StateFlow<Boolean> = _hasHealthConnectPermissions

    // Lista de permisos requeridos
    val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
    )

    fun checkHealthConnectAvailability(context: Context) {
        viewModelScope.launch {
            try {
                val availabilityStatus = HealthConnectClient.getSdkStatus(context)
                _healthConnectAvailability.value = when (availabilityStatus) {
                    HealthConnectClient.SDK_AVAILABLE -> HealthConnectAvailability.AVAILABLE
                    HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectAvailability.UPDATE_REQUIRED
                    HealthConnectClient.SDK_UNAVAILABLE -> HealthConnectAvailability.NOT_INSTALLED
                    else -> HealthConnectAvailability.NOT_INSTALLED // Default a no instalado
                }
                // Si está disponible, verifica el estado actual de los permisos
                if (_healthConnectAvailability.value == HealthConnectAvailability.AVAILABLE) {
                    updatePermissionsState(context)
                } else {
                    _hasHealthConnectPermissions.value = false // No puede tener permisos si no está disponible/instalado
                }
            } catch (e: Exception) {
                Log.e("VMHealthConnect", "Error checking HC availability: ${e.message}", e)
                _healthConnectAvailability.value = HealthConnectAvailability.NOT_INSTALLED
                _hasHealthConnectPermissions.value = false
            }
        }
    }

    fun openHealthConnectSettings(context: Context) {
        val providerPackageName = "com.google.android.apps.healthdata"
        // Intenta abrir la app directamente primero si está instalada
        val intent = context.packageManager.getLaunchIntentForPackage(providerPackageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            // Si no, intenta abrir la Play Store
            val uriString = "market://details?id=$providerPackageName"
            val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(uriString)
                setPackage("com.android.vending") // Especifica la Play Store
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(playStoreIntent)
            } catch (e: Exception) {
                Log.e("VMHealthConnect", "Error opening Play Store or Health Connect: ${e.message}")
                // Opcional: Mostrar un mensaje al usuario indicando que no se pudo abrir
            }
        }
    }

    fun requestPermissions(
        context: Context,
        requestPermissionLauncher: ActivityResultLauncher<Set<String>> // Cambiado a Set<String>
    ) {
        viewModelScope.launch {
            try {
                if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE) {
                    val healthConnectClient = HealthConnectClient.getOrCreate(context)
                    val granted = healthConnectClient.permissionController.getGrantedPermissions()

                    // Solicitar solo si los permisos necesarios NO están ya concedidos
                    if (!granted.containsAll(PERMISSIONS)) {
                        withContext(Dispatchers.Main) {
                            requestPermissionLauncher.launch(PERMISSIONS) // Lanza con el Set
                        }
                    } else {
                        // Si ya estaban concedidos (quizás por una comprobación anterior), actualiza el estado
                        _hasHealthConnectPermissions.value = true
                    }
                }
            } catch (e: Exception) {
                Log.e("VMHealthConnect", "Error requesting permissions: ${e.message}", e)
                // No reintenta automáticamente, el usuario puede volver a intentarlo desde la UI
            }
        }
    }

    fun updatePermissionsState(context: Context) {
        // Solo intenta actualizar si sabemos que HC está disponible
        if (healthConnectAvailability.value != HealthConnectAvailability.AVAILABLE) {
            _hasHealthConnectPermissions.value = false
            return
        }

        viewModelScope.launch {
            try {
                // Volver a verificar la disponibilidad por si acaso cambió
                if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE) {
                    val healthConnectClient = HealthConnectClient.getOrCreate(context)
                    val granted = healthConnectClient.permissionController.getGrantedPermissions()
                    _hasHealthConnectPermissions.value = granted.containsAll(PERMISSIONS)
                } else {
                    // Si ya no está disponible, no hay permisos
                    _hasHealthConnectPermissions.value = false
                    // Opcional: Podrías querer actualizar también _healthConnectAvailability aquí
                }
            } catch (e: Exception) {
                Log.e("VMHealthConnect", "Error updating permissions state: ${e.message}")
                _hasHealthConnectPermissions.value = false // Asume que no hay permisos si hay error
            }
        }
    }

    suspend fun readStepsForDate(client: HealthConnectClient, date: ZonedDateTime): Long {
        return try {
            val startTime = date.truncatedTo(ChronoUnit.DAYS).toInstant() // Inicio del día
            val endTime = startTime.plus(1, ChronoUnit.DAYS) // Fin del día
            val request = androidx.health.connect.client.request.ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            val response = client.readRecords(request)
            response.records.sumOf { it.count }
        } catch (e: Exception) {
            Log.e("VMHealthConnect", "Error reading steps: ${e.message}", e)
            0L // Retorna 0 si hay error (ej. permisos revocados entre chequeo y lectura)
        }
    }

    enum class HealthConnectAvailability {
        NOT_CHECKED,
        AVAILABLE,
        NOT_INSTALLED,
        UPDATE_REQUIRED
    }
}



