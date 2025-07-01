package app.cui.ro.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.cui.ro.db.AppDatabase
import app.cui.ro.db.Notification
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

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        if (title != null && body != null) {
            val notificationDao = AppDatabase.getDatabase(applicationContext).notificationDao()
            val notification = Notification(
                title = title,
                body = body,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
            job.launch {
                notificationDao.insertNotification(notification)
            }
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
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




