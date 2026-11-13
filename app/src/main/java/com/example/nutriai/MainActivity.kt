package com.example.nutriai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.nutriai.navigation.NutriApp
import com.example.nutriai.ui.theme.NutriTheme

/** Única Activity: toda la UI es Compose y la navegación la gestiona NavHost. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTheme {
                NutriApp()
            }
        }
    }
}
