package ir.polino.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Emerald = Color(0xFF0B6B57)
private val DeepGreen = Color(0xFF083F35)
private val Gold = Color(0xFFE3B04B)
private val Cream = Color(0xFFF7F4EA)

private val LightColors = lightColorScheme(
    primary = Emerald,
    secondary = Gold,
    background = Cream,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = DeepGreen,
    onSurface = DeepGreen
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF62D6B7),
    secondary = Gold,
    background = Color(0xFF081B17),
    surface = Color(0xFF0D2B24)
)

@Composable
fun PolinoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}
