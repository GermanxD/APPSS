package app.mamma.guard.auth

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthService {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val PREF_NAME = "app_mamma_guard"
    private val KEY_LOGGED_IN = "logged_in"

    // Obtener usuario actual
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    // Registrar usuario con email y password
    fun register(email: String, password: String, username: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        val userData = hashMapOf(
                            "uid" to it.uid,
                            "email" to email,
                            "username" to username
                        )
                        firestore.collection("users").document(it.uid).set(userData)
                            .addOnSuccessListener {
                                callback(true, null)
                            }
                            .addOnFailureListener { e ->
                                callback(false, e.localizedMessage)
                            }
                    }
                } else {
                    callback(false, task.exception?.localizedMessage)
                }
            }
    }

    // Iniciar sesión con email y password
    fun login(username: String, password: String, context: Context, callback: (Boolean, String?) -> Unit) {
        resolveEmailFromUsername(username) { email ->
            if (email != null) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        callback(task.isSuccessful, task.exception?.localizedMessage)
                        if (task.isSuccessful) {
                            saveLoginState(context, true) // Guardar sesión si el login es exitoso
                        }
                    }
            } else {
                callback(false, "Usuario no encontrado")
            }
        }
    }


    // Iniciar sesión con username y password
    fun loginWithUsername(username: String, password: String, context: Context, callback: (Boolean, String?) -> Unit) {
        resolveEmailFromUsername(username) { email ->
            if (email != null) {
                login(email, password, context, callback)
            } else {
                callback(false, "Usuario no encontrado")
            }
        }
    }

    // Buscar email en Firestore con el username
    private fun resolveEmailFromUsername(username: String, callback: (String?) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    callback(null)
                } else {
                    val email = querySnapshot.documents.first().getString("email")
                    callback(email)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    // Guardar estado de sesión en SharedPreferences
    fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_LOGGED_IN, isLoggedIn).apply()
    }

    // Verificar si el usuario está logueado
    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false) && firebaseAuth.currentUser != null
    }

    // Cerrar sesión
    fun logout(context: Context) {
        firebaseAuth.signOut()
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(KEY_LOGGED_IN).apply()
    }
}
