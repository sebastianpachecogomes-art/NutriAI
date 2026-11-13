package com.example.nutriai

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Datos::class, Nino::class], version = 2)
abstract class DatosDatabase : RoomDatabase() {
    abstract fun datosDao(): DatosDao
    abstract fun ninoDao(): NinoDao
}
