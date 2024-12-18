package fime.app.test.auth

class AuthService {
    fun login(username: String, password: String): Boolean {
        return username == "user" && password == "password" // Lógica de ejemplo
    }
}