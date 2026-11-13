package com.example.nutriai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriai.data.Perfil
import com.example.nutriai.data.RegistroDiario
import com.example.nutriai.data.Repositorio
import com.example.nutriai.ui.Carga
import com.example.nutriai.ui.Guardado
import com.example.nutriai.ui.comoCarga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilesViewModel(private val repo: Repositorio) : ViewModel() {

    private val _guardado = MutableStateFlow<Guardado>(Guardado.Inactivo)
    val guardado: StateFlow<Guardado> = _guardado.asStateFlow()

    fun perfiles(usuario: String): Flow<Carga<List<Perfil>>> = repo.perfiles(usuario).comoCarga()

    fun registros(perfilId: Int): Flow<Carga<List<RegistroDiario>>> = repo.registros(perfilId).comoCarga()

    fun pendientes(usuario: String): Flow<Int> = repo.pendientes(usuario)

    suspend fun perfil(id: Int): Perfil? = repo.perfil(id)

    fun crearPerfil(usuario: String, nombre: String, edad: Int, esNino: Boolean) {
        viewModelScope.launch {
            _guardado.value = Guardado.Guardando
            _guardado.value = try {
                repo.crearPerfil(Perfil(nombre.trim(), edad, esNino, usuario))
                Guardado.Exito
            } catch (e: Exception) {
                Guardado.Error(e.message ?: "No se pudo guardar el perfil")
            }
        }
    }

    fun reiniciar() {
        _guardado.value = Guardado.Inactivo
    }
}
