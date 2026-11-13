package com.example.nutriai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriai.data.Repositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(val cargando: Boolean = false, val error: String? = null)

/** Sesión del usuario (en memoria en el Sprint 1) y estado de login / registro. */
class AuthViewModel(private val repo: Repositorio) : ViewModel() {

    private val _usuario = MutableStateFlow<String?>(null)
    val usuario: StateFlow<String?> = _usuario.asStateFlow()

    private val _estado = MutableStateFlow(AuthUiState())
    val estado: StateFlow<AuthUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch { repo.sembrarDatosDePrueba() }
    }

    fun iniciarSesion(usuario: String, contrasena: String) {
        val u = usuario.trim()
        if (u.isBlank() || contrasena.isBlank()) {
            _estado.value = AuthUiState(error = "Completa usuario y contraseña")
            return
        }
        viewModelScope.launch {
            _estado.value = AuthUiState(cargando = true)
            val ok = repo.autenticar(u, contrasena)
            if (ok) {
                _estado.value = AuthUiState()
                _usuario.value = u
            } else {
                _estado.value = AuthUiState(error = "Usuario o contraseña incorrectos")
            }
        }
    }

    fun registrar(usuario: String, contrasena: String) {
        val u = usuario.trim()
        viewModelScope.launch {
            _estado.value = AuthUiState(cargando = true)
            val creada = repo.registrarCuenta(u, contrasena)
            if (creada) {
                _estado.value = AuthUiState()
                _usuario.value = u // inicia sesión automáticamente
            } else {
                _estado.value = AuthUiState(error = "Ese nombre de usuario ya está registrado")
            }
        }
    }

    fun cerrarSesion() {
        _usuario.value = null
        _estado.value = AuthUiState()
    }

    fun limpiarError() {
        _estado.value = _estado.value.copy(error = null)
    }
}
