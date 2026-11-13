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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.nutriai.ui.components.BotonPrincipal
import com.example.nutriai.ui.components.CampoTexto
import com.example.nutriai.ui.viewmodel.AuthUiState
import com.example.nutriai.util.Validadores

/** CU01 - Iniciar sesión (RF01). */
@Composable
fun LoginScreen(
    estado: AuthUiState,
    onLogin: (String, String) -> Unit,
    onIrCrearCuenta: () -> Unit,
    onLimpiarError: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var intentado by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("BitácoraVita", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Text("Nutrición, sueño y ánimo en un solo registro", style = MaterialTheme.typography.bodyMedium)

            CampoTexto(
                valor = usuario,
                onCambio = { usuario = it; onLimpiarError() },
                etiqueta = "Usuario",
                error = if (intentado && usuario.isBlank()) "Escribe tu usuario" else null
            )
            CampoTexto(
                valor = clave,
                onCambio = { clave = it; onLimpiarError() },
                etiqueta = "Contraseña",
                esContrasena = true,
                imeAction = ImeAction.Done,
                error = if (intentado && clave.isBlank()) "Escribe tu contraseña" else null
            )
            if (estado.error != null) {
                Text(estado.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            BotonPrincipal(
                texto = "Iniciar sesión",
                cargando = estado.cargando,
                onClick = {
                    intentado = true
                    if (usuario.isNotBlank() && clave.isNotBlank()) onLogin(usuario, clave)
                }
            )
            TextButton(onClick = onIrCrearCuenta) { Text("¿No tienes cuenta? Regístrate") }
            Text(
                "Datos de prueba: usuario demo · contraseña demo123",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/** CU01 (flujo alterno A3) - Crear cuenta. */
@Composable
fun CrearCuentaScreen(
    estado: AuthUiState,
    onCrear: (String, String) -> Unit,
    onVolver: () -> Unit,
    onLimpiarError: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    var intentado by remember { mutableStateOf(false) }

    val errorUsuario = if (intentado) Validadores.usuario(usuario) else null
    val errorClave = if (intentado) Validadores.contrasena(clave) else null
    val errorConfirmacion = if (intentado) Validadores.confirmacion(clave, confirmacion) else null

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            CampoTexto(usuario, { usuario = it; onLimpiarError() }, "Usuario", error = errorUsuario)
            CampoTexto(clave, { clave = it; onLimpiarError() }, "Contraseña", error = errorClave, esContrasena = true)
            CampoTexto(
                confirmacion, { confirmacion = it; onLimpiarError() }, "Confirmar contraseña",
                error = errorConfirmacion, esContrasena = true, imeAction = ImeAction.Done
            )
            if (estado.error != null) {
                Text(estado.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            BotonPrincipal(
                texto = "Crear cuenta",
                cargando = estado.cargando,
                onClick = {
                    intentado = true
                    if (Validadores.usuario(usuario) == null &&
                        Validadores.contrasena(clave) == null &&
                        Validadores.confirmacion(clave, confirmacion) == null
                    ) onCrear(usuario, clave)
                }
            )
            TextButton(onClick = onVolver) { Text("¿Ya tienes cuenta? Inicia sesión") }
        }
    }
}
