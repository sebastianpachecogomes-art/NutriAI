package com.example.nutriai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nutriai.data.Repositorio

/** Crea los ViewModels inyectándoles el repositorio. */
class VmFactory(private val repo: Repositorio) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repo)
        modelClass.isAssignableFrom(PerfilesViewModel::class.java) -> PerfilesViewModel(repo)
        modelClass.isAssignableFrom(RegistroViewModel::class.java) -> RegistroViewModel(repo)
        modelClass.isAssignableFrom(AsistenteViewModel::class.java) -> AsistenteViewModel()
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    } as T
}
