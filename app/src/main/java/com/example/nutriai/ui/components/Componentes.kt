package com.example.nutriai.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriai.R
import com.example.nutriai.ui.theme.ColoresNutri

// ───────────────────────── Pantallas de acceso (fondo degradado) ─────────────────────────

/** Fondo de login_backg.xml: degradado vertical verde oscuro / verde medio / verde. */
@Composable
fun FondoDegradado(modifier: Modifier = Modifier, contenido: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(ColoresNutri.DegradeFin, ColoresNutri.DegradeCentro, ColoresNutri.DegradeInicio)
                )
            ),
        content = contenido
    )
}

/** Logo (160dp) y nombre de la app (40sp, blanco) de activity_main.xml. */
@Composable
fun LogoYNombre(nombre: String) {
    Image(
        painter = painterResource(R.drawable.logo_nutriai),
        contentDescription = nombre,
        modifier = Modifier.size(160.dp)
    )
    Text(nombre, color = Color.White, fontSize = 40.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
}

/** Campo con contorno blanco y esquinas de 24dp (rounded_corner.xml). */
@Composable
fun CampoAuth(
    valor: String,
    onCambio: (String) -> Unit,
    pista: String,
    modifier: Modifier = Modifier,
    esContrasena: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    error: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            placeholder = { Text(pista, fontSize = 20.sp) },
            textStyle = TextStyle(fontSize = 20.sp),
            singleLine = true,
            isError = error != null,
            shape = RoundedCornerShape(24.dp),
            visualTransformation = if (esContrasena) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                errorTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                errorBorderColor = ColoresNutri.ErrorSobreOscuro,
                cursorColor = Color.White,
                errorCursorColor = Color.White,
                focusedPlaceholderColor = Color.White,
                unfocusedPlaceholderColor = Color.White,
                errorPlaceholderColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                error,
                color = ColoresNutri.ErrorSobreOscuro,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 18.dp, top = 4.dp)
            )
        }
    }
}

/** Botón de acceso (LOGIN / CREAR CUENTA): verde #488B6D, texto blanco de 20sp, con sombra. */
@Composable
fun BotonAuth(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, cargando: Boolean = false) {
    Button(
        onClick = onClick,
        enabled = !cargando,
        colors = ButtonDefaults.buttonColors(
            containerColor = ColoresNutri.VerdeBoton,
            contentColor = Color.White,
            disabledContainerColor = ColoresNutri.VerdeBoton.copy(alpha = 0.6f),
            disabledContentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        modifier = modifier.fillMaxWidth().height(60.dp)
    ) {
        if (cargando) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = Color.White)
        } else {
            Text(texto, fontSize = 20.sp)
        }
    }
}

/** Botón transparente con texto blanco en negrita ("Crear una cuenta"). */
@Composable
fun TextoEnlace(texto: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(texto, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// ───────────────────────── Pantallas internas (fondo blanco) ─────────────────────────

/** Encabezado verde #43A047 con título centrado en blanco (dashboard_layout.xml). */
@Composable
fun EncabezadoVerde(titulo: String, onAtras: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColoresNutri.Verde)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Text(
            titulo,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 56.dp, vertical = 20.dp)
        )
        if (onAtras != null) {
            IconButton(onClick = onAtras, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
        }
    }
}

/** Barra superior de todas las pantallas internas: el encabezado verde de NutriAI. */
@Composable
fun BarraSuperior(titulo: String, onAtras: (() -> Unit)? = null) = EncabezadoVerde(titulo, onAtras)

data class ItemNav(val ruta: String, val emoji: String, val etiqueta: String)

