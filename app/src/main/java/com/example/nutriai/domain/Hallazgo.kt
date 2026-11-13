package com.example.nutriai.domain

import java.util.Locale

enum class Nivel { INFO, ATENCION }

/**
 * Modelo POO de los resultados del análisis: cada tipo de hallazgo es una subclase
 * que sabe explicarse a sí misma (polimorfismo sobre [mensaje] y [nivel]).
 * Los mensajes son orientativos y no constituyen un diagnóstico (RNF03).
 */
sealed class Hallazgo {
    abstract val nivel: Nivel
    abstract val mensaje: String

    protected fun Double.f1(): String = String.format(Locale.US, "%.1f", this)

    data class SuenoBajo(val promedio: Double, val referencia: Double) : Hallazgo() {
        override val nivel = Nivel.ATENCION
        override val mensaje get() =
            "Tu promedio de sueño (${promedio.f1()} h) está por debajo de la referencia general de ${referencia.f1()} h."
    }

    data class EnergiaBaja(val promedio: Double) : Hallazgo() {
        override val nivel = Nivel.ATENCION
        override val mensaje get() = "Tu nivel de energía promedio es bajo (${promedio.f1()} de 5)."
    }

    data class AnimoBajo(val promedio: Double) : Hallazgo() {
        override val nivel = Nivel.ATENCION
        override val mensaje get() = "Tu ánimo promedio es bajo (${promedio.f1()} de 5)."
    }

    /** Relación entre dos variables registradas (correlación de Pearson, |r| >= 0.5). */
    data class Relacion(val a: String, val b: String, val r: Double) : Hallazgo() {
        override val nivel = Nivel.INFO
        override val mensaje get() =
            if (r > 0) "$a y $b tienden a subir y bajar juntos (r = ${String.format(Locale.US, "%.2f", r)}). " +
                "Es una relación en tus datos, no prueba una causa."
            else "$a y $b tienden a moverse en sentido contrario (r = ${String.format(Locale.US, "%.2f", r)}). " +
                "Es una relación en tus datos, no prueba una causa."
    }

    data class CambioPeso(val deltaKg: Double) : Hallazgo() {
        override val nivel = Nivel.INFO
        override val mensaje get() =
            (if (deltaKg > 0) "Subiste " else "Bajaste ") + "${kotlin.math.abs(deltaKg).f1()} kg entre el primer y el último registro."
    }

    data class Imc(val valor: Double, val categoria: String) : Hallazgo() {
        override val nivel = if (categoria == "peso normal") Nivel.INFO else Nivel.ATENCION
        override val mensaje get() =
            "Tu IMC es ${valor.f1()} (${categoria} según la clasificación general para adultos). Consulta a un profesional para interpretarlo."
    }

    data object NotaNino : Hallazgo() {
        override val nivel = Nivel.INFO
        override val mensaje =
            "En niños el crecimiento se valora con las curvas de la OMS; revisa estos datos con su pediatra."
    }

    data object SinAlertas : Hallazgo() {
        override val nivel = Nivel.INFO
        override val mensaje = "No se detectaron señales de atención en los registros analizados."
    }
}
