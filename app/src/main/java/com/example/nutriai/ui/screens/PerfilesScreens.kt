package com.example.nutriai.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutriai.data.Perfil
import com.example.nutriai.data.RegistroDiario
import com.example.nutriai.ui.Carga
import com.example.nutriai.ui.Guardado
import com.example.nutriai.ui.components.BarraSuperior
import com.example.nutriai.ui.components.BotonPrincipal
import com.example.nutriai.ui.components.CampoTexto
import com.example.nutriai.ui.components.GraficaLinea
import com.example.nutriai.ui.components.MensajeCentrado
import com.example.nutriai.ui.components.PantallaCarga
import com.example.nutriai.ui.components.TarjetaItem
import com.example.nutriai.ui.viewmodel.PerfilesViewModel
import com.example.nutriai.util.Validadores
import com.example.nutriai.util.formatearFecha
import com.example.nutriai.util.formatearFechaCorta
import com.example.nutriai.util.unDecimal

/** CU02 - Lista de perfiles con búsqueda (RF02). */
@Composable
fun PerfilesScreen(
    usuario: String,
    vm: PerfilesViewModel,
    onAbrirPerfil: (Int) -> Unit,
    onNuevoPerfil: () -> Unit,
    barraInferior: @Composable () -> Unit
) {
    val flujo = remember(usuario) { vm.perfiles(usuario) }
    val carga by flujo.collectAsStateWithLifecycle(initialValue = Carga.Cargando)
    var busqueda by remember { mutableStateOf("") }

    Scaffold(
        topBar = { BarraSuperior("Mis perfiles") },
        bottomBar = barraInferior,
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevoPerfil) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar perfil")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CampoTexto(
                valor = busqueda,
                onCambio = { busqueda = it },
                etiqueta = "Buscar por nombre",
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                imeAction = ImeAction.Search
            )
            when (val c = carga) {
                Carga.Cargando -> PantallaCarga()
                is Carga.Error -> MensajeCentrado("No se pudieron cargar los perfiles: ${c.mensaje}")
                is Carga.Listo -> {
                    val filtrados = if (busqueda.isBlank()) c.datos
                    else c.datos.filter { it.nombre.contains(busqueda, ignoreCase = true) }
                    if (c.datos.isEmpty()) {
                        MensajeCentrado("Aún no tienes perfiles. Toca + para crear el primero.")
                    } else {
                        Text(
                            "${filtrados.size} resultados encontrados",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filtrados, key = { it.id }) { perfil ->
                                TarjetaItem(
                                    titulo = perfil.nombre,
                                    subtitulo = "${perfil.edad} años",
                                    etiqueta = if (perfil.esNino) "Niño/a" else "Personal",
                                    onClick = { onAbrirPerfil(perfil.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** CU02 - Crear perfil. */
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

    Scaffold(topBar = { BarraSuperior("Nuevo perfil", onAtras) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTexto(nombre, { nombre = it }, "Nombre", error = if (intentado) Validadores.nombrePerfil(nombre) else null)
            CampoTexto(
                edad, { edad = it }, "Edad (años)",
                teclado = KeyboardType.Number,
                imeAction = ImeAction.Done,
                error = if (intentado) Validadores.edad(edad) else null
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Es un niño o niña a mi cargo", modifier = Modifier.weight(1f))
                Switch(checked = esNino, onCheckedChange = { esNino = it })
            }
            if (guardado is Guardado.Error) {
                Text((guardado as Guardado.Error).mensaje, color = MaterialTheme.colorScheme.error)
            }
            BotonPrincipal(
                texto = "Guardar perfil",
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Nuevo registro") },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                onClick = onNuevoRegistro
            )
        }
    ) { padding ->
        when (val c = carga) {
            Carga.Cargando -> PantallaCarga(Modifier.padding(padding))
            is Carga.Error -> MensajeCentrado("No se pudo cargar el historial: ${c.mensaje}", Modifier.padding(padding))
            is Carga.Listo -> {
                if (c.datos.isEmpty()) {
                    MensajeCentrado(
                        "Este perfil aún no tiene registros. Toca \"Nuevo registro\" para crear el primero.",
                        Modifier.padding(padding)
                    )
                } else {
                    val registros = c.datos // más reciente primero
                    val ultimo = registros.first()
                    LazyColumn(
                        modifier = Modifier.padding(padding),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Último registro", style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "${ultimo.peso.unDecimal()} kg · ${ultimo.talla.unDecimal()} cm · " +
                                            "sueño ${ultimo.horasSueno.unDecimal()} h"
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilterChip(selected = mostrarPeso, onClick = { mostrarPeso = true }, label = { Text("Peso") })
                                        FilterChip(selected = !mostrarPeso, onClick = { mostrarPeso = false }, label = { Text("Talla") })
                                    }
                                    GraficaLinea(
                                        valores = registros.reversed().map { if (mostrarPeso) it.peso else it.talla }
                                    )
                                    Text(
                                        "${formatearFechaCorta(registros.last().fechaUtc)} → ${formatearFechaCorta(ultimo.fechaUtc)}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(onClick = onResumen, modifier = Modifier.weight(1f)) { Text("Ver resumen") }
                                OutlinedButton(onClick = onAsistente, modifier = Modifier.weight(1f)) { Text("Preguntar al asistente") }
                            }
                        }
                        item { Text("Historial (${registros.size})", style = MaterialTheme.typography.titleMedium) }
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
            Text(formatearFecha(r.fechaUtc), style = MaterialTheme.typography.titleSmall)
            Text("${r.peso.unDecimal()} kg · ${r.talla.unDecimal()} cm")
            Text("Comidas: ${r.comidas}", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Sueño ${r.horasSueno.unDecimal()} h · energía ${r.energia}/5 · ánimo ${r.animo}/5",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                if (r.sincronizado) "Sincronizado" else "Pendiente de sincronizar",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