/** Barra de navegación inferior de dashboard_layout.xml: emoji de 20sp y etiqueta de 11sp. */
@Composable
fun BarraInferiorNutri(items: List<ItemNav>, seleccionada: String, onClick: (String) -> Unit) {
    Surface(color = Color.White, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(vertical = 8.dp)
        ) {
            items.forEach { item ->
                val activo = item.ruta == seleccionada
                Column(
                    modifier = Modifier.weight(1f).clickable { onClick(item.ruta) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(item.emoji, fontSize = 20.sp)
                    Text(
                        item.etiqueta,
                        fontSize = 11.sp,
                        color = if (activo) ColoresNutri.Verde else ColoresNutri.TextoClaro,
                        fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

/** Campo de formulario: etiqueta en negrita de 14sp y campo gris #F2F2F2 de 12dp (agregar_nino_layout.xml). */
@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    pista: String? = null,
    error: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
    esContrasena: Boolean = false,
    lineas: Int = 1,
    imeAction: ImeAction = ImeAction.Next
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(etiqueta, color = ColoresNutri.TextoOscuro, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            placeholder = if (pista != null) { { Text(pista) } } else null,
            isError = error != null,
            supportingText = if (error != null) {
                { Text(error) }
            } else null,
            singleLine = lineas == 1,
            minLines = lineas,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (esContrasena) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = teclado, imeAction = imeAction),
            colors = coloresCampoGris(),
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
        )
    }
}

/** Campo de búsqueda gris sin etiqueta (search_bg.xml). */
@Composable
fun CampoBusqueda(
    valor: String,
    onCambio: (String) -> Unit,
    pista: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Search
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        placeholder = { Text(pista) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        colors = coloresCampoGris(),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun coloresCampoGris() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = ColoresNutri.Gris,
    unfocusedContainerColor = ColoresNutri.Gris,
    errorContainerColor = ColoresNutri.Gris,
    focusedBorderColor = ColoresNutri.Verde,
    unfocusedBorderColor = Color.Transparent,
    errorBorderColor = MaterialTheme.colorScheme.error,
    focusedTextColor = ColoresNutri.TextoOscuro,
    unfocusedTextColor = ColoresNutri.TextoOscuro,
    cursorColor = ColoresNutri.Verde,
    focusedPlaceholderColor = ColoresNutri.TextoClaro,
    unfocusedPlaceholderColor = ColoresNutri.TextoClaro
)

/** Botón principal verde #43A047 con texto blanco de 16sp (botón "Registrar niño"). */
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
        colors = ButtonDefaults.buttonColors(containerColor = ColoresNutri.Verde, contentColor = Color.White),
        modifier = modifier.fillMaxWidth().height(56.dp)
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = LocalContentColor.current
            )
        } else {
            Text(texto, fontSize = 16.sp)
        }
    }
}

/** Botón de contorno verde de 1.5dp y esquinas de 20dp (outline_pill_button.xml). */
@Composable
fun BotonPildora(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, ColoresNutri.Verde),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = ColoresNutri.Verde),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = modifier
    ) {
        Text(texto, fontSize = 13.sp)
    }
}

/** Círculo verde con foto o silueta blanca (circle_mask.xml + ic_person_placeholder.xml). */
@Composable
fun AvatarPerfil(tamano: Dp, modifier: Modifier = Modifier, fotoPath: String? = null) {
    val bitmap = remember(fotoPath) {
        if (!fotoPath.isNullOrBlank()) {
            try {
                BitmapFactory.decodeFile(fotoPath)?.asImageBitmap()
            } catch (_: Exception) {
                null
            }
        } else null
    }

    Box(
        modifier = modifier.size(tamano).clip(CircleShape).background(ColoresNutri.Verde),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(tamano * 0.6f))
        }
    }
}

/** Fila de lista de item_nino.xml: avatar de 48dp y nombre en negrita de 18sp. */
@Composable
fun FilaPerfil(
    nombre: String,
    detalle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fotoPath: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarPerfil(48.dp, fotoPath = fotoPath)
        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
            Text(nombre, color = ColoresNutri.TextoOscuro, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(detalle, color = ColoresNutri.TextoClaro, fontSize = 13.sp)
        }
    }
}

/** Botón de tarjeta con borde verde fino y fondo blanco, estilo icono/cuadrado (Detalles del niño). */
@Composable
fun BotonAccionCuadrado(
    emoji: String,
    etiqueta: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, ColoresNutri.Verde.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 22.sp)
        Text(
            etiqueta,
            color = ColoresNutri.TextoOscuro,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1
        )
    }
}

