package com.example.nutriai.domain

/** Resultado del análisis de los registros de un perfil. */
data class ResumenBienestar(
    val totalRegistros: Int,
    val promedioSueno: Double,
    val promedioEnergia: Double,
    val promedioAnimo: Double,
    val pesoInicial: Double,
    val pesoActual: Double,
    val tallaActual: Double,
    val correlacionSuenoAnimo: Double?,
    val correlacionSuenoEnergia: Double?,
    val hallazgos: List<Hallazgo>
)
