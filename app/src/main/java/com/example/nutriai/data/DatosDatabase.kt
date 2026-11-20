package com.example.nutriai.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Datos::class, Perfil::class, RegistroDiario::class],
    version = 4,
    exportSchema = false
)
abstract class DatosDatabase : RoomDatabase() {
    abstract fun datosDao(): DatosDao
    abstract fun perfilDao(): PerfilDao
    abstract fun registroDao(): RegistroDao
}
