package com.example.nutriai

import android.app.Application
import androidx.room.Room
import com.example.nutriai.data.DatosDatabase

/** Estado global (singleton): inicializa Room y el contenedor de dependencias una sola vez. */
class DatosApplication : Application() {
    companion object {
        lateinit var database: DatosDatabase
        lateinit var contenedor: Contenedor
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, DatosDatabase::class.java, "DatosDatabase")
            // Solo para desarrollo: recrea la base si cambia el esquema.
            // En producción hay que escribir una Migration real.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
        contenedor = Contenedor(this, database)
    }
}
