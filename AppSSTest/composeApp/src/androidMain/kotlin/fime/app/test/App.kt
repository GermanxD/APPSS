import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fime.app.test.ui.RegisterScreen
import fime.app.test.ui.Screen

@Composable
fun App() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) },
                onRegisterClicked = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Screen.Home.route) },
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    Text("Welcome to the app!")
}
