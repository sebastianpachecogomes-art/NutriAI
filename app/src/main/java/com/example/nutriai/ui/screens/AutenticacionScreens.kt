package com.example.nutriai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriai.ui.components.BotonAuth
import com.example.nutriai.ui.components.CampoAuth
import com.example.nutriai.ui.components.FondoDegradado
import com.example.nutriai.ui.components.LogoYNombre
import com.example.nutriai.ui.components.TextoEnlace
import com.example.nutriai.ui.theme.ColoresNutri
import com.example.nutriai.ui.viewmodel.AuthUiState
import com.example.nutriai.util.Validadores

/** CU01 - Iniciar sesión (RF01). Mantiene el diseño de activity_main.xml de NutriAI. */
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

    FondoDegradado {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoYNombre("BitácoraVita")
            CampoAuth(
                valor = usuario,
                onCambio = { usuario = it; onLimpiarError() },
                pista = "Username",
                modifier = Modifier.padding(top = 32.dp),
                error = if (intentado && usuario.isBlank()) "Escribe tu usuario" else null
            )
            CampoAuth(
                valor = clave,
                onCambio = { clave = it; onLimpiarError() },
                pista = "Password",
                modifier = Modifier.padding(top = 16.dp),
                esContrasena = true,
                imeAction = ImeAction.Done,
                error = if (intentado && clave.isBlank()) "Escribe tu contraseña" else null
            )
            if (estado.error != null) {
                Text(
                    estado.error,
                    color = ColoresNutri.ErrorSobreOscuro,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            BotonAuth(
                texto = "LOGIN",
                cargando = estado.cargando,
                modifier = Modifier.padding(top = 32.dp),
                onClick = {
                    intentado = true
                    if (usuario.isNotBlank() && clave.isNotBlank()) onLogin(usuario, clave)
                }
            )
            TextoEnlace("Crear una cuenta", onIrCrearCuenta)
            Text("Datos de prueba: demo · demo123", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}

/** CU01 (flujo alterno A3) - Crear cuenta. Mantiene el diseño de home_layout.xml de NutriAI. */
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

    FondoDegradado {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LogoYNombre("BitácoraVita")
            CampoAuth(
                usuario, { usuario = it; onLimpiarError() }, "Username",
                modifier = Modifier.padding(top = 32.dp),
                error = if (intentado) Validadores.usuario(usuario) else null
            )
            CampoAuth(
                clave, { clave = it; onLimpiarError() }, "Password",
                modifier = Modifier.padding(top = 16.dp), esContrasena = true,
                error = if (intentado) Validadores.contrasena(clave) else null
            )
            CampoAuth(
                confirmacion, { confirmacion = it; onLimpiarError() }, "Confirm password",
                modifier = Modifier.padding(top = 16.dp), esContrasena = true, imeAction = ImeAction.Done,
                error = if (intentado) Validadores.confirmacion(clave, confirmacion) else null
            )
            if (estado.error != null) {
                Text(estado.error, color = ColoresNutri.ErrorSobreOscuro, fontSize = 14.sp, modifier = Modifier.padding(top = 12.dp))
            }
            BotonAuth(
                texto = "CREAR CUENTA",
                cargando = estado.cargando,
                modifier = Modifier.padding(top = 32.dp),
                onClick = {
                    intentado = true
                    if (Validadores.usuario(usuario) == null &&
                        Validadores.contrasena(clave) == null &&
                        Validadores.confirmacion(clave, confirmacion) == null
                    ) onCrear(usuario, clave)
                }
            )
            TextoEnlace("¿Ya tienes una cuenta? Iniciar sesión", onVolver)
        }
    }
}
