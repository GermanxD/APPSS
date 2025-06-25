package app.cui.ro.models

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.cui.ro.R
import app.cui.ro.main.MainActivity
import app.cui.ro.ui.screens.NotificationType
import com.google.common.reflect.TypeToken
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Random
import java.util.UUID

object NotificationStorage {
    private const val PREF_NAME = "notifications_storage"
    private const val KEY_NOTIFICATIONS = "stored_notifications"

    private val gson = Gson()

    fun saveNotification(context: Context, notification: StoredNotification) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val currentNotifications = getNotifications(context).toMutableList()

        // Agregar la nueva notificación al inicio
        currentNotifications.add(0, notification)

        // Mantener solo las últimas 50 notificaciones
        if (currentNotifications.size > 50) {
            currentNotifications.removeAt(currentNotifications.size - 1)
        }

        val notificationsJson = gson.toJson(currentNotifications)
        prefs.edit().putString(KEY_NOTIFICATIONS, notificationsJson).apply()
    }

    fun getNotifications(context: Context): List<StoredNotification> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val notificationsJson = prefs.getString(KEY_NOTIFICATIONS, "[]")

        return try {
            val type = object : TypeToken<List<StoredNotification>>() {}.type
            gson.fromJson(notificationsJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun markAsRead(context: Context, notificationId: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val notifications = getNotifications(context).map { notification ->
            if (notification.id == notificationId) {
                notification.copy(isRead = true)
            } else {
                notification
            }
        }

        val notificationsJson = gson.toJson(notifications)
        prefs.edit().putString(KEY_NOTIFICATIONS, notificationsJson).apply()
    }

    fun markAllAsRead(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val notifications = getNotifications(context).map { it.copy(isRead = true) }

        val notificationsJson = gson.toJson(notifications)
        prefs.edit().putString(KEY_NOTIFICATIONS, notificationsJson).apply()
    }

    fun deleteNotification(context: Context, notificationId: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val notifications = getNotifications(context).filter { it.id != notificationId }

        val notificationsJson = gson.toJson(notifications)
        prefs.edit().putString(KEY_NOTIFICATIONS, notificationsJson).apply()
    }

    fun clearAllNotifications(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_NOTIFICATIONS).apply()
    }

    fun getUnreadCount(context: Context): Int {
        return getNotifications(context).count { !it.isRead }
    }
}

// Data class para notificaciones almacenadas
data class StoredNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long,
    val isRead: Boolean = false,
    val data: Map<String, String> = emptyMap() // Para datos adicionales de FCM
)

// Actualizar el FirebaseMessagingService
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: "Cuiro"
        val message = remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: "Tienes una nueva notificación"

        // Crear notificación almacenada
        val storedNotification = StoredNotification(
            id = UUID.randomUUID().toString(),
            title = title,
            message = message,
            type = determineNotificationType(remoteMessage.data),
            timestamp = System.currentTimeMillis(),
            isRead = false,
            data = remoteMessage.data
        )

        // Guardar la notificación
        NotificationStorage.saveNotification(this, storedNotification)

        // Incrementar contador
        NotificationCounter.increment(this)

        // Mostrar notificación en el sistema
        showNotification(title, message)
    }

    private fun determineNotificationType(data: Map<String, String>): NotificationType {
        return when (data["type"]?.lowercase()) {
            "success" -> NotificationType.SUCCESS
            "warning" -> NotificationType.WARNING
            "error" -> NotificationType.ERROR
            "message" -> NotificationType.MESSAGE
            else -> NotificationType.INFO
        }
    }

    private fun showNotification(title: String?, message: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "default_channel_id"

        // Crear canal si no existe (obligatorio en Android O+)
        val channel = NotificationChannel(
            channelId,
            "Notificaciones Cuiro",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        // Intent para abrir la app cuando se toque la notificación
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_notifications", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title ?: "Cuiro")
            .setContentText(message ?: "")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Random().nextInt(), notification)
    }
}

// Actualizar el ViewModel
class VMNotifications(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLek", "StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _notifications = MutableStateFlow<List<StoredNotification>>(emptyList())
    val notifications: StateFlow<List<StoredNotification>> = _notifications

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val notificationCount: StateFlow<Int> = NotificationCounter.count

    init {
        NotificationCounter.initialize(context)
        loadNotifications()
    }

    fun loadNotifications() {
        _isLoading.value = true
        // Simular carga asíncrona
        viewModelScope.launch {
            delay(500) // Simular delay de carga
            val storedNotifications = NotificationStorage.getNotifications(context)
            _notifications.value = storedNotifications

            // Sincronizar contador con notificaciones no leídas reales
            val unreadCount = storedNotifications.count { !it.isRead }
            if (unreadCount != NotificationCounter.count.value) {
                // Actualizar contador si no coincide
                NotificationCounter.setCount(context, unreadCount)
            }

            _isLoading.value = false
        }
    }

    fun markAsRead(notificationId: String) {
        NotificationStorage.markAsRead(context, notificationId)
        loadNotifications()
        NotificationCounter.decrement(context)
    }

    fun markAllAsRead() {
        NotificationStorage.markAllAsRead(context)
        loadNotifications()
        NotificationCounter.clear(context)
    }

    fun deleteNotification(notificationId: String) {
        val notification = _notifications.value.find { it.id == notificationId }
        NotificationStorage.deleteNotification(context, notificationId)
        loadNotifications()

        // Solo decrementar si la notificación no estaba leída
        if (notification?.isRead == false) {
            NotificationCounter.decrement(context)
        }
    }

    fun clearAllNotifications() {
        NotificationStorage.clearAllNotifications(context)
        loadNotifications()
        NotificationCounter.clear(context)
    }
}

// Actualizar NotificationCounter con función setCount
object NotificationCounter {
    private const val PREF_NAME = "notification_prefs"
    private const val KEY_COUNT = "notification_count"

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count

    private var isInitialized = false

    fun initialize(context: Context) {
        if (!isInitialized) {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            _count.value = prefs.getInt(KEY_COUNT, 0)
            isInitialized = true
        }
    }

    private fun save(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_COUNT, _count.value).apply()
    }

    fun increment(context: Context) {
        _count.value += 1
        save(context)
    }

    fun clear(context: Context) {
        _count.value = 0
        save(context)
    }

    fun decrement(context: Context) {
        if (_count.value > 0) {
            _count.value -= 1
            save(context)
        }
    }

    // Nueva función para establecer un valor específico
    fun setCount(context: Context, count: Int) {
        _count.value = maxOf(0, count)
        save(context)
    }
}




