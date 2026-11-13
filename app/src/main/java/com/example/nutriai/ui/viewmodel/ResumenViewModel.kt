package com.example.nutriai.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nutriai.data.Repositorio
import com.example.nutriai.domain.AnalizadorBienestar
import com.example.nutriai.domain.ResumenBienestar
import com.example.nutriai.ui.Carga
import com.example.nutriai.ui.comoCarga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** CU08 - Resumen de bienestar: aplica la lógica de dominio a los registros del perfil. */
class ResumenViewModel(
    private val repo: Repositorio,
    private val analizador: AnalizadorBienestar
) : ViewModel() {

    val minimoRegistros: Int get() = analizador.minimoRegistros

    /** `Listo(null)` significa que aún no hay suficientes registros. */
    fun resumen(perfilId: Int): Flow<Carga<ResumenBienestar?>> =
        repo.registros(perfilId)
            .map { registros ->
                val esNino = repo.perfil(perfilId)?.esNino ?: false
                analizador.analizar(registros, esNino)
            }
            .comoCarga()
}
