package com.example.nutriai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutriai.ui.components.BarraSuperior
import com.example.nutriai.ui.components.TarjetaItem
import com.example.nutriai.ui.viewmodel.PerfilesViewModel

/** CU07 - Ajustes y cierre de sesión (RF11). La sincronización real llega en el Sprint 2. */
@Composable
fun AjustesScreen(
    usuario: String,
    vm: PerfilesViewModel,
    onCerrarSesion: () -> Unit,
    barraInferior: @Composable () -> Unit
) {
    val flujo = remember(usuario) { vm.pendientes(usuario) }
    val pendientes by flujo.collectAsStateWithLifecycle(initialValue = 0)

    Scaffold(
        topBar = { BarraSuperior("Ajustes") },
        bottomBar = barraInferior
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaItem(titulo = usuario, subtitulo = "Cuenta con sesión iniciada")
            TarjetaItem(
                titulo = "Registros pendientes de sincronizar",
                subtitulo = "Se enviarán al servidor cuando esté disponible",
                etiqueta = pendientes.toString()
            )
            Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                Text("Sincronizar ahora (Sprint 2)")
            }
            Text(
                "Recordatorios: próximamente.",
                style = MaterialTheme.typography.bodySmall
            )
            OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión")
            }
        }
    }
}
