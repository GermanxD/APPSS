package app.mamma.guard.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class SessionManager(private val context: Context) {

    companion object {
        private val SESSION_KEY = booleanPreferencesKey("is_logged_in")
    }

    // Guardar sesión iniciada
    suspend fun saveSession(isLoggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SESSION_KEY] = isLoggedIn
        }
    }

    // Obtener estado de sesión
    val isUserLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[SESSION_KEY] ?: false  // Por defecto es false (No ha iniciado sesión)
    }

    // Cerrar sesión
    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs[SESSION_KEY] = false
        }
    }
}