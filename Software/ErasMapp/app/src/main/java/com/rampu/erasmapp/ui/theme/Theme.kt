package com.rampu.erasmapp.ui.theme

import android.app.Activity
import android.hardware.lights.Light
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    onBackground = DarkText,
    onSurface = DarkText,
    background = DarkBackground,
    surface = DarkBackground,
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkAccent
)

private val LightColorScheme = lightColorScheme(
    onBackground = LightText,
    onSurface = LightText,
    background = LightBackground,
    surface = LightBackground,
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightAccent
)

@Composable
fun ErasMappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if(darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}