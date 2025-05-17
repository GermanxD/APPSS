package app.cui.ro.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.coroutines.Job

@Composable
fun SettingsScreen(onMenuClick: () -> Job) {
    Column {
        Text(text = "Settings Screen")
    }
}
