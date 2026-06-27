package com.codebythura.fruit2048.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = darkPrimaryColor,
    primaryContainer = darkPrimaryContainer,
    secondaryContainer = darkSecondaryContainer
)

private val LightColorScheme = lightColorScheme(
    primary = lightPrimaryColor,
    primaryContainer = lightPrimaryContainer,
    secondaryContainer = lightSecondaryContainer,
)

@Composable
fun Fruit2048Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}