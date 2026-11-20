package com.example.nutriai.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutriai.servicios.GestorRecordatorios
import com.example.nutriai.ui.components.BarraSuperior
import com.example.nutriai.ui.components.BotonPildora
import com.example.nutriai.ui.components.BotonPrincipal
import com.example.nutriai.ui.components.CampoTexto
import com.example.nutriai.ui.theme.ColoresNutri
import com.example.nutriai.ui.viewmodel.RecordatorioViewModel
import com.example.nutriai.util.Validadores

/** CU06 - Configurar recordatorio diario (RF09). */
@Composable
fun RecordatorioScreen(vm: RecordatorioViewModel, barraInferior: @Composable () -> Unit) {
    val config by vm.config.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    var hora by remember { mutableStateOf(config.hora.toString()) }
    var minuto by remember { mutableStateOf(config.minuto.toString()) }
    var intentado by remember { mutableStateOf(false) }

    LaunchedEffect(config) {
        hora = config.hora.toString()
        minuto = config.minuto.toString()
    }

    val lanzadorPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) vm.activar(hora, minuto)
        else vm.mostrarMensaje(
            "Sin el permiso de notificaciones no se mostrará el aviso. Puedes activarlo en los ajustes del sistema."
        )
    }

    Scaffold(
        topBar = { BarraSuperior("Recordatorios") },
        bottomBar = barraInferior,
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).imePadding().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                if (config.activo) "Estado: activo a las %02d:%02d".format(config.hora, config.minuto)
                else "Estado: desactivado",
                style = MaterialTheme.typography.titleMedium,
                color = ColoresNutri.TextoOscuro
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoTexto(
                    hora, { hora = it }, "Hora (0-23)",
                    modifier = Modifier.weight(1f),
                    teclado = KeyboardType.Number,
                    error = if (intentado) Validadores.hora(hora) else null
                )
                CampoTexto(
                    minuto, { minuto = it }, "Minutos (0-59)",
                    modifier = Modifier.weight(1f),
                    teclado = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                    error = if (intentado) Validadores.minuto(minuto) else null
                )
            }
            BotonPrincipal(
                texto = "Guardar y activar",
                onClick = {
                    intentado = true
                    if (Validadores.hora(hora) == null && Validadores.minuto(minuto) == null) {
                        if (GestorRecordatorios.tienePermisoNotificaciones(contexto)) vm.activar(hora, minuto)
                        else lanzadorPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            )
            if (config.activo) {
                BotonPildora("Desactivar recordatorio", { vm.desactivar() })
            }
            if (mensaje != null) Text(mensaje.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            Text(
                "La alarma se programa en el dispositivo. Si lo reinicias, vuelve a activar el recordatorio.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
