package com.example.nutriai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutriai.ui.components.AvisoLegal
import com.example.nutriai.ui.components.BarraSuperior
import com.example.nutriai.ui.viewmodel.AsistenteViewModel
import com.example.nutriai.ui.viewmodel.MensajeChat

/** CU05 - Asistente (Sprint 1: respuestas locales de prueba, con aviso legal visible, RNF03). */
@Composable
fun AsistenteScreen(vm: AsistenteViewModel, barraInferior: @Composable () -> Unit) {
    val mensajes by vm.mensajes.collectAsStateWithLifecycle()
    val escribiendo by vm.escribiendo.collectAsStateWithLifecycle()
    var texto by remember { mutableStateOf("") }
    val estadoLista = rememberLazyListState()

    LaunchedEffect(mensajes.size, escribiendo) {
        val total = mensajes.size + if (escribiendo) 1 else 0
        if (total > 0) estadoLista.animateScrollToItem(total - 1)
    }

    Scaffold(
        topBar = { BarraSuperior("Asistente") },
        bottomBar = barraInferior
    ) { padding ->
        Column(modifier = Modifier.padding(padding).imePadding()) {
            AvisoLegal(modifier = Modifier.padding(12.dp))
            LazyColumn(
                state = estadoLista,
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mensajes) { m -> Burbuja(m) }
                if (escribiendo) {
                    item { Burbuja(MensajeChat("Escribiendo…", esUsuario = false)) }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("Escribe tu consulta") },
                    modifier = Modifier.weight(1f),
                    maxLines = 3
                )
                IconButton(
                    onClick = {
                        vm.enviar(texto)
                        texto = ""
                    },
                    enabled = texto.isNotBlank() && !escribiendo
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                }
            }
        }
    }
}

@Composable
private fun Burbuja(m: MensajeChat) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (m.esUsuario) Alignment.CenterEnd else Alignment.CenterStart) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (m.esUsuario) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(m.texto, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
