package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BookBluePrimaryDark,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = BookBlueContainerDark,
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = BookGiltGoldDark,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = Color(0xFFFEF3C7),
    background = MidnightDarkBg,
    onBackground = TextLightPrimary,
    surface = MidnightDarkSurface,
    onSurface = TextLightPrimary,
    surfaceVariant = MidnightDarkSurfaceVariant,
    onSurfaceVariant = TextLightSecondary,
    outline = Color(0xFF24324D),
    outlineVariant = Color(0xFF162035),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BookBluePrimary,
    onPrimary = Color.White,
    primaryContainer = BookBlueContainerLight,
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = BookGiltGold,
    onSecondary = Color.White,
    secondaryContainer = BookGiltGoldContainer,
    onSecondaryContainer = Color(0xFF78350F),
    background = PaperLightBg,
    onBackground = TextDarkPrimary,
    surface = PaperLightSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = PaperLightSurfaceVariant,
    onSurfaceVariant = TextDarkSecondary,
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use literary palette by default for rich writing vibe
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

