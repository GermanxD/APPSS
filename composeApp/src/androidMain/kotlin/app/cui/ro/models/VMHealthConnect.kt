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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
            _healthConnectAvailability.value = HealthConnectAvailability.NOT_CHECKED // Reset
            val providerPackageName = "com.google.android.apps.healthdata"
            try {
                val availabilityStatus = HealthConnectClient.getSdkStatus(context, providerPackageName)

                when (availabilityStatus) {
                    HealthConnectClient.SDK_UNAVAILABLE -> {
                        _healthConnectAvailability.value = HealthConnectAvailability.NOT_INSTALLED
                    }
                    HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                        _healthConnectAvailability.value = HealthConnectAvailability.UPDATE_REQUIRED
                    }
                    else -> {
                        _healthConnectAvailability.value = HealthConnectAvailability.AVAILABLE
                    }
                }
            } catch (e: Exception) {
                Log.e("VMHealthConnect", "Error checking Health Connect availability: ${e.message}")
                _healthConnectAvailability.value = HealthConnectAvailability.NOT_INSTALLED  // or handle differently
            }
        }
    }

    fun openHealthConnectSettings(context: Context) {
        val providerPackageName = "com.google.android.apps.healthdata"
        val uriString = "market://details?id=$providerPackageName&url=healthconnect%3A%2F%2Fonboarding"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setPackage("com.android.vending")
            data = Uri.parse(uriString)
            putExtra("overlay", true)
            putExtra("callerId", context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Añade esta línea
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("VMHealthConnect", "Error opening Play Store: ${e.message}")
            // Handle the error, e.g., show a message to the user
        }
    }


    fun requestPermissions(
        context: Context,
        requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    ) {
        viewModelScope.launch {
            // Solo solicita permisos si Health Connect está disponible
            if (healthConnectAvailability.value == HealthConnectAvailability.AVAILABLE) {
                try {
                    val healthConnectClient = HealthConnectClient.getOrCreate(context)
                    val granted = healthConnectClient.permissionController.getGrantedPermissions()
                    _hasHealthConnectPermissions.value = granted.containsAll(PERMISSIONS)

                    if (!_hasHealthConnectPermissions.value) {
                        requestPermissionLauncher.launch(PERMISSIONS.toTypedArray())
                    }
                } catch (e: Exception) {
                    Log.e("VMHealthConnect", "Error getting HealthConnectClient or requesting permissions: ${e.message}")
                    _hasHealthConnectPermissions.value = false //  En caso de error, establece que no hay permisos.
                }
            } else {
                _hasHealthConnectPermissions.value = false
            }
        }
    }

    fun updatePermissionsState(context: Context) {
        viewModelScope.launch {
            // Solo actualiza el estado si Health Connect está disponible
            if (healthConnectAvailability.value == HealthConnectAvailability.AVAILABLE) {
                try {
                    val healthConnectClient = HealthConnectClient.getOrCreate(context)
                    val granted = healthConnectClient.permissionController.getGrantedPermissions()
                    _hasHealthConnectPermissions.value = granted.containsAll(PERMISSIONS)
                } catch (e: Exception) {
                    Log.e("VMHealthConnect", "Error getting HealthConnectClient or updating permissions: ${e.message}")
                    _hasHealthConnectPermissions.value = false // En caso de error, establece que no hay permisos.
                }
            } else {
                _hasHealthConnectPermissions.value = false
            }
        }
    }

    suspend fun readStepsForDate(client: HealthConnectClient, date: ZonedDateTime): Long {
        val startTime = date.toInstant()
        val endTime = date.plusDays(1).toInstant()
        val response = client.readRecords(
            androidx.health.connect.client.request.ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = androidx.health.connect.client.time.TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response.records.sumOf { it.count }
    }

    enum class HealthConnectAvailability {
        NOT_CHECKED,
        AVAILABLE,
        NOT_INSTALLED,
        UPDATE_REQUIRED
    }
}



