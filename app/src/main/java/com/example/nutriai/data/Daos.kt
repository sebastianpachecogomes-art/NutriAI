package com.example.nutriai.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DatosDao {
    @Query("SELECT * FROM datosEntity WHERE usuario = :usuario LIMIT 1")
    suspend fun buscarPorUsuario(usuario: String): Datos?

    @Query("SELECT COUNT(*) FROM datosEntity")
    suspend fun contar(): Int

    @Insert
    suspend fun addDatos(datos: Datos): Long
}

@Dao
interface PerfilDao {
    @Query("SELECT * FROM perfilEntity WHERE usuarioPropietario = :usuario ORDER BY nombre ASC")
    fun observarPorUsuario(usuario: String): Flow<List<Perfil>>

    @Query("SELECT * FROM perfilEntity WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Perfil?

    @Insert
    suspend fun addPerfil(perfil: Perfil): Long
}

@Dao
interface RegistroDao {
    @Query("SELECT * FROM registroEntity WHERE perfilId = :perfilId ORDER BY fechaUtc DESC")
    fun observarPorPerfil(perfilId: Int): Flow<List<RegistroDiario>>

    @Query(
        "SELECT COUNT(*) FROM registroEntity r INNER JOIN perfilEntity p ON r.perfilId = p.id " +
            "WHERE p.usuarioPropietario = :usuario AND r.sincronizado = 0"
    )
    fun observarPendientes(usuario: String): Flow<Int>

    @Insert
    suspend fun addRegistro(registro: RegistroDiario): Long
}
