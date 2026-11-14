package com.example.nutriai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nutriai.ui.Guardado
import com.example.nutriai.ui.components.BarraSuperior
import com.example.nutriai.ui.components.BotonPrincipal
import com.example.nutriai.ui.components.CampoTexto
import com.example.nutriai.ui.components.SelectorEscala
import com.example.nutriai.ui.components.TituloSeccion
import com.example.nutriai.ui.viewmodel.RegistroViewModel
import com.example.nutriai.util.Validadores

/** CU03 - Registro diario integral: medidas, alimentación, sueño, energía y ánimo (RF03-RF05). */
@Composable
fun NuevoRegistroScreen(
    perfilId: Int,
    vm: RegistroViewModel,
    onAtras: () -> Unit
) {
    var peso by remember { mutableStateOf("") }
    var talla by remember { mutableStateOf("") }
    var comidas by remember { mutableStateOf("") }
    var horas by remember { mutableStateOf("") }
    var energia by remember { mutableIntStateOf(3) }
    var animo by remember { mutableIntStateOf(3) }
    var intentado by remember { mutableStateOf(false) }
    val guardado by vm.guardado.collectAsStateWithLifecycle()

    LaunchedEffect(guardado) {
        if (guardado is Guardado.Exito) {
            vm.reiniciar()
            onAtras()
        }
    }

    Scaffold(topBar = { BarraSuperior("Nuevo registro", onAtras) }, containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TituloSeccion("Medidas", tamano = 18)
            CampoTexto(
                peso, { peso = it }, "Peso (kg)",
                teclado = KeyboardType.Decimal,
                error = if (intentado) Validadores.peso(peso) else null
            )
            CampoTexto(
                talla, { talla = it }, "Talla (cm)",
                teclado = KeyboardType.Decimal,
                error = if (intentado) Validadores.talla(talla) else null
            )

            TituloSeccion("Alimentación", tamano = 18)
            CampoTexto(
                comidas, { comidas = it }, "¿Qué comiste hoy?",
                lineas = 3,
                error = if (intentado) Validadores.comidas(comidas) else null
            )

            TituloSeccion("Sueño y ánimo", tamano = 18)
            CampoTexto(
                horas, { horas = it }, "Horas de sueño",
                teclado = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
                error = if (intentado) Validadores.horasSueno(horas) else null
            )
            SelectorEscala("Nivel de energía", energia, { energia = it }, "muy bajo", "muy alto")
            SelectorEscala("Estado de ánimo", animo, { animo = it }, "muy bajo", "muy bueno")

            if (guardado is Guardado.Error) {
                Text((guardado as Guardado.Error).mensaje, color = MaterialTheme.colorScheme.error)
            }
            BotonPrincipal(
                texto = "Guardar registro",
                cargando = guardado is Guardado.Guardando,
                onClick = {
                    intentado = true
                    val valido = Validadores.peso(peso) == null &&
                        Validadores.talla(talla) == null &&
                        Validadores.comidas(comidas) == null &&
                        Validadores.horasSueno(horas) == null
                    if (valido) vm.guardar(perfilId, peso, talla, comidas, horas, energia, animo)
                }
            )
        }
    }
}
