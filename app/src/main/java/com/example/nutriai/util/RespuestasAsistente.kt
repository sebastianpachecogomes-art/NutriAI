package com.example.nutriai.util

/**
 * Respuestas locales de prueba del asistente (Sprint 1). NO es IA real:
 * el servicio de inferencia se integra en un sprint posterior (RF08).
 * Todas las respuestas son orientativas y no sustituyen una consulta profesional (RNF03).
 */
fun respuestaDePrueba(consulta: String): String {
    val c = consulta.lowercase()
    return when {
        "sueño" in c || "dormir" in c || "descans" in c ->
            "Mantener horarios regulares para dormir y evitar pantallas antes de acostarte suele ayudar al descanso. " +
                "Si el cansancio persiste, consulta con un profesional de la salud."
        "agua" in c || "hidrat" in c ->
            "Beber agua a lo largo del día ayuda a la concentración y la energía. " +
                "La cantidad adecuada depende de la edad y la actividad de cada persona."
        "cansad" in c || "fatiga" in c || "energía" in c || "energia" in c ->
            "La fatiga puede relacionarse con el sueño, la alimentación y la carga de estudio o trabajo. " +
                "Revisa tu historial de registros para ver si hay un patrón."
        "comida" in c || "aliment" in c || "dieta" in c || "desayuno" in c ->
            "Una alimentación variada, con frutas, verduras, proteínas y cereales, apoya el rendimiento diario. " +
                "Para un plan personalizado, consulta a un nutricionista."
        "niño" in c || "niña" in c || "peso" in c || "talla" in c ->
            "El crecimiento de cada niño es distinto; lo mejor es comparar su evolución en el tiempo " +
                "y revisarla con su pediatra."
        else ->
            "Gracias por tu consulta. Por ahora puedo orientarte sobre sueño, alimentación, hidratación y energía. " +
                "Esta es una respuesta de prueba, no un diagnóstico."
    }
}
