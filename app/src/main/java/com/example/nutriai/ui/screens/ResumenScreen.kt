package com.example.nutriai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutriai.domain.Hallazgo
import com.example.nutriai.domain.Nivel
import com.example.nutriai.domain.ResumenBienestar
import com.example.nutriai.ui.Carga
import com.example.nutriai.ui.components.AvisoLegal
import com.example.nutriai.ui.components.BarraSuperior
import com.example.nutriai.ui.components.MensajeCentrado
import com.example.nutriai.ui.components.PantallaCarga
import com.example.nutriai.ui.theme.ColoresNutri
import com.example.nutriai.ui.viewmodel.ResumenViewModel
import com.example.nutriai.util.dosDecimales
import com.example.nutriai.util.unDecimal

/** CU08 - Resumen de bienestar de un perfil (RF10). */
@Composable
fun ResumenScreen(perfilId: Int, vm: ResumenViewModel, onAtras: () -> Unit) {
    val flujo = remember(perfilId) { vm.resumen(perfilId) }
    val carga by flujo.collectAsStateWithLifecycle(initialValue = Carga.Cargando)

    Scaffold(topBar = { BarraSuperior("Resumen de bienestar", onAtras) }, containerColor = Color.White) { padding ->
        when (val c = carga) {
            Carga.Cargando -> PantallaCarga(Modifier.padding(padding))
            is Carga.Error -> MensajeCentrado("No se pudo calcular el resumen: ${c.mensaje}", Modifier.padding(padding))
            is Carga.Listo -> {
                val resumen = c.datos
                if (resumen == null) {
                    MensajeCentrado(
                        "Se necesitan al menos ${vm.minimoRegistros} registros para calcular el resumen.",
                        Modifier.padding(padding)
                    )
                } else {
                    ContenidoResumen(resumen, Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
private fun ContenidoResumen(r: ResumenBienestar, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { AvisoLegal() }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Promedios (${r.totalRegistros} registros)", style = MaterialTheme.typography.titleMedium)
                    Text("Sueño: ${r.promedioSueno.unDecimal()} h")
                    Text("Energía: ${r.promedioEnergia.unDecimal()} / 5")
                    Text("Ánimo: ${r.promedioAnimo.unDecimal()} / 5")
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Medidas", style = MaterialTheme.typography.titleMedium)
                    Text("Peso: ${r.pesoInicial.unDecimal()} kg → ${r.pesoActual.unDecimal()} kg")
                    Text("Talla actual: ${r.tallaActual.unDecimal()} cm")
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Relaciones entre variables (r de Pearson)", style = MaterialTheme.typography.titleMedium)
                    Text("Sueño y ánimo: ${r.correlacionSuenoAnimo?.dosDecimales() ?: "sin datos suficientes"}")
                    Text("Sueño y energía: ${r.correlacionSuenoEnergia?.dosDecimales() ?: "sin datos suficientes"}")
                }
            }
        }
        item { Text("Hallazgos", style = MaterialTheme.typography.titleMedium) }
        items(r.hallazgos) { h -> TarjetaHallazgo(h) }
    }
}

@Composable
private fun TarjetaHallazgo(h: Hallazgo) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                if (h.nivel == Nivel.ATENCION) "Atención" else "Información",
                style = MaterialTheme.typography.labelLarge,
                color = if (h.nivel == Nivel.ATENCION) MaterialTheme.colorScheme.error
                else ColoresNutri.Verde
            )
            Text(h.mensaje, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
