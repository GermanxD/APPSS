package app.mamma.guard.auth

import com.google.firebase.auth.FirebaseAuth

class AuthService {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

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

    fun login(password: String, username: String, callback: (Any?) -> Unit): Boolean {
        firebaseAuth.signInWithEmailAndPassword(password, username)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }

        return true
    }
}
