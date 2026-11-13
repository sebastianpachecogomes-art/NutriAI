package com.example.nutriai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriai.util.respuestaDePrueba
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MensajeChat(val texto: String, val esUsuario: Boolean)

class AsistenteViewModel : ViewModel() {

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

    fun enviar(texto: String) {
        val consulta = texto.trim()
        if (consulta.isBlank() || _escribiendo.value) return
        _mensajes.value = _mensajes.value + MensajeChat(consulta, esUsuario = true)
        viewModelScope.launch {
            _escribiendo.value = true
            delay(700) // simula la latencia del servicio de IA
            _mensajes.value = _mensajes.value + MensajeChat(respuestaDePrueba(consulta), esUsuario = false)
            _escribiendo.value = false
        }
    }
}