/** Acceso rápido: mosaico gris con emoji de 24sp y etiqueta de 11sp. */
@Composable
fun TileAcceso(emoji: String, texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ColoresNutri.Gris)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 24.sp)
        Text(texto, color = ColoresNutri.TextoOscuro, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

/** Tarjeta de lista gris con título, subtítulo y etiqueta opcional. */
@Composable
fun TarjetaItem(
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    etiqueta: String? = null,
    onClick: (() -> Unit)? = null
) {
    val base = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(ColoresNutri.Gris)
    Row(
        modifier = (if (onClick != null) base.clickable(onClick = onClick) else base).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, color = ColoresNutri.TextoOscuro, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(subtitulo, color = ColoresNutri.TextoMedio, fontSize = 13.sp)
        }
        if (etiqueta != null) {
            Text(etiqueta, color = ColoresNutri.Verde, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun coloresChip() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = ColoresNutri.Verde,
    selectedLabelColor = Color.White
)

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
        Text(etiqueta, color = ColoresNutri.TextoOscuro, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 6.dp)) {
            for (i in 1..5) {
                FilterChip(selected = valor == i, onClick = { onCambio(i) }, label = { Text("$i") }, colors = coloresChip())
            }
        }
        Text("1 = $extremoBajo · 5 = $extremoAlto", color = ColoresNutri.TextoClaro, fontSize = 12.sp)
    }
}

/** Aviso médico-legal obligatorio (RNF03), en verde claro. */
@Composable
fun AvisoLegal(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ColoresNutri.VerdeClaro)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Info, contentDescription = null, tint = ColoresNutri.Verde, modifier = Modifier.padding(end = 8.dp))
        Text(
            "Información orientativa. No sustituye la consulta con un médico, pediatra, " +
                "nutricionista o psicólogo.",
            color = ColoresNutri.TextoOscuro,
            fontSize = 12.sp
        )
    }
}

@Composable
fun PantallaCarga(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = ColoresNutri.Verde)
    }
}

@Composable
fun MensajeCentrado(mensaje: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(mensaje, textAlign = TextAlign.Center, color = ColoresNutri.TextoMedio, fontSize = 15.sp)
    }
}

/** Gráfica de línea simple en verde (valores en orden cronológico). */
@Composable
fun GraficaLinea(valores: List<Double>, modifier: Modifier = Modifier) {
    if (valores.size < 2) {
        Text(
            "Se necesitan al menos 2 registros para mostrar la gráfica.",
            color = ColoresNutri.TextoMedio,
            fontSize = 14.sp
        )
        return
    }
    val colorLinea = ColoresNutri.Verde
    val colorEje = ColoresNutri.TextoClaro
    val minimo = valores.min()
    val maximo = valores.max()
    val diferencia = maximo - minimo

    Canvas(modifier = modifier.fillMaxWidth().height(160.dp)) {
        val margen = 12.dp.toPx()
        val ancho = size.width - 2 * margen
        val alto = size.height - 2 * margen
        drawLine(colorEje, Offset(margen, size.height - margen), Offset(size.width - margen, size.height - margen), 2f)
        val puntos = valores.mapIndexed { i, v ->
            val fraccion = if (diferencia > 0.0) ((v - minimo) / diferencia).toFloat() else 0.5f
            Offset(
                margen + ancho * i / (valores.size - 1),
                margen + alto * (1f - fraccion)
            )
        }
        for (i in 0 until puntos.size - 1) {
            drawLine(colorLinea, puntos[i], puntos[i + 1], strokeWidth = 4f)
        }
        puntos.forEach { drawCircle(colorLinea, 6f, it) }
    }
}

@Composable
fun EspacioVertical(alto: Dp) = Spacer(Modifier.height(alto))

/** Título de sección en negrita (20sp, #333333) como "Mis niños". */
@Composable
fun TituloSeccion(texto: String, modifier: Modifier = Modifier, tamano: Int = 20) {
    Text(texto, color = ColoresNutri.TextoOscuro, fontSize = tamano.sp, fontWeight = FontWeight.Bold, modifier = modifier)
}
