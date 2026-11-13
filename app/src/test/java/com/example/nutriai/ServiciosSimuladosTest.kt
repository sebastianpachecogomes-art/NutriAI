package com.example.nutriai

import com.example.nutriai.data.RegistroDiario
import com.example.nutriai.servicios.AsistenteLocal
import com.example.nutriai.servicios.ResultadoSync
import com.example.nutriai.servicios.ServidorSimulado
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiciosSimuladosTest {

    private fun reg(id: Int, peso: Double = 70.0) = RegistroDiario(
        perfilId = 1, fechaUtc = 0L, peso = peso, talla = 170.0, comidas = "arroz",
        horasSueno = 7.0, energia = 3, animo = 3, id = id
    )

    @Test fun servidor_acepta_lote_valido_y_cuenta_recibidos() = runBlocking {
        val servidor = ServidorSimulado(latenciaMs = 0)
        val r = servidor.enviar(listOf(reg(1), reg(2)))
        assertTrue(r is ResultadoSync.Aceptado)
        assertEquals(listOf(1, 2), (r as ResultadoSync.Aceptado).ids)
        assertEquals(2, servidor.recibidos.value)
    }

    @Test fun servidor_sin_conexion_no_recibe_nada() = runBlocking {
        val servidor = ServidorSimulado(latenciaMs = 0)
        servidor.simularSinConexion(true)
        assertEquals(ResultadoSync.SinConexion, servidor.enviar(listOf(reg(1))))
        assertEquals(0, servidor.recibidos.value)
    }

    @Test fun servidor_rechaza_registro_invalido() = runBlocking {
        val servidor = ServidorSimulado(latenciaMs = 0)
        val r = servidor.enviar(listOf(reg(1), reg(2, peso = -5.0)))
        assertTrue(r is ResultadoSync.Rechazado)
        assertEquals(0, servidor.recibidos.value)
    }

    @Test fun asistente_responde_segun_palabra_clave() = runBlocking {
        val ia = AsistenteLocal(latenciaMs = 0)
        assertTrue(ia.responder("¿Cómo mejoro mi sueño?", null).contains("dormir"))
    }
}
