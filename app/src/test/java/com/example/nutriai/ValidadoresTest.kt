package com.example.nutriai

import com.example.nutriai.util.Seguridad
import com.example.nutriai.util.Validadores
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadoresTest {

    @Test fun usuario_vacio_o_corto_es_invalido() {
        assertNotNull(Validadores.usuario(""))
        assertNotNull(Validadores.usuario("ab"))
        assertNotNull(Validadores.usuario("a b c"))
        assertNull(Validadores.usuario("demo_1"))
    }

    @Test fun contrasena_minimo_seis_caracteres() {
        assertNotNull(Validadores.contrasena("12345"))
        assertNull(Validadores.contrasena("123456"))
    }

    @Test fun confirmacion_debe_coincidir() {
        assertNotNull(Validadores.confirmacion("abc123", "abc124"))
        assertNull(Validadores.confirmacion("abc123", "abc123"))
    }

    @Test fun peso_rechaza_cero_negativo_y_texto() {
        assertNotNull(Validadores.peso("0"))
        assertNotNull(Validadores.peso("-5"))
        assertNotNull(Validadores.peso("abc"))
        assertNull(Validadores.peso("68,5")) // acepta coma decimal
        assertNull(Validadores.peso("20"))
    }

    @Test fun talla_en_rango() {
        assertNotNull(Validadores.talla("10"))
        assertNotNull(Validadores.talla("300"))
        assertNull(Validadores.talla("172"))
    }

    @Test fun horas_de_sueno_entre_0_y_24() {
        assertNotNull(Validadores.horasSueno("25"))
        assertNotNull(Validadores.horasSueno("-1"))
        assertNull(Validadores.horasSueno("7.5"))
    }

    @Test fun edad_entera_entre_0_y_120() {
        assertNotNull(Validadores.edad("abc"))
        assertNotNull(Validadores.edad("121"))
        assertNull(Validadores.edad("6"))
    }

    @Test fun comidas_no_vacias() {
        assertNotNull(Validadores.comidas("   "))
        assertNull(Validadores.comidas("arroz"))
    }

    @Test fun hash_es_determinista_y_depende_de_la_sal() {
        assertEquals(Seguridad.hash("demo123", "s1"), Seguridad.hash("demo123", "s1"))
        assertNotEquals(Seguridad.hash("demo123", "s1"), Seguridad.hash("demo123", "s2"))
        assertNotEquals("demo123", Seguridad.hash("demo123", "s1"))
    }
}
