package com.example.nutriai

import android.content.Context
import com.example.nutriai.data.DatosDatabase
import com.example.nutriai.data.Repositorio
import com.example.nutriai.domain.AnalizadorBienestar
import com.example.nutriai.servicios.AsistenteLocal
import com.example.nutriai.servicios.GestorRecordatorios
import com.example.nutriai.servicios.ServicioIA
import com.example.nutriai.servicios.ServidorSimulado

/**
 * Contenedor de dependencias de la app (inyección manual).
 * Aquí se cambian las implementaciones simuladas por las reales:
 *  - [servidor]: ServidorSimulado -> cliente de la API en Go (RF07)
 *  - [ia]: AsistenteLocal -> servicio de IA real (RF08)
 */
class Contenedor(contexto: Context, db: DatosDatabase) {
    val repositorio = Repositorio(db)
    val analizador = AnalizadorBienestar()
    val servidor = ServidorSimulado()
    val ia: ServicioIA = AsistenteLocal()
    val recordatorios = GestorRecordatorios(contexto.applicationContext)
}
