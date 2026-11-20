package com.example.nutriai.domain

import com.example.nutriai.data.RegistroDiario
import kotlin.math.abs

/**
 * Lógica de negocio del resumen de bienestar (RF10).
 * Sustituye localmente a la vista materializada de PostgreSQL: cuando el servidor esté
 * disponible, esta misma clase puede usarse como respaldo sin conexión.
 *
 * @param minimoRegistros cantidad mínima de registros para analizar.
 */
class AnalizadorBienestar(val minimoRegistros: Int = MINIMO_POR_DEFECTO) {

    fun analizar(registros: List<RegistroDiario>, esNino: Boolean): ResumenBienestar? {
        if (registros.size < minimoRegistros) return null

        val ordenados = registros.sortedBy { it.fechaUtc }
        val sueno = ordenados.map { it.horasSueno }
        val energia = ordenados.map { it.energia.toDouble() }
        val animo = ordenados.map { it.animo.toDouble() }

        val promSueno = Estadistica.promedio(sueno)
        val promEnergia = Estadistica.promedio(energia)
        val promAnimo = Estadistica.promedio(animo)
        val rSuenoAnimo = Estadistica.correlacion(sueno, animo)
        val rSuenoEnergia = Estadistica.correlacion(sueno, energia)
        val primero = ordenados.first()
        val ultimo = ordenados.last()

        val hallazgos = buildList<Hallazgo> {
            val referenciaSueno = if (esNino) SUENO_REFERENCIA_NINO else SUENO_REFERENCIA_ADULTO
            if (promSueno < referenciaSueno) add(Hallazgo.SuenoBajo(promSueno, referenciaSueno))
            if (promEnergia < UMBRAL_BAJO) add(Hallazgo.EnergiaBaja(promEnergia))
            if (promAnimo < UMBRAL_BAJO) add(Hallazgo.AnimoBajo(promAnimo))
            if (rSuenoAnimo != null && abs(rSuenoAnimo) >= UMBRAL_CORRELACION) {
                add(Hallazgo.Relacion("Sueño", "ánimo", rSuenoAnimo))
            }
            if (rSuenoEnergia != null && abs(rSuenoEnergia) >= UMBRAL_CORRELACION) {
                add(Hallazgo.Relacion("Sueño", "energía", rSuenoEnergia))
            }
            val deltaPeso = ultimo.peso - primero.peso
            if (abs(deltaPeso) >= UMBRAL_CAMBIO_PESO) add(Hallazgo.CambioPeso(deltaPeso))
            if (esNino) {
                add(Hallazgo.NotaNino)
            } else {
                val imc = imc(ultimo.peso, ultimo.talla)
                add(Hallazgo.Imc(imc, categoriaImcAdulto(imc)))
            }
            if (none { it.nivel == Nivel.ATENCION }) add(Hallazgo.SinAlertas)
        }

        return ResumenBienestar(
            totalRegistros = ordenados.size,
            promedioSueno = promSueno,
            promedioEnergia = promEnergia,
            promedioAnimo = promAnimo,
            pesoInicial = primero.peso,
            pesoActual = ultimo.peso,
            tallaActual = ultimo.talla,
            correlacionSuenoAnimo = rSuenoAnimo,
            correlacionSuenoEnergia = rSuenoEnergia,
            hallazgos = hallazgos
        )
    }

    companion object {
        const val MINIMO_POR_DEFECTO = 5
        const val SUENO_REFERENCIA_ADULTO = 7.0
        const val SUENO_REFERENCIA_NINO = 9.0
        const val UMBRAL_BAJO = 3.0
        const val UMBRAL_CORRELACION = 0.5
        const val UMBRAL_CAMBIO_PESO = 0.5

        /** Índice de masa corporal: peso (kg) / talla (m) al cuadrado. */
        fun imc(pesoKg: Double, tallaCm: Double): Double {
            if (tallaCm <= 0.0) return 0.0
            val m = tallaCm / 100.0
            return pesoKg / (m * m)
        }

        /** Categorías generales de la OMS para adultos. */
        fun categoriaImcAdulto(imc: Double): String = when {
            imc < 18.5 -> "bajo peso"
            imc < 25.0 -> "peso normal"
            imc < 30.0 -> "sobrepeso"
            else -> "obesidad"
        }
    }
}
