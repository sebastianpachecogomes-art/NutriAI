package com.example.nutriai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriai.data.Repositorio
import com.example.nutriai.domain.AnalizadorBienestar
import com.example.nutriai.domain.ResumenBienestar
import com.example.nutriai.servicios.ServicioIA
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class MensajeChat(val texto: String, val esUsuario: Boolean, val esError: Boolean = false)

/**
 * CU05 - Asistente. Si se abre desde un perfil, usa el resumen de bienestar de ese perfil
 * como contexto (integración entre el módulo de análisis y el de asistente).
 */
class AsistenteViewModel(
    private val repo: Repositorio,
    private val analizador: AnalizadorBienestar,
    private val ia: ServicioIA
) : ViewModel() {

    private val _mensajes = MutableStateFlow(
        listOf(
            MensajeChat(
                "Hola, soy el asistente de BitácoraVita. Pregúntame sobre sueño, alimentación, " +
                    "hidratación o energía.",
                esUsuario = false
            )
        )
    )
    val mensajes: StateFlow<List<MensajeChat>> = _mensajes.asStateFlow()

    private val _escribiendo = MutableStateFlow(false)
    val escribiendo: StateFlow<Boolean> = _escribiendo.asStateFlow()

    private val _perfilContexto = MutableStateFlow<String?>(null)
    /** Nombre del perfil cuyo resumen se usa como contexto (null = consulta general). */
    val perfilContexto: StateFlow<String?> = _perfilContexto.asStateFlow()

    private var contexto: ResumenBienestar? = null

    fun fijarPerfil(perfilId: Int?) {
        viewModelScope.launch {
            if (perfilId == null) {
                contexto = null
                _perfilContexto.value = null
                return@launch
            }
            val perfil = repo.perfil(perfilId)
            contexto = perfil?.let { analizador.analizar(repo.registros(perfilId).first(), it.esNino) }
            _perfilContexto.value = perfil?.nombre
        }
    }

    fun enviar(texto: String) {
        val consulta = texto.trim()
        if (consulta.isBlank() || _escribiendo.value) return
        _mensajes.value = _mensajes.value + MensajeChat(consulta, esUsuario = true)
        viewModelScope.launch {
            _escribiendo.value = true
            val respuesta = try {
                MensajeChat(ia.responder(consulta, contexto), esUsuario = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                MensajeChat("No pude obtener una respuesta en este momento. Inténtalo de nuevo.", false, esError = true)
            }
            _mensajes.value = _mensajes.value + respuesta
            _escribiendo.value = false
        }
    }
}
