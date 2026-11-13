# Bitácora de integración – Sprint 1

## 1. Punto de partida
Repositorio **NutriAI** (Universidad de Cundinamarca):
- UI con **Views XML**: `MainActivity` (login), `Home` (crear cuenta), `Dashboard` (lista «Mis niños»), `AgregarNino`, `NinoAdapter` (RecyclerView).
- Room con entidades `Datos` (usuario/contraseña) y `Nino` (nombre, edad, peso, altura, foto), versión de BD 2.
- Contraseñas guardadas en **texto plano**; accesos rápidos del dashboard con «Próximamente».

La actividad pide Kotlin + **Jetpack Compose**, por lo que la capa de presentación se reconstruyó en Compose conservando la capa de datos y las ideas de pantalla.

## 2. Decisiones (según Actividad 1: conservar / integrar / modificar / descartar)

| Elemento original | Decisión | Detalle en el Sprint 1 |
|---|---|---|
| Room + Corrutinas + `DatosApplication` | Conservar | Se mantiene el singleton y el uso de `Dispatchers.IO` (ahora en `Repositorio`). |
| Login y crear cuenta (`MainActivity`, `Home`) | Conservar / modificar | Pasan a `LoginScreen` y `CrearCuentaScreen`. Validaciones más estrictas y contraseña con hash + sal (RNF07). Tras crear la cuenta se inicia sesión automáticamente. |
| Lista de niños con búsqueda (`Dashboard`, `NinoAdapter`) | Modificar | `PerfilesScreen` con búsqueda por nombre y contador de resultados. |
| Entidad `Nino` | Modificar | Evoluciona a `Perfil` (propio o niño a cargo). Peso y altura salen del perfil y pasan a `RegistroDiario`. Se quita la foto en este sprint. |
| Registro de alimentación (NutriAI) | Integrar | Campo `comidas` dentro del registro diario. |
| Bitácora de sueño, energía y ánimo (BitacoraEmo) | Integrar | Campos `horasSueno`, `energia`, `animo` del mismo registro diario (CU03). |
| Gráficas de evolución | Modificar | Gráfica de línea simple (peso o talla) con Canvas; sin curvas OMS. |
| Asistente de IA | Modificar | Chat con respuestas locales de prueba y aviso legal fijo (RNF03). |
| Accesos «Próximamente» | Descartar | Se reemplazan por navegación real. Recordatorios queda para otro sprint. |
| Backend Go + PostgreSQL, WakaTime, entorno físico | Fuera del Sprint 1 | Sincronización prevista para el Sprint 2; WakaTime y entorno físico descartados por ahora. |

## 3. Cambios técnicos
- **Build:** se añadieron Compose (BOM), Navigation Compose, Activity Compose, Lifecycle (ViewModel/runtime Compose) y el plugin `kotlin-compose`. Se retiraron AppCompat, ConstraintLayout y Material Views. Se renombró el proyecto a `BitacoraVita` y el nombre visible de la app.
- **Base de datos:** versión 3 con `Datos`, `Perfil` y `RegistroDiario`. Se usa `fallbackToDestructiveMigration` (solo desarrollo): al actualizar desde NutriAI se pierden los datos locales.
- **Arquitectura:** `Repositorio` → ViewModels (`StateFlow`) → pantallas Compose. Estados `Carga` y `Guardado`.
- **Navegación:** `NavHost` con guardia de sesión: sin usuario solo se accede a login/crear cuenta; con usuario esas pantallas redirigen a Perfiles.
- **Fechas:** guardadas en UTC (millis) y mostradas en la zona del dispositivo (RNF06).
- **Pruebas:** `ValidadoresTest` (validaciones y hash). Se eliminó `ExampleUnitTest`.

## 4. Trazabilidad con requerimientos (Actividad 2)
| RF | Estado Sprint 1 |
|---|---|
| RF01 Registro e inicio de sesión | Implementado |
| RF02 Gestión de perfiles | Implementado |
| RF03 Peso y talla | Implementado |
| RF04 Alimentación | Implementado |
| RF05 Sueño, energía y ánimo | Implementado |
| RF06 Historial y gráfica | Implementado |
| RF07 Sincronización | Pendiente (indicador de registros pendientes y botón deshabilitado) |
| RF08 Asistente de IA | Parcial (respuestas locales de prueba) |
| RF09 Recordatorio | Pendiente |
| RF10 Resumen de correlación | Pendiente |
| RF11 Ajustes y cierre de sesión | Implementado (sin sincronización real) |

## 5. Estado de verificación (leer antes de entregar)
- **El código se escribió sin poder compilarlo** en el entorno donde se generó (no había Android SDK ni Gradle). **Aún no se ha ejecutado en emulador, no se ha generado el APK ni se tomaron capturas.**
- Lo primero que debe hacer el equipo: abrir el proyecto, hacer *Gradle Sync*, compilar, ejecutar y corregir lo que aparezca (errores de import o de versión de dependencias son lo más probable).
- Versiones heredadas de NutriAI (AGP 9.4.1, Kotlin 2.3.21, KSP 2.3.12, Room 2.8.5). Las de Compose BOM (2025.06.01), Navigation (2.9.0), Activity Compose (1.10.1) y Lifecycle (2.9.0) se eligieron por compatibilidad conocida; si Android Studio sugiere versiones más recientes, pueden actualizarse en `gradle/libs.versions.toml`.
- Sin ejecutar: la sesión vive en memoria (al cerrar la app hay que iniciar sesión de nuevo); los textos están en el código y no en `strings.xml`.

## 6. Registro de trabajo (completar por el equipo)
| Fecha | Responsable | Actividad | Resultado / observaciones |
|---|---|---|---|
| | | Importar base NutriAI al repositorio | Commit inicial en `main` |
| | | Reconstruir UI en Compose y navegación | Rama `sprint-1/prototipo-compose` |
| | | Compilar, ejecutar en emulador y corregir errores | |
| | | Tomar capturas (ver `PRUEBAS_Y_CAPTURAS.md`) | |
| | | Generar APK debug y adjuntarlo | |

## 7. Pendiente para el Sprint 2
Sincronización con la API en Go (RF07), recordatorios (RF09), resumen de correlación (RF10), servicio de IA real (RF08), persistencia de sesión, migración real de Room y mover textos a `strings.xml`.

## 8. Sprint 2 – Integración funcional
Ver `CHANGELOG.md` (cambios) y `docs/EVIDENCIA_INTEGRACION.md` (integración entre módulos).

| Decisión | Motivo |
|---|---|
| Sincronización simulada tras la interfaz `ServicioSincronizacion` | La API en Go aún no está disponible; el punto de sustitución queda documentado. |
| Resumen calculado en el dispositivo (`AnalizadorBienestar`) | Equivale a la vista materializada del servidor y funciona sin conexión. |
| Asistente local tras `ServicioIA` | Evita depender de un servicio externo y de sus cuotas. |
| Alarma inexacta (`setInexactRepeating`) | No requiere el permiso de alarmas exactas; la hora puede variar unos minutos. |
| IMC solo para adultos | En niños el IMC se interpreta con curvas OMS, que no están implementadas. |
