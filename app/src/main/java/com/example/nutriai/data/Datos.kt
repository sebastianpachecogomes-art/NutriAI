package com.example.nutriai.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cuenta de usuario (heredada de NutriAI).
 * CAMBIO Sprint 1: la contraseña ya no se guarda en texto plano (RNF07);
 * se guarda el hash SHA-256 de (sal + contraseña) junto con la sal.
 */
@Entity(tableName = "datosEntity")
data class Datos(
    val usuario: String,
    val contrasena: String, // hash, no texto plano
    val sal: String = "",
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
