package com.example.nutriai

import android.app.Application
import androidx.room.Room

class DatosApplication : Application() {
 companion object{
     lateinit var database: DatosDatabase
 }
    override fun onCreate() {

        super.onCreate()

        database = Room.databaseBuilder(this, DatosDatabase::class.java, "DatosDatabase").build()

    }
}