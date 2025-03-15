package app.cui.ro.models

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.ZonedDateTime

class VMHealthConnect : ViewModel() {

    // Función para leer pasos desde Health Connect
    suspend fun readStepsForDate(client: HealthConnectClient, date: ZonedDateTime): Long {
        // Convertir ZonedDateTime a Instant
        val startTime = date.toInstant()
        val endTime = date.plusDays(1).toInstant()

        // Crear el filtro de rango de tiempo
        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)

        // Leer los registros de pasos
        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = timeRangeFilter
            )
        )

        // Sumar el conteo de pasos de todos los registros
        return response.records.sumOf { it.count }
    }

    suspend fun checkHealthConnectAvailability(
        context: Context,
        healthConnectClient: HealthConnectClient,
        requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
        hasHealthConnectPermissions: MutableStateFlow<Boolean>
    ) {
        val providerPackageName = "com.google.android.apps.healthdata" // Health Connect
        val availabilityStatus = HealthConnectClient.getSdkStatus(context, providerPackageName)
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            // Health Connect no está disponible
            println("Health Connect no está disponible")
            return
        }

        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            // Redirige al usuario para que actualice el proveedor de Health Connect
            val uriString = "market://details?id=com.google.android.apps.healthdata"
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
                    setPackage("com.android.vending")
                }
            )
            return
        }

        // Verificar permisos
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        hasHealthConnectPermissions.value = granted.containsAll(PERMISSIONS)

        if (!hasHealthConnectPermissions.value) {
            requestPermissionLauncher.launch(PERMISSIONS.toTypedArray())
        }
    }

    val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
    )

}



