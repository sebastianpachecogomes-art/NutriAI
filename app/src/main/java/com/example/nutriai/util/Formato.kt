package com.example.nutriai.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Las fechas se guardan en UTC (millis) y se muestran en la zona horaria del dispositivo. */
fun formatearFecha(millisUtc: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CO")).format(Date(millisUtc))

fun formatearFechaCorta(millisUtc: Long): String =
    SimpleDateFormat("dd/MM", Locale("es", "CO")).format(Date(millisUtc))

fun Double.unDecimal(): String = String.format(Locale.US, "%.1f", this)

fun Double.dosDecimales(): String = String.format(Locale.US, "%.2f", this)
