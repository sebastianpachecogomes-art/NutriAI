# Registro de cambios

## [0.2.0-sprint2] – Integración funcional (semana del 9 al 16 de noviembre de 2026)
Rama: `sprint-2/integracion-funcional`

### Agregado
- **Sincronización simulada (RF07, CU09):** `ServicioSincronizacion` + `ServidorSimulado` (valida como lo haría la API en Go, simula latencia y falta de conexión). `Repositorio.sincronizar()` envía los registros pendientes y marca los aceptados. Pantalla Ajustes: pendientes, recibidos por el servidor simulado, interruptor «Simular servidor sin conexión» y botón *Sincronizar ahora*.
- **Resumen de bienestar (RF10, CU08):** lógica de dominio en `domain/` (`Estadistica`, `Hallazgo`, `ResumenBienestar`, `AnalizadorBienestar`): promedios, correlación de Pearson sueño–ánimo y sueño–energía, cambio de peso, IMC (solo adultos) y hallazgos orientativos. Pantalla `ResumenScreen`.
- **Recordatorio diario (RF09, CU06):** `GestorRecordatorios` (SharedPreferences + `AlarmManager`), `ReceptorRecordatorio` (notificación), permiso `POST_NOTIFICATIONS` y pantalla `RecordatorioScreen`.
- **Asistente con contexto (RF08, CU05):** `ServicioIA` + `AsistenteLocal`. Desde el detalle de un perfil, «Preguntar al asistente» usa el resumen de ese perfil como contexto.
- `Contenedor`: inyección manual de dependencias; punto único para sustituir los servicios simulados.
- Validadores `hora` y `minuto`.
- Pruebas unitarias: `AnalizadorBienestarTest` y `ServiciosSimuladosTest`.

### Cambiado
- `DatosApplication` expone `contenedor` en lugar de `repositorio`; `VmFactory` recibe el `Contenedor`.
- `AsistenteViewModel` usa `ServicioIA` y maneja errores del servicio.
- `PerfilDetalleScreen`: botones «Ver resumen» y «Preguntar al asistente».
- `AjustesScreen`: sincronización real (simulada) y acceso al recordatorio.
- `build.gradle.kts`: `androidx.core:core-ktx`; versión `0.2.0-sprint2` (versionCode 3).

### Sin cambios
- Esquema de Room (sigue en la versión 3): no hay migración.

### Pendiente
- Sustituir `ServidorSimulado` por el cliente de la API en Go y `AsistenteLocal` por un servicio de IA real.
- Reprogramar el recordatorio tras reiniciar el dispositivo; persistir la sesión.

## [0.1.0-sprint1] – Prototipo móvil funcional
Ver `docs/BITACORA_INTEGRACION.md`.
