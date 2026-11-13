package com.example.nutriai.data

import com.example.nutriai.servicios.ResultadoSync
import com.example.nutriai.servicios.ServicioSincronizacion
import com.example.nutriai.util.Seguridad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Único punto de acceso a datos para los ViewModels.
 * Todas las operaciones suspendidas se ejecutan en Dispatchers.IO (RNF02).
 */
class Repositorio(private val db: DatosDatabase) {

    // ---------- Cuentas (RF01) ----------
    suspend fun autenticar(usuario: String, contrasena: String): Boolean = withContext(Dispatchers.IO) {
        val cuenta = db.datosDao().buscarPorUsuario(usuario) ?: return@withContext false
        cuenta.contrasena == Seguridad.hash(contrasena, cuenta.sal)
    }

    /** @return true si se creó la cuenta, false si el usuario ya existía. */
    suspend fun registrarCuenta(usuario: String, contrasena: String): Boolean = withContext(Dispatchers.IO) {
        if (db.datosDao().buscarPorUsuario(usuario) != null) return@withContext false
        val sal = Seguridad.generarSal()
        db.datosDao().addDatos(Datos(usuario = usuario, contrasena = Seguridad.hash(contrasena, sal), sal = sal))
        true
    }

    // ---------- Perfiles (RF02) ----------
    fun perfiles(usuario: String): Flow<List<Perfil>> = db.perfilDao().observarPorUsuario(usuario)

    suspend fun perfil(id: Int): Perfil? = withContext(Dispatchers.IO) { db.perfilDao().buscarPorId(id) }

    suspend fun crearPerfil(perfil: Perfil): Long = withContext(Dispatchers.IO) { db.perfilDao().addPerfil(perfil) }

    // ---------- Registros diarios (RF03-RF06) ----------
    fun registros(perfilId: Int): Flow<List<RegistroDiario>> = db.registroDao().observarPorPerfil(perfilId)

    fun pendientes(usuario: String): Flow<Int> = db.registroDao().observarPendientes(usuario)

    suspend fun guardarRegistro(registro: RegistroDiario): Long =
        withContext(Dispatchers.IO) { db.registroDao().addRegistro(registro) }

    // ---------- Sincronización (RF07, Sprint 2) ----------
    /** Envía al servicio los registros pendientes del usuario y marca como sincronizados los aceptados. */
    suspend fun sincronizar(usuario: String, servicio: ServicioSincronizacion): ResultadoSync =
        withContext(Dispatchers.IO) {
            val pendientes = db.registroDao().pendientesDe(usuario)
            if (pendientes.isEmpty()) return@withContext ResultadoSync.NadaPendiente
            val resultado = servicio.enviar(pendientes)
            if (resultado is ResultadoSync.Aceptado) {
                resultado.ids.chunked(500).forEach { db.registroDao().marcarSincronizados(it) }
            }
            resultado
        }

    // ---------- Datos de prueba (Sprint 1) ----------
    /** Si la base está vacía crea el usuario demo / demo123 con dos perfiles y una semana de registros. */
    suspend fun sembrarDatosDePrueba() = withContext(Dispatchers.IO) {
        if (db.datosDao().contar() > 0) return@withContext
        val sal = Seguridad.generarSal()
        db.datosDao().addDatos(Datos("demo", Seguridad.hash("demo123", sal), sal))

        val adulto = db.perfilDao().addPerfil(Perfil("Demo Estudiante", 21, false, "demo")).toInt()
        val nina = db.perfilDao().addPerfil(Perfil("Sofía", 6, true, "demo")).toInt()

        val dia = 24L * 60 * 60 * 1000
        val ahora = System.currentTimeMillis()
        val comidasAdulto = listOf(
            "Huevos con arepa, almuerzo con pollo y arroz, cena ligera",
            "Avena con fruta, bandeja paisa, sándwich",
            "Café y pan, pasta con ensalada, yogur",
            "Desayuno completo, sopa y pescado, fruta",
            "Cereal, hamburguesa, cena tardía",
            "Tostadas con aguacate, arroz con lentejas, tortilla",
            "Calentado, pollo con verduras, avena"
        )
        val comidasNina = listOf(
            "Leche con cereal, sopa y arroz, fruta",
            "Huevo con pan, pasta, yogur",
            "Avena, pollo con papa, galletas",
            "Arepa con queso, lentejas, banano",
            "Leche y pan, arroz con carne, manzana",
            "Cereal, sopa de verduras, gelatina",
            "Huevo revuelto, pescado con arroz, fruta"
        )
        for (i in 0 until 7) {
            val fecha = ahora - (6 - i) * dia
            db.registroDao().addRegistro(
                RegistroDiario(
                    perfilId = adulto, fechaUtc = fecha,
                    peso = 68.0 + i * 0.15, talla = 172.0,
                    comidas = comidasAdulto[i],
                    horasSueno = listOf(6.0, 7.5, 5.5, 8.0, 6.5, 7.0, 7.5)[i],
                    energia = listOf(3, 4, 2, 5, 3, 4, 4)[i],
                    animo = listOf(3, 4, 2, 5, 3, 4, 5)[i]
                )
            )
            db.registroDao().addRegistro(
                RegistroDiario(
                    perfilId = nina, fechaUtc = fecha,
                    peso = 20.0 + i * 0.1, talla = 113.0 + i * 0.1,
                    comidas = comidasNina[i],
                    horasSueno = listOf(10.0, 9.5, 10.5, 9.0, 10.0, 10.5, 9.5)[i],
                    energia = listOf(4, 4, 5, 3, 4, 5, 4)[i],
                    animo = listOf(5, 4, 5, 3, 4, 5, 4)[i]
                )
            )
        }
    }
}
