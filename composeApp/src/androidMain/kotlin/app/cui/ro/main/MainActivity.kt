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

        // Obtener token FCM
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCMToken", "Token: $token")
            } else {
                Log.e("FCMToken", "Error al obtener token", task.exception)
            }
        }

        FirebaseApp.initializeApp(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            App(context = this)
        }
    }
}