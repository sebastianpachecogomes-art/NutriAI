package com.example.nutriai

import android.app.Application
import androidx.room.Room
import com.example.nutriai.data.DatosDatabase
import com.example.nutriai.data.Repositorio

/** Estado global (singleton): inicializa Room una sola vez. Heredado de NutriAI. */
class DatosApplication : Application() {
    companion object {
        lateinit var database: DatosDatabase
        lateinit var repositorio: Repositorio
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, DatosDatabase::class.java, "DatosDatabase")
            // Solo para desarrollo: recrea la base si cambia el esquema (v2 -> v3 en el Sprint 1).
            // En producción hay que escribir una Migration real.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
        repositorio = Repositorio(database)
    }
}
