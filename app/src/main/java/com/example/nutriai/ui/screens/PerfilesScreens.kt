package com.example.nutriai.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.io.File
import java.util.UUID
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutriai.data.Perfil
import com.example.nutriai.data.RegistroDiario
import com.example.nutriai.ui.Carga
import com.example.nutriai.ui.Guardado
import com.example.nutriai.ui.components.AvatarPerfil
import com.example.nutriai.ui.components.BarraSuperior
import com.example.nutriai.ui.components.BotonAccionCuadrado
import com.example.nutriai.ui.components.BotonPildora
import com.example.nutriai.ui.components.CampoBusqueda
import com.example.nutriai.ui.components.EncabezadoVerde
import com.example.nutriai.ui.components.FilaPerfil
import com.example.nutriai.ui.components.TileAcceso
import com.example.nutriai.ui.components.TituloSeccion
import com.example.nutriai.ui.components.coloresChip
import com.example.nutriai.ui.theme.ColoresNutri
import com.example.nutriai.ui.components.BotonPrincipal
import com.example.nutriai.ui.components.CampoTexto
import com.example.nutriai.ui.components.GraficaLinea
import com.example.nutriai.ui.components.MensajeCentrado
import com.example.nutriai.ui.components.PantallaCarga
import com.example.nutriai.ui.viewmodel.PerfilesViewModel
import com.example.nutriai.util.Validadores
import com.example.nutriai.util.formatearFecha
import com.example.nutriai.util.formatearFechaCorta
import com.example.nutriai.util.unDecimal

