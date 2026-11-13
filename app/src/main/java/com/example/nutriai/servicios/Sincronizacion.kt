package com.example.nutriai.servicios

import com.example.nutriai.data.RegistroDiario
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Resultado de enviar un lote de registros al servidor. */
sealed interface ResultadoSync {
    data class Aceptado(val ids: List<Int>) : ResultadoSync
    data class Rechazado(val mensaje: String) : ResultadoSync
    data object SinConexion : ResultadoSync
    data object NadaPendiente : ResultadoSync
}

/**
 * Contrato del cliente de sincronización (RF07).
 *
 * PUNTO DE SUSTITUCIÓN: hoy se usa [ServidorSimulado]. Para conectar el backend real basta con
 * crear una clase que implemente esta interfaz y haga POST del lote como JSON a la API en Go
 * (esperando HTTP 202), y cambiar la instancia en `DatosApplication.contenedor`.
 */
interface ServicioSincronizacion {
    suspend fun enviar(lote: List<RegistroDiario>): ResultadoSync
}

/**
 * Servidor simulado en memoria. Aplica las mismas reglas de validación que tendría la API en Go
 * y permite simular la falta de conexión para probar el manejo de errores.
 */
class ServidorSimulado(private val latenciaMs: Long = 800L) : ServicioSincronizacion {

    private val _sinConexion = MutableStateFlow(false)
    val sinConexion: StateFlow<Boolean> = _sinConexion.asStateFlow()

    private val idsRecibidos = mutableSetOf<Int>()
    private val _recibidos = MutableStateFlow(0)

    /** Cantidad de registros almacenados por el servidor simulado en esta sesión. */
    val recibidos: StateFlow<Int> = _recibidos.asStateFlow()

    fun simularSinConexion(valor: Boolean) {
        _sinConexion.value = valor
    }

    override suspend fun enviar(lote: List<RegistroDiario>): ResultadoSync {
        delay(latenciaMs) // latencia de red simulada
        if (_sinConexion.value) return ResultadoSync.SinConexion
        val invalido = lote.firstOrNull { !esValido(it) }
        if (invalido != null) {
            return ResultadoSync.Rechazado("El servidor rechazó el registro ${invalido.id} (HTTP 400 simulado)")
        }
        synchronized(idsRecibidos) {
            idsRecibidos.addAll(lote.map { it.id })
            _recibidos.value = idsRecibidos.size
        }
        return ResultadoSync.Aceptado(lote.map { it.id })
    }

    private fun esValido(r: RegistroDiario): Boolean =
        r.peso > 0.0 && r.talla > 0.0 &&
            r.horasSueno in 0.0..24.0 &&
            r.energia in 1..5 && r.animo in 1..5 &&
            r.comidas.isNotBlank()
}
