package com.example.nutriai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nutriai.Contenedor

/** Crea los ViewModels inyectándoles lo que necesitan desde el [Contenedor]. */
class VmFactory(private val c: Contenedor) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(c.repositorio)
        modelClass.isAssignableFrom(PerfilesViewModel::class.java) -> PerfilesViewModel(c.repositorio)
        modelClass.isAssignableFrom(RegistroViewModel::class.java) -> RegistroViewModel(c.repositorio)
        modelClass.isAssignableFrom(AsistenteViewModel::class.java) ->
            AsistenteViewModel(c.repositorio, c.analizador, c.ia)
        modelClass.isAssignableFrom(SincronizacionViewModel::class.java) ->
            SincronizacionViewModel(c.repositorio, c.servidor)
        modelClass.isAssignableFrom(ResumenViewModel::class.java) -> ResumenViewModel(c.repositorio, c.analizador)
        modelClass.isAssignableFrom(RecordatorioViewModel::class.java) -> RecordatorioViewModel(c.recordatorios)
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    } as T
}
