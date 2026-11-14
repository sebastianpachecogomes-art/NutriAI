package com.example.nutriai

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.nutriai.navigation.NutriApp
import com.example.nutriai.ui.theme.NutriTheme

/** Única Activity: toda la UI es Compose y la navegación la gestiona NavHost. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Barra de estado transparente con iconos claros: las pantallas empiezan con degradado o encabezado verde.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            NutriTheme {
                NutriApp()
            }
        }
    }
}
