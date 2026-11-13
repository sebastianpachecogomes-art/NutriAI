package com.example.nutriai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriai.data.Repositorio
import com.example.nutriai.servicios.ResultadoSync
import com.example.nutriai.servicios.ServidorSimulado
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EstadoSync {
    data object Inactivo : EstadoSync
    data object Sincronizando : EstadoSync
    data class Exito(val cantidad: Int) : EstadoSync
    data class Info(val mensaje: String) : EstadoSync
    data class Error(val mensaje: String) : EstadoSync
}

/** CU09 - Sincronizar con el servidor (simulado). */
class SincronizacionViewModel(
    private val repo: Repositorio,
    private val servidor: ServidorSimulado
) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoSync>(EstadoSync.Inactivo)
    val estado: StateFlow<EstadoSync> = _estado.asStateFlow()

    val sinConexion: StateFlow<Boolean> = servidor.sinConexion
    val recibidosPorServidor: StateFlow<Int> = servidor.recibidos

    fun simularSinConexion(valor: Boolean) = servidor.simularSinConexion(valor)

    fun sincronizar(usuario: String) {
        if (_estado.value is EstadoSync.Sincronizando) return
        viewModelScope.launch {
            _estado.value = EstadoSync.Sincronizando
            _estado.value = try {
                when (val r = repo.sincronizar(usuario, servidor)) {
                    is ResultadoSync.Aceptado -> EstadoSync.Exito(r.ids.size)
                    is ResultadoSync.Rechazado -> EstadoSync.Error(r.mensaje)
                    ResultadoSync.SinConexion ->
                        EstadoSync.Error("Sin conexión con el servidor. Tus registros siguen guardados en el dispositivo.")
                    ResultadoSync.NadaPendiente -> EstadoSync.Info("No hay registros pendientes de sincronizar.")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                EstadoSync.Error(e.message ?: "Error inesperado al sincronizar")
            }
        }
    }

    fun descartarMensaje() {
        if (_estado.value !is EstadoSync.Sincronizando) _estado.value = EstadoSync.Inactivo
    }
}
