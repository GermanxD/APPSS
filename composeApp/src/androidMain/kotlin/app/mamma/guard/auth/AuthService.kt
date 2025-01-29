package app.mamma.guard.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthService {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val PREF_NAME = "app_mamma_guard"
    private val KEY_LOGGED_IN = "logged_in"
    private val TAG = "AuthService" // Etiqueta para los logs

    // Obtener usuario actual
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    // Registrar usuario con email y password
    fun register(email: String, password: String, username: String, callback: (Boolean, String?) -> Unit) {
        Log.d(TAG, "Registrando usuario con email: $email y username: $username")
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Registro exitoso para el usuario: $email")
                    val user = firebaseAuth.currentUser
                    user?.let {
                        val userData = hashMapOf(
                            "uid" to it.uid,
                            "email" to email,
                            "username" to username
                        )
                        firestore.collection("users").document(it.uid).set(userData)
                            .addOnSuccessListener {
                                Log.d(TAG, "Datos del usuario guardados en Firestore")
                                callback(true, null)
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error al guardar datos del usuario en Firestore: ${e.localizedMessage}")
                                callback(false, e.localizedMessage)
                            }
                    }
                } else {
                    Log.e(TAG, "Error en el registro: ${task.exception?.localizedMessage}")
                    callback(false, task.exception?.localizedMessage)
                }
            }
    }

    // Iniciar sesión con email y password
    fun login(email: String, password: String, context: Context, callback: (Boolean, String?) -> Unit) {
        Log.d(TAG, "Intentando iniciar sesión con email: $email")
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Inicio de sesión exitoso para el usuario: $email")
                    saveLoginState(context, true) // Guardar sesión si el login es exitoso
                    callback(true, null)
                } else {
                    Log.e(TAG, "Error en el inicio de sesión: ${task.exception?.localizedMessage}")
                    callback(false, task.exception?.localizedMessage)
                }
            }
    }

    // Iniciar sesión con username y password
    fun loginWithUsername(username: String, password: String, context: Context, callback: (Boolean, String?) -> Unit) {
        Log.d(TAG, "Intentando iniciar sesión con username: $username")
        resolveEmailFromUsername(username) { email ->
            if (email != null) {
                Log.d(TAG, "Email encontrado para el username $username: $email")
                login(email, password, context, callback)
            } else {
                Log.e(TAG, "No se encontró el email para el username: $username")
                callback(false, "Usuario no encontrado")
            }
        }
    }

    // Buscar email en Firestore con el username
    private fun resolveEmailFromUsername(username: String, callback: (String?) -> Unit) {
        Log.d(TAG, "Buscando email para el username: $username")
        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.e(TAG, "No se encontró ningún usuario con el username: $username")
                    callback(null)
                } else {
                    // Verificar si hay documentos en la consulta
                    val document = querySnapshot.documents.firstOrNull()
                    if (document != null) {
                        val email = document.getString("email")
                        if (email != null) {
                            Log.d(TAG, "Email encontrado: $email")
                            callback(email)
                        } else {
                            Log.e(TAG, "El campo 'email' no está presente en el documento para el username: $username")
                            callback(null)
                        }
                    } else {
                        Log.e(TAG, "El documento está vacío para el username: $username")
                        callback(null)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al buscar el email en Firestore: ${exception.localizedMessage}")
                callback(null)
            }
    }

    // Guardar estado de sesión en SharedPreferences
    fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        Log.d(TAG, "Guardando estado de sesión: $isLoggedIn")
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_LOGGED_IN, isLoggedIn).apply()
    }

    // Verificar si el usuario está logueado
    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean(KEY_LOGGED_IN, false) && firebaseAuth.currentUser != null
        Log.d(TAG, "Usuario logueado: $isLoggedIn")
        return isLoggedIn
    }

    // Cerrar sesión
    fun logout(context: Context) {
        Log.d(TAG, "Cerrando sesión")
        firebaseAuth.signOut()
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(KEY_LOGGED_IN).apply()
    }
}