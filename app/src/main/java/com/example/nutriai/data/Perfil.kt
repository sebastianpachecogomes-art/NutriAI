package com.example.nutriai.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Sujeto de monitoreo: el propio usuario o un niño/niña a su cargo (RF02).
 * Evolución de la entidad `Nino` de NutriAI: peso y altura pasaron a [RegistroDiario].
 */
@Entity(tableName = "perfilEntity")
data class Perfil(
    val nombre: String,
    val edad: Int,
    val esNino: Boolean,
    val usuarioPropietario: String,
    val fotoPath: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
