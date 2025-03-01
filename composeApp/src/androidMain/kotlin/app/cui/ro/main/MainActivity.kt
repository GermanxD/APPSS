package app.cui.ro.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch
import androidx.health.connect.client.permission.HealthPermission


class MainActivity : ComponentActivity() {

    private lateinit var healthConnectClient: HealthConnectClient

    // Solicitar permisos de Health Connect
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                lifecycleScope.launch { checkPermissionsAndRun() }
            } else {
                // Permisos no concedidos, maneja el error
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // Si Health Connect no está disponible, redirige al usuario para instalarlo.
        checkHealthConnectAvailability(this)

        setContent {
            App(context = this)
        }
    }

    private fun checkHealthConnectAvailability(context: Context) {
        val providerPackageName = "com.google.android.apps.healthdata" // Health Connect
        val availabilityStatus = HealthConnectClient.getSdkStatus(context, "com.google.android.apps.healthdata")
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            // Health Connect no está disponible
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

        healthConnectClient = HealthConnectClient.getOrCreate(context)

        lifecycleScope.launch {
            checkPermissionsAndRun()
        }
    }

    private suspend fun checkPermissionsAndRun() {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (granted.containsAll(PERMISSIONS)) {
            proceedWithHealthConnect()
        } else {
            // Si no se han concedido los permisos, solicita los permisos de Health Connect
            requestPermissionLauncher.launch(PERMISSIONS.toTypedArray())
        }
    }

    private fun proceedWithHealthConnect() {
        // Aquí va el código que realiza la operación después de que los permisos se han concedido
    }
}

val PERMISSIONS = setOf(
    HealthPermission.getReadPermission(StepsRecord::class),
)
