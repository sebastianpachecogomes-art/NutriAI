package com.example.nutriai.util

/**
 * Validaciones básicas de formularios. Cada función devuelve el mensaje de error
 * o `null` si el valor es válido. Son funciones puras: se prueban con JUnit sin emulador.
 */
object Validadores {

    /** Acepta coma o punto decimal. */
    fun aDouble(texto: String): Double? = texto.trim().replace(',', '.').toDoubleOrNull()

    fun usuario(v: String): String? {
        val t = v.trim()
        return when {
            t.isBlank() -> "Escribe un nombre de usuario"
            t.length < 3 -> "Mínimo 3 caracteres"
            !t.all { it.isLetterOrDigit() || it == '_' } -> "Solo letras, números y guion bajo"
            else -> null
        }
    }

    fun contrasena(v: String): String? = when {
        v.isBlank() -> "Escribe una contraseña"
        v.length < 6 -> "Mínimo 6 caracteres"
        else -> null
    }

    fun confirmacion(contrasena: String, confirmacion: String): String? =
        if (contrasena != confirmacion) "Las contraseñas no coinciden" else null

    fun nombrePerfil(v: String): String? = when {
        v.isBlank() -> "Escribe un nombre"
        v.trim().length > 40 -> "Máximo 40 caracteres"
        else -> null
    }

    fun edad(v: String): String? {
        val n = v.trim().toIntOrNull() ?: return "Escribe un número entero"
        return if (n !in 0..120) "La edad debe estar entre 0 y 120" else null
    }

    fun peso(v: String): String? {
        val n = aDouble(v) ?: return "Escribe un número"
        return if (n <= 0.0 || n > 300.0) "El peso debe ser mayor que 0 y hasta 300 kg" else null
    }

    fun talla(v: String): String? {
        val n = aDouble(v) ?: return "Escribe un número"
        return if (n < 30.0 || n > 250.0) "La talla debe estar entre 30 y 250 cm" else null
    }

    fun comidas(v: String): String? = if (v.isBlank()) "Anota al menos un alimento" else null

    fun horasSueno(v: String): String? {
        val n = aDouble(v) ?: return "Escribe un número"
        return if (n < 0.0 || n > 24.0) "Las horas deben estar entre 0 y 24" else null
    }

    fun hora(v: String): String? {
        val n = v.trim().toIntOrNull() ?: return "Escribe un número entero"
        return if (n !in 0..23) "La hora debe estar entre 0 y 23" else null
    }

    fun minuto(v: String): String? {
        val n = v.trim().toIntOrNull() ?: return "Escribe un número entero"
        return if (n !in 0..59) "Los minutos deben estar entre 0 y 59" else null
    }
}
