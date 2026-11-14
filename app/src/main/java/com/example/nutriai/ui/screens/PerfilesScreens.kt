package com.example.nutriai.ui.screens

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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutriai.data.Perfil
import com.example.nutriai.data.RegistroDiario
import com.example.nutriai.ui.Carga
import com.example.nutriai.ui.Guardado
import com.example.nutriai.ui.components.AvatarPerfil
import com.example.nutriai.ui.components.BarraSuperior
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
                                    onClick = { onAbrirPerfil(perfil.id) }
                                )
                            }
                        }
                    }
                }
            }
            TituloSeccion("Accesos rápidos", Modifier.padding(top = 12.dp), tamano = 18)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var esNino by remember { mutableStateOf(false) }
    var intentado by remember { mutableStateOf(false) }
    val guardado by vm.guardado.collectAsStateWithLifecycle()

    LaunchedEffect(guardado) {
        if (guardado is Guardado.Exito) {
            vm.reiniciar()
            onAtras()
        }
    }

    Scaffold(topBar = { BarraSuperior("Nuevo perfil", onAtras) }, containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AvatarPerfil(90.dp, Modifier.padding(top = 16.dp))
            Text(
                "Hola $usuario,",
                color = ColoresNutri.TextoOscuro,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp)
            )
            Text("¿A quién vas a registrar hoy?", color = ColoresNutri.TextoMedio, fontSize = 14.sp)

            CampoTexto(
                nombre, { nombre = it }, "Nombre del perfil",
                modifier = Modifier.padding(top = 24.dp),
                error = if (intentado) Validadores.nombrePerfil(nombre) else null
            )
            CampoTexto(
                edad, { edad = it }, "Edad",
                modifier = Modifier.padding(top = 16.dp),
                teclado = KeyboardType.Number,
                imeAction = ImeAction.Done,
                error = if (intentado) Validadores.edad(edad) else null
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Es un niño o niña a mi cargo", color = ColoresNutri.TextoOscuro, modifier = Modifier.weight(1f))
                Switch(
                    checked = esNino,
                    onCheckedChange = { esNino = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = ColoresNutri.Verde, checkedThumbColor = Color.White)
                )
            }
            if (guardado is Guardado.Error) {
                Text((guardado as Guardado.Error).mensaje, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
            BotonPrincipal(
                texto = "Registrar perfil",
                modifier = Modifier.padding(top = 28.dp),
                cargando = guardado is Guardado.Guardando,
                onClick = {
                    intentado = true
                    if (Validadores.nombrePerfil(nombre) == null && Validadores.edad(edad) == null) {
                        vm.crearPerfil(usuario, nombre, edad.trim().toInt(), esNino)
                    }
                }
            )
        }
    }
}

/** CU04 - Historial y gráfica de evolución de un perfil (RF06). */
@Composable
fun PerfilDetalleScreen(
    perfilId: Int,
    vm: PerfilesViewModel,
    onAtras: () -> Unit,
    onNuevoRegistro: () -> Unit,
    onResumen: () -> Unit,
    onAsistente: () -> Unit
) {
    var perfil by remember { mutableStateOf<Perfil?>(null) }
    LaunchedEffect(perfilId) { perfil = vm.perfil(perfilId) }

    val flujo = remember(perfilId) { vm.registros(perfilId) }
    val carga by flujo.collectAsStateWithLifecycle(initialValue = Carga.Cargando)
    var mostrarPeso by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { BarraSuperior(perfil?.nombre ?: "Perfil", onAtras) },
        containerColor = Color.White
    ) { padding ->
        when (val c = carga) {
            Carga.Cargando -> PantallaCarga(Modifier.padding(padding))
            is Carga.Error -> MensajeCentrado("No se pudo cargar el historial: ${c.mensaje}", Modifier.padding(padding))
            is Carga.Listo -> {
                val registros = c.datos // más reciente primero
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (registros.isNotEmpty()) {
                        val ultimo = registros.first()
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                TileAcceso("📊", "Resumen", onResumen, Modifier.weight(1f))
                                TileAcceso("💬", "Asistente", onAsistente, Modifier.weight(1f))
                            }
                        }
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TituloSeccion("Último registro", tamano = 16)
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
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TituloSeccion("Historial (${registros.size})")
                            BotonPildora("+ Nuevo registro", onNuevoRegistro)
                        }
                    }
                    if (registros.isEmpty()) {
                        item {
                            Text(
                                "Este perfil aún no tiene registros. Toca \"+ Nuevo registro\" para crear el primero.",
                                color = ColoresNutri.TextoMedio
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
