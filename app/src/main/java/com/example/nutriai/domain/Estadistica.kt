package com.example.nutriai.domain

import kotlin.math.sqrt

/** Funciones estadísticas puras. Equivalen a las agregaciones (AVG, CORR) de la vista materializada del servidor. */
object Estadistica {

    fun promedio(valores: List<Double>): Double =
        if (valores.isEmpty()) 0.0 else valores.sum() / valores.size

    /**
     * Coeficiente de correlación de Pearson entre dos series del mismo tamaño.
     * @return un valor entre -1 y 1, o null si hay menos de 3 datos o una de las series no varía.
     */
    fun correlacion(x: List<Double>, y: List<Double>): Double? {
        if (x.size != y.size || x.size < 3) return null
        val mx = promedio(x)
        val my = promedio(y)
        var numerador = 0.0
        var sx = 0.0
        var sy = 0.0
        for (i in x.indices) {
            val dx = x[i] - mx
            val dy = y[i] - my
            numerador += dx * dy
            sx += dx * dx
            sy += dy * dy
        }
        val denominador = sqrt(sx * sy)
        return if (denominador == 0.0) null else (numerador / denominador).coerceIn(-1.0, 1.0)
    }
}
