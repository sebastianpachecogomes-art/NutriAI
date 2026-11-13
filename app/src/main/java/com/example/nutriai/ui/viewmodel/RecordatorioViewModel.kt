package com.example.nutriai.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nutriai.servicios.ConfigRecordatorio
import com.example.nutriai.servicios.GestorRecordatorios
import com.example.nutriai.util.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** CU06 - Configurar el recordatorio diario. */
class RecordatorioViewModel(private val gestor: GestorRecordatorios) : ViewModel() {

    private val _config = MutableStateFlow(gestor.configuracion())
    val config: StateFlow<ConfigRecordatorio> = _config.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun activar(hora: String, minuto: String) {
        val error = Validadores.hora(hora) ?: Validadores.minuto(minuto)
        if (error != null) {
            _mensaje.value = error
            return
        }
        try {
            gestor.activar(hora.trim().toInt(), minuto.trim().toInt())
            _config.value = gestor.configuracion()
            _mensaje.value = "Recordatorio programado todos los días a las %02d:%02d".format(
                _config.value.hora, _config.value.minuto
            )
        } catch (e: Exception) {
            _mensaje.value = "No se pudo programar el recordatorio: ${e.message}"
        }
    }

    fun desactivar() {
        gestor.desactivar()
        _config.value = gestor.configuracion()
        _mensaje.value = "Recordatorio desactivado"
    }

    fun mostrarMensaje(texto: String) {
        _mensaje.value = texto
    }
}
