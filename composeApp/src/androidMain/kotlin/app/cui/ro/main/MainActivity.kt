package app.cui.ro.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import app.cui.ro.models.NotificationViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    // Launcher para solicitar el permiso de notificaciones
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("Notifications", "Permiso de notificaciones concedido")
            setupFirebaseMessaging()
        } else {
            Log.w("Notifications", "Permiso de notificaciones denegado")
            // Aún configuramos FCM, pero informamos al usuario
            setupFirebaseMessaging()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase primero
        FirebaseApp.initializeApp(this)

        // Configurar la ventana
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Solicitar permiso de notificaciones automáticamente
        requestNotificationPermissionOnStart()

        setContent {
            App(context = this)
        }
    }

    private fun requestNotificationPermissionOnStart() {
        // Solo solicitar en Android 13 (API 33) y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                // Ya tiene el permiso
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("Notifications", "Permiso ya concedido")
                    setupFirebaseMessaging()
                }
                // Solicitar el permiso
                else -> {
                    Log.d("Notifications", "Solicitando permiso de notificaciones")
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Para versiones anteriores a Android 13, las notificaciones están habilitadas por defecto
            Log.d("Notifications", "Android < 13, notificaciones habilitadas por defecto")
            setupFirebaseMessaging()
        }
    }

    private fun setupFirebaseMessaging() {
        val notifications = NotificationViewModel(application)
        Log.d("FCM", "Configurando Firebase Cloud Messaging...")

        // Obtener token FCM
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Error al obtener token FCM", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM", "Token FCM obtenido: $token")

        }

        // Suscribirse a topic 'daily'
        notifications.subscribeToTopic()
    }
}