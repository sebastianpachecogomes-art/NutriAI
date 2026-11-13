package com.example.nutriai.ui

/** Estado de una operación de guardado iniciada desde un formulario. */
sealed interface Guardado {
    data object Inactivo : Guardado
    data object Guardando : Guardado
    data object Exito : Guardado
    data class Error(val mensaje: String) : Guardado
}
