package app.mamma.guard.auth

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthService {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val PREF_NAME = "app_mamma_guard"
    private val KEY_LOGGED_IN = "logged_in"

    fun register(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.localizedMessage)
                }
            }
    }

    fun login(username: String, password: String, callback: (Boolean) -> Unit) {
        resolveEmailFromUsername(username) { email ->
            if (email != null) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        callback(task.isSuccessful)
                    }
            } else {
                callback(false)
            }
        }
    }


    private fun resolveEmailFromUsername(username: String, callback: (String?) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
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

    fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_LOGGED_IN, isLoggedIn).apply()
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false)
    }

    fun logout(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(KEY_LOGGED_IN).apply() // Eliminar el estado de sesión
    }

}
