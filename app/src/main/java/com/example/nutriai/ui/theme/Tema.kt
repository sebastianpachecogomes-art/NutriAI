package com.example.nutriai.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Paleta y medidas tomadas de los layouts y drawables XML originales de NutriAI:
 *  - login_backg.xml            -> degradado #1B3D30 / #4F9978 / #285443
 *  - colors.xml (_3884f1)       -> #488B6D (botón de login)
 *  - dashboard_layout.xml       -> encabezado y selección #43A047, textos #333333 / #9E9E9E
 *  - search_bg.xml              -> campos y tarjetas #F2F2F2 con esquinas de 12dp
 *  - outline_pill_button.xml    -> botón de contorno verde, esquinas de 20dp
 */
object ColoresNutri {
    val Verde = Color(0xFF43A047)
    val VerdeBoton = Color(0xFF488B6D)
    val VerdeClaro = Color(0xFFE8F5E9)
    val Gris = Color(0xFFF2F2F2)
    val TextoOscuro = Color(0xFF333333)
    val TextoMedio = Color(0xFF666666)
    val TextoClaro = Color(0xFF9E9E9E)
    val DegradeInicio = Color(0xFF1B3D30)
    val DegradeCentro = Color(0xFF4F9978)
    val DegradeFin = Color(0xFF285443)
    val ErrorSobreOscuro = Color(0xFFFFCDD2)
}

// La app original tiene todas sus pantallas en claro (colores fijos), por eso solo hay esquema claro.
private val EsquemaNutri = lightColorScheme(
    primary = ColoresNutri.Verde,
    onPrimary = Color.White,
    primaryContainer = ColoresNutri.VerdeClaro,
    onPrimaryContainer = ColoresNutri.TextoOscuro,
    secondary = ColoresNutri.VerdeBoton,
    background = Color.White,
    onBackground = ColoresNutri.TextoOscuro,
    surface = Color.White,
    onSurface = ColoresNutri.TextoOscuro,
    surfaceVariant = ColoresNutri.Gris,
    onSurfaceVariant = ColoresNutri.TextoMedio,
    surfaceContainerHighest = ColoresNutri.Gris,
    outline = ColoresNutri.TextoClaro
)

private val FormasNutri = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun NutriTheme(contenido: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsquemaNutri, shapes = FormasNutri, content = contenido)
}
