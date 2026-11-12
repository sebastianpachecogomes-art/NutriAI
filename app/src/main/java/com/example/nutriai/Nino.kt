package com.example.nutriai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ninoEntity")
data class Nino(
    val nombre: String,
    val edad: Int,
    val peso: Double,
    val altura: Double,
    val fotoPath: String?,
    val usuarioPropietario: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
