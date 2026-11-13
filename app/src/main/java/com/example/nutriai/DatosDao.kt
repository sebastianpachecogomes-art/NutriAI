package com.example.nutriai

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DatosDao {
    @Query("SELECT * FROM datosEntity")
    suspend fun getAllDatos(): MutableList<Datos>

    @Insert
    suspend fun addDatos(datos: Datos): Long
}