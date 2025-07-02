package app.cui.ro.models

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.cui.ro.R
import app.cui.ro.db.AppDatabase
import app.cui.ro.db.Notification
import app.cui.ro.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Actualizar el FirebaseMessagingService
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val job = CoroutineScope(Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            val notificationType = remoteMessage.data["notification_type"]
            val timestampString = remoteMessage.data["timestamp"]
            val timestamp = timestampString?.toLongOrNull() ?: System.currentTimeMillis()

            // Guardar en base de datos Room
            if (title != null && body != null) {
                val notificationDao = AppDatabase.getDatabase(applicationContext).notificationDao()
                val notification = Notification(
                    title = title,
                    body = body,
                    timestamp = timestamp,
                    isRead = false
                )
                job.launch {
                    notificationDao.insertNotification(notification)
                    Log.d(TAG, "Notification saved to DB: $title")
                }

                // Muestra la notificación al usuario
                sendNotification(title, body)
            } else {
                Log.w(TAG, "Title or body is null in data payload")
            }
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    /**
     * Crea y muestra una notificación simple.
     */
    private fun sendNotification(title: String?, messageBody: String?) {
        if (title.isNullOrBlank() || messageBody.isNullOrBlank()) {
            Log.e(TAG, "Cannot send notification, title or messageBody is null/blank")
            return
        }

        // Actividad a abrir al tocar
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones Generales",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
        Log.d(TAG, "Notification sent: $title")
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationDao = AppDatabase.getDatabase(application).notificationDao()

    val unreadNotificationCount: StateFlow<Int> = notificationDao.getUnreadNotificationCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
}




