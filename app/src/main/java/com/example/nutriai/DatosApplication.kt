package com.example.nutriai

import android.app.Application
import androidx.room.Room

class DatosApplication : Application() {
    companion object {
        lateinit var database: DatosDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, DatosDatabase::class.java, "DatosDatabase")
            // Agregamos una tabla nueva (Nino) sin escribir una migración real:
            // esto borra y recrea la base de datos si detecta un cambio de versión de esquema.
            // Sirve para desarrollo; en producción habría que escribir una Migration de verdad.
            .fallbackToDestructiveMigration()
            .build()
    }
}
