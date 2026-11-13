package com.example.nutriai.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Campo de texto con etiqueta y mensaje de error de validación. */
@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
    esContrasena: Boolean = false,
    lineas: Int = 1,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = if (error != null) {
            { Text(error) }
        } else null,
        singleLine = lineas == 1,
        minLines = lineas,
        visualTransformation = if (esContrasena) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = teclado, imeAction = imeAction),
        modifier = modifier.fillMaxWidth()
    )
}

/** Botón principal con indicador de carga. */
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    cargando: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = habilitado && !cargando,
        modifier = modifier.fillMaxWidth().height(48.dp)
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = LocalContentColor.current
            )
        } else {
            Text(texto)
        }
    }
}

/** Barra superior con botón opcional de regreso. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(titulo: String, onAtras: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            if (onAtras != null) {
                IconButton(onClick = onAtras) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        }
    )
}

/** Tarjeta de lista con título, subtítulo y una etiqueta opcional. */
@Composable
fun TarjetaItem(
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    etiqueta: String? = null,
    onClick: (() -> Unit)? = null
) {
    val contenido: @Composable () -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(subtitulo, style = MaterialTheme.typography.bodyMedium)
            }
            if (etiqueta != null) {
                Text(etiqueta, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
    if (onClick != null) {
        Card(modifier = modifier.fillMaxWidth().clickable(onClick = onClick)) { contenido() }
    } else {
        Card(modifier = modifier.fillMaxWidth()) { contenido() }
    }
}

/** Selector 1..5 (energía, ánimo). */
@Composable
fun SelectorEscala(
    etiqueta: String,
    valor: Int,
    onCambio: (Int) -> Unit,
    extremoBajo: String,
    extremoAlto: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(etiqueta, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
            for (i in 1..5) {
                FilterChip(selected = valor == i, onClick = { onCambio(i) }, label = { Text("$i") })
            }
        }
        Text("1 = $extremoBajo · 5 = $extremoAlto", style = MaterialTheme.typography.bodySmall)
    }
}

/** Aviso médico-legal obligatorio (RNF03). */
@Composable
fun AvisoLegal(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text(
                "Información orientativa. No sustituye la consulta con un médico, pediatra, " +
                    "nutricionista o psicólogo.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PantallaCarga(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun MensajeCentrado(mensaje: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(mensaje, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
    }
}

/** Gráfica de línea simple (valores en orden cronológico). */
@Composable
fun GraficaLinea(valores: List<Double>, modifier: Modifier = Modifier) {
    if (valores.size < 2) {
        Text("Se necesitan al menos 2 registros para mostrar la gráfica.", style = MaterialTheme.typography.bodyMedium)
        return
    }
    val colorLinea = MaterialTheme.colorScheme.primary
    val colorEje = MaterialTheme.colorScheme.outline
    val minimo = valores.min()
    val maximo = valores.max()
    val rango = if (maximo - minimo > 0.0) maximo - minimo else 1.0

    Canvas(modifier = modifier.fillMaxWidth().height(160.dp)) {
        val margen = 12.dp.toPx()
        val ancho = size.width - 2 * margen
        val alto = size.height - 2 * margen
        drawLine(colorEje, Offset(margen, size.height - margen), Offset(size.width - margen, size.height - margen), 2f)
        val puntos = valores.mapIndexed { i, v ->
            Offset(
                margen + ancho * i / (valores.size - 1),
                margen + alto * (1f - ((v - minimo) / rango).toFloat())
            )
        }
        for (i in 0 until puntos.size - 1) {
            drawLine(colorLinea, puntos[i], puntos[i + 1], strokeWidth = 4f)
        }
        puntos.forEach { drawCircle(colorLinea, 6f, it) }
    }
}
