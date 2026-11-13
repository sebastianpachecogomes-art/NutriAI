package com.example.nutriai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta tomada del escudo institucional (verde y amarillo).
private val Verde = Color(0xFF004A2B)
private val Amarillo = Color(0xFFFBE122)

private val EsquemaClaro = lightColorScheme(
    primary = Verde,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB7F0CF),
    onPrimaryContainer = Color(0xFF002111),
    secondary = Color(0xFF6B5F00),
    secondaryContainer = Amarillo,
    onSecondaryContainer = Color(0xFF211B00)
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFF7FDBA7),
    onPrimary = Color(0xFF00391F),
    primaryContainer = Verde,
    onPrimaryContainer = Color(0xFFB7F0CF),
    secondary = Amarillo,
    onSecondary = Color(0xFF3B3200),
    secondaryContainer = Color(0xFF524600),
    onSecondaryContainer = Color(0xFFFFF2A8)
)

@Composable
fun NutriTheme(oscuro: Boolean = isSystemInDarkTheme(), contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (oscuro) EsquemaOscuro else EsquemaClaro,
        content = contenido
    )
}
