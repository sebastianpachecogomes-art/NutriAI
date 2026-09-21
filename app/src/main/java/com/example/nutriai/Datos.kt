package com.example.nutriai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "datosEntity")
data class Datos(
    val usuario: String,
    val contrasena: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)