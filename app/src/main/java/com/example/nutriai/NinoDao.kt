package com.example.nutriai

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NinoDao {
    @Query("SELECT * FROM ninoEntity WHERE usuarioPropietario = :usuario ORDER BY nombre ASC")
    suspend fun getNinosPorUsuario(usuario: String): MutableList<Nino>

    @Insert
    suspend fun addNino(nino: Nino): Long
}
