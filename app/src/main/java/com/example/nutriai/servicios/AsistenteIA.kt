package com.example.nutriai.servicios

import com.example.nutriai.domain.Nivel
import com.example.nutriai.domain.ResumenBienestar
import com.example.nutriai.util.respuestaDePrueba
import java.util.Locale
import kotlinx.coroutines.delay

/**
 * Contrato del asistente conversacional (RF08).
 *
 * PUNTO DE SUSTITUCIÓN: hoy se usa [AsistenteLocal] (respuestas por palabras clave + contexto
 * del perfil). Para usar un servicio de IA real se implementa esta interfaz llamando al servicio
 * elegido (enviando la consulta y, si se desea, el resumen) y se cambia la instancia en
 * `DatosApplication.contenedor`.
 */
interface ServicioIA {
    /** Puede lanzar una excepción si el servicio no está disponible; la UI la muestra como error. */
    suspend fun responder(consulta: String, contexto: ResumenBienestar?): String
}

class AsistenteLocal(private val latenciaMs: Long = 700L) : ServicioIA {

    override suspend fun responder(consulta: String, contexto: ResumenBienestar?): String {
        delay(latenciaMs)
        val base = respuestaDePrueba(consulta)
        val extra = contexto?.let { comentarioDeContexto(it) } ?: return base
        return "$base\n\n$extra"
    }

    private fun comentarioDeContexto(r: ResumenBienestar): String {
        val resumen = String.format(
            Locale.US,
            "Con tus últimos %d registros: sueño %.1f h, energía %.1f/5, ánimo %.1f/5.",
            r.totalRegistros, r.promedioSueno, r.promedioEnergia, r.promedioAnimo
        )
        val atencion = r.hallazgos.firstOrNull { it.nivel == Nivel.ATENCION }
        return if (atencion != null) "$resumen ${atencion.mensaje}" else resumen
    }
}
