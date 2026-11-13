package com.example.nutriai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriai.data.RegistroDiario
import com.example.nutriai.data.Repositorio
import com.example.nutriai.ui.Guardado
import com.example.nutriai.util.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Guarda el registro diario integral (CU03). La fecha se toma en UTC (RNF06). */
class RegistroViewModel(private val repo: Repositorio) : ViewModel() {

    private val _guardado = MutableStateFlow<Guardado>(Guardado.Inactivo)
    val guardado: StateFlow<Guardado> = _guardado.asStateFlow()

    fun guardar(
        perfilId: Int,
        peso: String,
        talla: String,
        comidas: String,
        horasSueno: String,
        energia: Int,
        animo: Int
    ) {
        val p = Validadores.aDouble(peso)
        val t = Validadores.aDouble(talla)
        val h = Validadores.aDouble(horasSueno)
        if (p == null || t == null || h == null) {
            _guardado.value = Guardado.Error("Revisa los campos numéricos")
            return
        }
        viewModelScope.launch {
            _guardado.value = Guardado.Guardando
            _guardado.value = try {
                repo.guardarRegistro(
                    RegistroDiario(
                        perfilId = perfilId,
                        fechaUtc = System.currentTimeMillis(),
                        peso = p, talla = t,
                        comidas = comidas.trim(),
                        horasSueno = h,
                        energia = energia, animo = animo
                    )
                )
                Guardado.Exito
            } catch (e: Exception) {
                Guardado.Error(e.message ?: "No se pudo guardar el registro")
            }
        }
    }

    fun reiniciar() {
        _guardado.value = Guardado.Inactivo
    }
}
