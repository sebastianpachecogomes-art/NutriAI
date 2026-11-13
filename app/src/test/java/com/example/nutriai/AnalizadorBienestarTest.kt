package com.example.nutriai

import com.example.nutriai.data.RegistroDiario
import com.example.nutriai.domain.AnalizadorBienestar
import com.example.nutriai.domain.Estadistica
import com.example.nutriai.domain.Hallazgo
import com.example.nutriai.domain.Nivel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalizadorBienestarTest {

    private fun reg(i: Int, peso: Double, talla: Double, sueno: Double, energia: Int, animo: Int) =
        RegistroDiario(perfilId = 1, fechaUtc = 1_000_000L * i, peso = peso, talla = talla,
            comidas = "x", horasSueno = sueno, energia = energia, animo = animo)

    private val adulto = (0 until 7).map {
        reg(it, 68.0 + it * 0.15, 172.0,
            listOf(6.0, 7.5, 5.5, 8.0, 6.5, 7.0, 7.5)[it],
            listOf(3, 4, 2, 5, 3, 4, 4)[it],
            listOf(3, 4, 2, 5, 3, 4, 5)[it])
    }

    @Test fun menos_registros_que_el_minimo_devuelve_null() {
        assertNull(AnalizadorBienestar().analizar(adulto.take(4), esNino = false))
    }

    @Test fun calcula_promedios_y_correlaciones_con_datos_de_prueba() {
        val r = AnalizadorBienestar().analizar(adulto, esNino = false)!!
        assertEquals(7, r.totalRegistros)
        assertEquals(6.857, r.promedioSueno, 0.001)
        assertEquals(3.571, r.promedioEnergia, 0.001)
        assertEquals(0.951, r.correlacionSuenoAnimo!!, 0.001)
        assertEquals(0.963, r.correlacionSuenoEnergia!!, 0.001)
    }

    @Test fun adulto_recibe_imc_y_alerta_de_sueno() {
        val r = AnalizadorBienestar().analizar(adulto, esNino = false)!!
        assertTrue(r.hallazgos.any { it is Hallazgo.Imc })
        assertTrue(r.hallazgos.any { it is Hallazgo.SuenoBajo })
        assertTrue(r.hallazgos.none { it is Hallazgo.NotaNino })
    }

    @Test fun nino_no_recibe_imc_sino_nota_pediatrica() {
        val r = AnalizadorBienestar().analizar(adulto, esNino = true)!!
        assertTrue(r.hallazgos.none { it is Hallazgo.Imc })
        assertTrue(r.hallazgos.any { it is Hallazgo.NotaNino })
    }

    @Test fun valores_bajos_generan_hallazgos_de_atencion() {
        val malos = (0 until 6).map { reg(it, 80.0, 170.0, 5.0, 2, 2) }
        val r = AnalizadorBienestar().analizar(malos, esNino = false)!!
        assertTrue(r.hallazgos.count { it.nivel == Nivel.ATENCION } >= 3)
    }

    @Test fun categorias_de_imc() {
        assertEquals("bajo peso", AnalizadorBienestar.categoriaImcAdulto(17.0))
        assertEquals("peso normal", AnalizadorBienestar.categoriaImcAdulto(22.0))
        assertEquals("sobrepeso", AnalizadorBienestar.categoriaImcAdulto(27.0))
        assertEquals("obesidad", AnalizadorBienestar.categoriaImcAdulto(31.0))
        assertEquals(22.857, AnalizadorBienestar.imc(70.0, 175.0), 0.001)
    }

    @Test fun correlacion_de_pearson() {
        assertEquals(1.0, Estadistica.correlacion(listOf(1.0, 2.0, 3.0), listOf(2.0, 4.0, 6.0))!!, 1e-9)
        assertEquals(-1.0, Estadistica.correlacion(listOf(1.0, 2.0, 3.0), listOf(6.0, 4.0, 2.0))!!, 1e-9)
        assertNull(Estadistica.correlacion(listOf(1.0, 1.0, 1.0), listOf(1.0, 2.0, 3.0)))
        assertNull(Estadistica.correlacion(listOf(1.0, 2.0), listOf(1.0, 2.0)))
        assertNotNull(Estadistica.correlacion(listOf(1.0, 2.0, 4.0), listOf(1.0, 3.0, 2.0)))
    }
}
