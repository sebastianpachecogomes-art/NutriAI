package com.example.nutriai.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale

/** Hash de contraseñas con sal (RNF07). Suficiente para el prototipo local. */
object Seguridad {
    fun generarSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    fun hash(contrasena: String, sal: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest((sal + contrasena).toByteArray(Charsets.UTF_8)).toHex()
    }

    private fun ByteArray.toHex(): String = joinToString("") { String.format(Locale.US, "%02x", it) }
}
