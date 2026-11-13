package com.example.nutriai.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/** Estados de una pantalla que carga datos: cargando / listo / error. */
sealed interface Carga<out T> {
    data object Cargando : Carga<Nothing>
    data class Listo<T>(val datos: T) : Carga<T>
    data class Error(val mensaje: String) : Carga<Nothing>
}

fun <T> Flow<T>.comoCarga(): Flow<Carga<T>> =
    map<T, Carga<T>> { Carga.Listo(it) }
        .onStart { emit(Carga.Cargando) }
        .catch { emit(Carga.Error(it.message ?: "Error desconocido")) }
