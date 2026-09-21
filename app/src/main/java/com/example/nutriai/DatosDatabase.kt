package com.example.nutriai

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Datos::class], version = 1)
abstract class DatosDatabase : RoomDatabase() {
    abstract fun datosDao(): DatosDao
}