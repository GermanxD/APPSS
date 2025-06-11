package app.cui.ro.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Suscribirse a 'daily'
        FirebaseMessaging.getInstance().subscribeToTopic("daily").addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "Suscripción a topic 'daily' exitosa")
            } else {
                Log.e("FCM", "Error al suscribirse a 'daily'", task.exception)
            }
        }

        FirebaseApp.initializeApp(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            App(context = this)
        }
    }
}