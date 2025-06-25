package app.cui.ro.models

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VMNotifications(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    val notificationCount: StateFlow<Int> = NotificationCounter.count

    init {
        NotificationCounter.initialize(context)
    }

    fun clearNotifications() {
        NotificationCounter.clear(context)
    }
}

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
}


