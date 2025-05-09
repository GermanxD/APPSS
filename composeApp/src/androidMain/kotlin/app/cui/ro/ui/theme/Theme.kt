package app.cui.ro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = CuiroColors.PrimaryPink,
    secondary = CuiroColors.SecondaryRose,
    background = CuiroColors.SectionsPink,
    surface = CuiroColors.PinkFields,
    onPrimary = CuiroColors.FontBrown,
    onSecondary = CuiroColors.FontBrown,
    onBackground = CuiroColors.FontBrown,
    onSurface = CuiroColors.FontBrown,
)

private val DarkColorScheme = darkColorScheme(
    primary = CuiroColors.SecondaryRose,
    secondary = CuiroColors.PrimaryPink,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun CuiroTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = Color.Black.toArgb()
            window.navigationBarColor = if (useDarkTheme) Color.Black.toArgb() else CuiroColors.PinkFields.toArgb()

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}
