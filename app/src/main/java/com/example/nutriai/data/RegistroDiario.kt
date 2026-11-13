package com.example.nutriai.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Registro diario integral (RF03, RF04, RF05): medidas, alimentación, sueño, energía y ánimo.
 * [fechaUtc] se guarda en milisegundos UTC (RNF06).
 */
@Entity(tableName = "registroEntity")
data class RegistroDiario(
    val perfilId: Int,
    val fechaUtc: Long,
    val peso: Double,       // kg
    val talla: Double,      // cm
    val comidas: String,
    val horasSueno: Double, // 0..24
    val energia: Int,       // 1..5
    val animo: Int,         // 1..5
    val sincronizado: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
