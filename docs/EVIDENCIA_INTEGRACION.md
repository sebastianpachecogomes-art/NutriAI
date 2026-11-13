# Evidencia de integración entre módulos – Sprint 2

## Módulos y cómo se conectan
| Origen | Destino | Punto de integración (código) |
|---|---|---|
| Registro diario (CU03) | Sincronización (CU09) | `RegistroDiario.sincronizado` + `RegistroDao.pendientesDe()` / `marcarSincronizados()` |
| Sincronización | Servidor simulado | `Repositorio.sincronizar()` → `ServicioSincronizacion.enviar()` |
| Registros de un perfil | Resumen (CU08) | `ResumenViewModel` → `AnalizadorBienestar.analizar()` |
| Resumen (CU08) | Asistente (CU05) | `AsistenteViewModel.fijarPerfil()` → `ServicioIA.responder(consulta, contexto)` |
| Recordatorio (CU06) | Registro diario | La notificación abre `MainActivity` para completar el registro |
| Todos | Dependencias | `Contenedor` + `VmFactory` |

## Flujo completo demostrable (inicio → resultado)
1. Iniciar sesión con `demo` / `demo123`.
2. Abrir el perfil «Demo Estudiante» (7 registros de prueba).
3. *Nuevo registro* con datos válidos → aparece en el historial como «Pendiente de sincronizar».
4. *Ver resumen* → promedios, correlaciones y hallazgos.
5. *Preguntar al asistente* → «¿Cómo mejoro mi sueño?» → la respuesta incluye los promedios del perfil.
6. Ajustes: activar «Simular servidor sin conexión» → *Sincronizar ahora* → mensaje de error y los pendientes no cambian.
7. Desactivar el interruptor → *Sincronizar ahora* → «Se sincronizaron 15 registros» (14 de prueba + 1 nuevo); pendientes 0 y servidor simulado 15.
8. Ajustes → Recordatorio diario → hora y minutos → *Guardar y activar* (acepta el permiso de notificaciones).

## Manejo de errores cubierto
| Situación | Comportamiento |
|---|---|
| Servidor sin conexión | Mensaje de error; los registros siguen pendientes |
| Registro inválido para el servidor | `Rechazado` con mensaje (HTTP 400 simulado) |
| Nada que sincronizar | Mensaje informativo; botón deshabilitado |
| Menos de 5 registros | El resumen explica cuántos se necesitan |
| Servicio de IA con error | Burbuja de error y se puede reintentar |
| Hora o minutos inválidos | Mensaje bajo el campo |
| Permiso de notificaciones denegado | Mensaje; no se activa el aviso |

## Verificación realizada
La lógica de dominio (`Estadistica`, `Hallazgo`, `AnalizadorBienestar`, `Validadores`, `Seguridad`) se compiló con `kotlinc 2.1.20` y se ejecutó con los datos de prueba del repositorio (12 comprobaciones, todas correctas). Resultados con los datos de prueba:

| Perfil | Sueño prom. | Energía prom. | Ánimo prom. | r sueño–ánimo | r sueño–energía |
|---|---|---|---|---|---|
| Demo Estudiante (adulto) | 6.86 h | 3.57 | 3.71 | 0.95 | 0.96 |
| Sofía (niña) | 9.86 h | 4.14 | 4.29 | 0.91 | 0.93 |

**No verificado todavía:** compilación completa con Gradle/Android Studio, ejecución en emulador, notificación real y APK. Deben comprobarse al abrir el proyecto.
