package app.cui.ro.sharedpreferences

import android.content.Context

class PreferencesManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun savePushEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("push_enabled", enabled).apply()
    }

    fun isPushEnabled(): Boolean {
        return sharedPreferences.getBoolean("push_enabled", true)
    }
}

