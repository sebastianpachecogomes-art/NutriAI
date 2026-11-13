package com.example.nutriai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutriai.ui.components.BarraSuperior
import com.example.nutriai.ui.components.BotonPrincipal
import com.example.nutriai.ui.components.TarjetaItem
import com.example.nutriai.ui.viewmodel.EstadoSync
import com.example.nutriai.ui.viewmodel.PerfilesViewModel
import com.example.nutriai.ui.viewmodel.SincronizacionViewModel

/** CU07 Ajustes + CU09 Sincronizar (servidor simulado) + acceso a CU06 Recordatorio. */
@Composable
fun AjustesScreen(
    usuario: String,
    vm: PerfilesViewModel,
    sync: SincronizacionViewModel,
    onAbrirRecordatorio: () -> Unit,
    onCerrarSesion: () -> Unit,
    barraInferior: @Composable () -> Unit
) {
    val flujoPendientes = remember(usuario) { vm.pendientes(usuario) }
    val pendientes by flujoPendientes.collectAsStateWithLifecycle(initialValue = 0)
    val estado by sync.estado.collectAsStateWithLifecycle()
    val sinConexion by sync.sinConexion.collectAsStateWithLifecycle()
    val recibidos by sync.recibidosPorServidor.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { BarraSuperior("Ajustes") },
        bottomBar = barraInferior
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaItem(titulo = usuario, subtitulo = "Cuenta con sesión iniciada")

            Text("Sincronización", style = MaterialTheme.typography.titleMedium)
            TarjetaItem(
                titulo = "Pendientes de sincronizar",
                subtitulo = "Registros guardados solo en este dispositivo",
                etiqueta = pendientes.toString()
            )
            TarjetaItem(
                titulo = "Recibidos por el servidor simulado",
                subtitulo = "Solo en esta sesión; se reemplazará por la API en Go",
                etiqueta = recibidos.toString()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Simular servidor sin conexión", modifier = Modifier.weight(1f))
                Switch(checked = sinConexion, onCheckedChange = { sync.simularSinConexion(it) })
            }
            BotonPrincipal(
                texto = "Sincronizar ahora",
                habilitado = pendientes > 0,
                cargando = estado is EstadoSync.Sincronizando,
                onClick = { sync.sincronizar(usuario) }
            )
            when (val e = estado) {
                is EstadoSync.Exito -> Text(
                    "Se sincronizaron ${e.cantidad} registros.",
                    color = MaterialTheme.colorScheme.primary
                )
                is EstadoSync.Info -> Text(e.mensaje)
                is EstadoSync.Error -> Text(e.mensaje, color = MaterialTheme.colorScheme.error)
                else -> Unit
            }

            Text("Recordatorios", style = MaterialTheme.typography.titleMedium)
            TarjetaItem(
                titulo = "Recordatorio diario",
                subtitulo = "Aviso para completar el registro del día",
                onClick = onAbrirRecordatorio
            )

            OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión")
            }
        }
    }
}