/** CU02 - Lista de perfiles con búsqueda (RF02). Diseño del dashboard_layout.xml de NutriAI. */
@Composable
fun PerfilesScreen(
    usuario: String,
    vm: PerfilesViewModel,
    onAbrirPerfil: (Int) -> Unit,
    onNuevoPerfil: () -> Unit,
    onAbrirRecordatorios: () -> Unit,
    onEvaluacion: (Int) -> Unit,
    barraInferior: @Composable () -> Unit
) {
    val flujo = remember(usuario) { vm.perfiles(usuario) }
    val carga by flujo.collectAsStateWithLifecycle(initialValue = Carga.Cargando)
    var busqueda by remember { mutableStateOf("") }
    var aviso by remember { mutableStateOf<String?>(null) }

    val todos = (carga as? Carga.Listo)?.datos.orEmpty()
    val filtrados = if (busqueda.isBlank()) todos else todos.filter { it.nombre.contains(busqueda, ignoreCase = true) }

    Scaffold(
        topBar = { EncabezadoVerde("Consultar Información") },
        bottomBar = barraInferior,
        containerColor = Color.White
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            CampoBusqueda(busqueda, { busqueda = it }, "Buscar por nombre")
            Text(
                "${filtrados.size} resultados encontrados",
                color = ColoresNutri.TextoClaro,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TituloSeccion("Mis perfiles")
                BotonPildora("+ Agregar perfil", onNuevoPerfil)
            }
            Box(modifier = Modifier.weight(1f).padding(top = 8.dp)) {
                when (val c = carga) {
                    Carga.Cargando -> PantallaCarga()
                    is Carga.Error -> MensajeCentrado("No se pudieron cargar los perfiles: ${c.mensaje}")
                    is Carga.Listo -> if (c.datos.isEmpty()) {
                        MensajeCentrado("Aún no tienes perfiles. Toca \"+ Agregar perfil\" para crear el primero.")
                    } else {
                        LazyColumn {
                            items(filtrados, key = { it.id }) { perfil ->
                                FilaPerfil(
                                    nombre = perfil.nombre,
                                    detalle = "${perfil.edad} años · " + if (perfil.esNino) "Niño/a" else "Personal",
                                    onClick = { onAbrirPerfil(perfil.id) },
                                    fotoPath = perfil.fotoPath
                                )
                            }
                        }
                    }
                }
            }
            TituloSeccion("Accesos rápidos", Modifier.padding(top = 12.dp), tamano = 18)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TileAcceso("🔔", "Recordatorios", onAbrirRecordatorios, Modifier.weight(1f))
                TileAcceso(
                    "📊", "Evaluación",
                    {
                        val primero = todos.firstOrNull()
                        if (primero != null) onEvaluacion(primero.id)
                        else aviso = "Crea un perfil para ver su evaluación."
                    },
                    Modifier.weight(1f)
                )
                TileAcceso(
                    "📈", "Estadísticas",
                    {
                        val primero = todos.firstOrNull()
                        if (primero != null) onEvaluacion(primero.id)
                        else aviso = "Crea un perfil para ver sus estadísticas."
                    },
                    Modifier.weight(1f)
                )
            }
            if (aviso != null) {
                Text(aviso.orEmpty(), color = ColoresNutri.TextoMedio, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}

/** CU02 - Crear perfil. Diseño de agregar_nino_layout.xml de NutriAI. */
@Composable
fun NuevoPerfilScreen(
    usuario: String,
    vm: PerfilesViewModel,
    onAtras: () -> Unit
) {
    val contexto = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var esNino by remember { mutableStateOf(true) }
    var fotoPath by remember { mutableStateOf<String?>(null) }
    var intentado by remember { mutableStateOf(false) }
    val guardado by vm.guardado.collectAsStateWithLifecycle()

    val selectorImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val archivoDestino = File(contexto.filesDir, "nino_${UUID.randomUUID()}.jpg")
                contexto.contentResolver.openInputStream(uri)?.use { entrada ->
                    archivoDestino.outputStream().use { salida ->
                        entrada.copyTo(salida)
                    }
                }
                fotoPath = archivoDestino.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(guardado) {
        if (guardado is Guardado.Exito) {
            vm.reiniciar()
            onAtras()
        }
    }

    Scaffold(topBar = { BarraSuperior("Agregar niño", onAtras) }, containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AvatarPerfil(
                tamano = 90.dp,
                fotoPath = fotoPath,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable {
                        selectorImagen.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
            )
            Text(
                "Toca para elegir una foto",
                color = ColoresNutri.TextoClaro,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 6.dp)
            )

            Text(
                "Hola ${if (usuario.isNotBlank() && usuario != "invitado") usuario else "Usuario"},",
                color = ColoresNutri.TextoOscuro,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp)
            )
            Text("¿Qué información deseas registrar hoy?", color = ColoresNutri.TextoMedio, fontSize = 14.sp)

            CampoTexto(
                nombre, { nombre = it }, "Nombre del niñ@",
                pista = "Nombre",
                modifier = Modifier.padding(top = 24.dp),
                error = if (intentado) Validadores.nombrePerfil(nombre) else null
            )
            CampoTexto(
                edad, { edad = it }, "Edad",
                pista = "Años",
                modifier = Modifier.padding(top = 16.dp),
                teclado = KeyboardType.Number,
                error = if (intentado) Validadores.edad(edad) else null
            )
            CampoTexto(
                peso, { peso = it }, "Peso",
                pista = "Peso",
                modifier = Modifier.padding(top = 16.dp),
                teclado = KeyboardType.Decimal,
                error = if (intentado) Validadores.peso(peso) else null
            )
            CampoTexto(
                altura, { altura = it }, "Altura",
                pista = "Altura",
                modifier = Modifier.padding(top = 16.dp),
                teclado = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
                error = if (intentado) Validadores.talla(altura) else null
            )
            if (guardado is Guardado.Error) {
                Text(
                    (guardado as Guardado.Error).mensaje,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            BotonPrincipal(
                texto = "Registrar niño",
                modifier = Modifier.padding(top = 28.dp),
                cargando = guardado is Guardado.Guardando,
                onClick = {
                    intentado = true
                    vm.crearPerfilNino(
                        usuario = usuario,
                        nombre = nombre,
                        edadStr = edad,
                        pesoStr = peso,
                        alturaStr = altura,
                        fotoPath = fotoPath,
                        esNino = esNino
                    )
                }
            )
        }
    }
}

/** CU04 - Perfil del niño con información básica, acciones y su historial al fondo. */
@Composable
fun PerfilDetalleScreen(
    perfilId: Int,
    vm: PerfilesViewModel,
    onAtras: () -> Unit,
    onNuevoRegistro: () -> Unit,
    onResumen: () -> Unit,
    onAsistente: () -> Unit = {}
) {
    var perfil by remember { mutableStateOf<Perfil?>(null) }
    LaunchedEffect(perfilId) { perfil = vm.perfil(perfilId) }

    val flujo = remember(perfilId) { vm.registros(perfilId) }
    val carga by flujo.collectAsStateWithLifecycle(initialValue = Carga.Cargando)
    var mostrarPeso by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { BarraSuperior("Perfil del niño", onAtras) },
        containerColor = Color.White
    ) { padding ->
        when (val c = carga) {
            Carga.Cargando -> PantallaCarga(Modifier.padding(padding))
            is Carga.Error -> MensajeCentrado("No se pudo cargar el perfil: ${c.mensaje}", Modifier.padding(padding))
            is Carga.Listo -> {
                val registros = c.datos // más reciente primero
                val p = perfil
                val ultimo = registros.firstOrNull()

                // Cálculo de datos básicos
                val pesoActual = ultimo?.peso
                val tallaActualCm = ultimo?.talla
                val imcCalculado = if (pesoActual != null && tallaActualCm != null && tallaActualCm > 0.0) {
                    val tallaM = tallaActualCm / 100.0
                    val imc = pesoActual / (tallaM * tallaM)
                    String.format(java.util.Locale.US, "%.1f", imc)
                } else null

                val fechaNacimientoEstimada = remember(p?.edad) {
                    val edad = p?.edad ?: 0
                    val anio = 2026 - edad
                    "15/06/$anio"
                }

                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Avatar del niño, Nombre y Edad
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            AvatarPerfil(tamano = 96.dp, fotoPath = p?.fotoPath)
                            Text(
                                text = p?.nombre ?: "Niño",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColoresNutri.TextoOscuro,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                            Text(
                                text = "${p?.edad ?: 0} Años",
                                fontSize = 14.sp,
                                color = ColoresNutri.TextoMedio,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // 2. Fila de botones de acción cuadrados
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BotonAccionCuadrado("📏", "Mediciones", onClick = onNuevoRegistro, modifier = Modifier.weight(1f))
                            BotonAccionCuadrado("📊", "Evaluación", onClick = onResumen, modifier = Modifier.weight(1f))
                            BotonAccionCuadrado("📋", "Notas", onClick = onNuevoRegistro, modifier = Modifier.weight(1f))
                        }
                    }

                    // 3. Tarjeta de Información básica
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = ColoresNutri.Gris)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                TituloSeccion("Información básica", tamano = 16)
                                FilaInfo("Fecha de nacimiento", fechaNacimientoEstimada)
                                FilaInfo(
                                    "Peso",
                                    if (pesoActual != null) "${pesoActual.unDecimal()} Kg" else "--"
                                )
                                FilaInfo(
                                    "Altura",
                                    if (tallaActualCm != null) {
                                        String.format(java.util.Locale.US, "%.2f M", tallaActualCm / 100.0)
                                    } else "--"
                                )
                                FilaInfo("IMC", imcCalculado ?: "--")
                            }
                        }
                    }

                    // 4. Botones principales: Nueva medición (verde destacado) y Estadísticas
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BotonPrincipal(
                                texto = "+ Nueva medición",
                                onClick = onNuevoRegistro
                            )
                            BotonPildora(
                                texto = "📊 Ver Estadísticas y Evaluación",
                                onClick = onResumen,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // 5. Gráfica de evolución
                    if (registros.isNotEmpty() && ultimo != null) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TituloSeccion("Evolución", tamano = 16)
                                    Text(
                                        "${ultimo.peso.unDecimal()} kg · ${ultimo.talla.unDecimal()} cm · " +
                                                "sueño ${ultimo.horasSueno.unDecimal()} h",
                                        color = ColoresNutri.TextoOscuro
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilterChip(
                                            selected = mostrarPeso, onClick = { mostrarPeso = true },
                                            label = { Text("Peso") }, colors = coloresChip()
                                        )
                                        FilterChip(
                                            selected = !mostrarPeso, onClick = { mostrarPeso = false },
                                            label = { Text("Talla") }, colors = coloresChip()
                                        )
                                    }
                                    GraficaLinea(valores = registros.reversed().map { if (mostrarPeso) it.peso else it.talla })
                                    Text(
                                        "${formatearFechaCorta(registros.last().fechaUtc)} → ${formatearFechaCorta(ultimo.fechaUtc)}",
                                        color = ColoresNutri.TextoClaro, fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // 6. Historial de registros al fondo (visible al escrollear)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TituloSeccion("Historial (${registros.size})")
                        }
                    }

                    if (registros.isEmpty()) {
                        item {
                            Text(
                                "Este perfil aún no tiene registros. Toca \"+ Nueva medición\" para registrar los primeros datos.",
                                color = ColoresNutri.TextoMedio,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    } else {
                        items(registros, key = { it.id }) { r -> TarjetaRegistro(r) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaInfo(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(etiqueta, color = ColoresNutri.TextoOscuro, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Text(valor, color = ColoresNutri.TextoOscuro, fontSize = 14.sp)
    }
}

@Composable
private fun TarjetaRegistro(r: RegistroDiario) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(formatearFecha(r.fechaUtc), fontWeight = FontWeight.Bold, color = ColoresNutri.TextoOscuro)
            Text("${r.peso.unDecimal()} kg · ${r.talla.unDecimal()} cm")
            Text("Comidas: ${r.comidas}", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Sueño ${r.horasSueno.unDecimal()} h · energía ${r.energia}/5 · ánimo ${r.animo}/5",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                if (r.sincronizado) "Sincronizado" else "Pendiente de sincronizar",
                style = MaterialTheme.typography.labelSmall,
                color = ColoresNutri.Verde
            )
        }
    }
}
