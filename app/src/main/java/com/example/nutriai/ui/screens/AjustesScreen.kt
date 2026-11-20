package com.example.nutriai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutriai.ui.Carga
import com.example.nutriai.ui.components.AvatarPerfil
import com.example.nutriai.ui.components.BarraSuperior
import com.example.nutriai.ui.components.BotonPrincipal
import com.example.nutriai.ui.components.TarjetaItem
import com.example.nutriai.ui.components.TituloSeccion
import com.example.nutriai.ui.theme.ColoresNutri
import com.example.nutriai.ui.viewmodel.EstadoSync
import com.example.nutriai.ui.viewmodel.PerfilesViewModel
import com.example.nutriai.ui.viewmodel.SincronizacionViewModel

/** CU07 Perfil del usuario / Cuenta + Control de personas + CU09 Sincronizar. */
@Composable
fun AjustesScreen(
    usuario: String,
    vm: PerfilesViewModel,
    sync: SincronizacionViewModel,
    onCerrarSesion: () -> Unit,
    onVolverAlPanel: () -> Unit,
    onAbrirPerfil: (Int) -> Unit = {},
    barraInferior: @Composable () -> Unit
) {
    val flujoPerfiles = remember(usuario) { vm.perfiles(usuario) }
    val cargaPerfiles by flujoPerfiles.collectAsStateWithLifecycle(initialValue = Carga.Cargando)

    val flujoPendientes = remember(usuario) { vm.pendientes(usuario) }
    val pendientes by flujoPendientes.collectAsStateWithLifecycle(initialValue = 0)
    val estado by sync.estado.collectAsStateWithLifecycle()
    val sinConexion by sync.sinConexion.collectAsStateWithLifecycle()
    val recibidos by sync.recibidosPorServidor.collectAsStateWithLifecycle()


    Scaffold(
        topBar = { BarraSuperior("Perfil") },
        bottomBar = barraInferior,
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Avatar del usuario, Nombre y Rol
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AvatarPerfil(tamano = 96.dp)
                Text(
                    text = if (usuario.isNotBlank() && usuario != "invitado") usuario else "Usuario",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColoresNutri.TextoOscuro,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = "Usuario",
                    fontSize = 13.sp,
                    color = ColoresNutri.TextoMedio,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // 2. Control de personas (niños a cargo)
            val ninos = (cargaPerfiles as? Carga.Listo)?.datos.orEmpty()
            if (ninos.isNotEmpty()) {
                TituloSeccion("Control de personas", tamano = 16)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ninos.take(4).forEach { nino ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onAbrirPerfil(nino.id) }
                                .padding(4.dp)
                        ) {
                            AvatarPerfil(tamano = 64.dp, fotoPath = nino.fotoPath)
                            Text(
                                text = nino.nombre,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColoresNutri.TextoOscuro,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }

            // 3. Configuración de cuenta
            TituloSeccion("Configuración de cuenta", tamano = 16)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ColoresNutri.Gris)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    // Cerrar sesión
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onCerrarSesion)
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("↪", fontSize = 18.sp, color = MaterialTheme.colorScheme.error)
                        Text(
                            "Cerrar sesión",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 4. Panel de Sincronización
            TituloSeccion("Sincronización de datos", tamano = 16)
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
                Text("Simular servidor sin conexión", color = ColoresNutri.TextoOscuro, modifier = Modifier.weight(1f))
                Switch(
                    checked = sinConexion,
                    onCheckedChange = {
                        sync.simularSinConexion(it)
                        sync.descartarMensaje()
                    },
                    colors = SwitchDefaults.colors(checkedTrackColor = ColoresNutri.Verde, checkedThumbColor = Color.White)
                )
            }
            BotonPrincipal(
                texto = "Sincronizar ahora",
                habilitado = pendientes > 0,
                cargando = estado is EstadoSync.Sincronizando,
                onClick = { sync.sincronizar(usuario) }
            )
            when (val e = estado) {
                is EstadoSync.Exito -> Text("Se sincronizaron ${e.cantidad} registros.", color = ColoresNutri.Verde)
                is EstadoSync.Info -> Text(e.mensaje, color = ColoresNutri.TextoMedio)
                is EstadoSync.Error -> Text(e.mensaje, color = MaterialTheme.colorScheme.error)
                else -> Unit
            }

            // 5. Botón Volver al panel
            BotonPrincipal(
                texto = "Volver al panel",
                onClick = onVolverAlPanel,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
