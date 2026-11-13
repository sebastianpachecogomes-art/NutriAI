# BitácoraVita – Prototipo móvil (Sprint 1)

Proyecto de Internacionalización UNIVA – Universidad de Cundinamarca.
Base: **NutriAI** (Universidad de Cundinamarca), integrando el registro emocional y de sueño de **BitacoraEmo** (UNIVA).

App Android nativa en **Kotlin + Jetpack Compose** con persistencia local en **Room** (offline-first).

## Qué incluye el Sprint 1
- Navegación con Navigation Compose: login, crear cuenta, perfiles, detalle de perfil, nuevo perfil, nuevo registro, asistente y ajustes (barra inferior en las 3 pantallas principales).
- **Flujo principal (MVP):** iniciar sesión → elegir perfil → *Nuevo registro* (peso, talla, comidas, sueño, energía y ánimo) → ver historial y gráfica de evolución.
- Validaciones de formularios (`util/Validadores.kt`, con pruebas unitarias).
- Manejo de estados: `Carga` (cargando / listo / error), `Guardado`, `StateFlow` en ViewModels.
- Componentes reutilizables (`ui/components/Componentes.kt`).
- Datos de prueba: al primer arranque se crea el usuario **demo / demo123** con dos perfiles y 7 días de registros.
- Asistente con respuestas locales de prueba y aviso médico-legal visible (no es IA real todavía).

## Fuera del Sprint 1 (pendiente)
Sincronización con el backend Go + PostgreSQL, recordatorios, resumen de correlación, servicio de IA real.

## Cómo ejecutar
1. Abrir la carpeta en **Android Studio** (versión compatible con AGP 9.4.1) y esperar el *Gradle Sync*.
2. Elegir un emulador o dispositivo (Android 7.0 / API 24 o superior).
3. Pulsar **Run ▶**. Iniciar sesión con `demo` / `demo123`.

### Generar el APK de prueba
*Build ▸ Build Bundle(s) / APK(s) ▸ Build APK(s)*, o por terminal:

```
./gradlew assembleDebug
```
El archivo queda en `app/build/outputs/apk/debug/app-debug.apk`.

### Pruebas unitarias
```
./gradlew testDebugUnitTest
```

## Estructura
```
app/src/main/java/com/example/nutriai/
  MainActivity.kt, DatosApplication.kt
  data/        entidades Room, DAOs, base de datos y Repositorio
  navigation/  rutas y NavHost (Navegacion.kt)
  ui/          theme, components, screens, viewmodel, estados (Carga, Guardado)
  util/        Validadores, Seguridad, Formato, RespuestasAsistente
docs/          bitácora de integración y guía de pruebas/capturas
```

## Documentación
- `docs/BITACORA_INTEGRACION.md` – decisiones y cambios de la integración.
- `docs/PRUEBAS_Y_CAPTURAS.md` – casos de prueba manuales y lista de capturas a tomar.
