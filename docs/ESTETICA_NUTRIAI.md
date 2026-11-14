# Estética de NutriAI en la versión Compose

La interfaz original de NutriAI (layouts XML y drawables) se reprodujo en Compose. Todos los valores salen de los archivos originales.

| Elemento original | Archivo XML | Equivalente en Compose |
|---|---|---|
| Fondo degradado verde (#1B3D30, #4F9978, #285443) | `login_backg.xml` | `FondoDegradado` |
| Logo de 160dp y nombre de 40sp en blanco | `activity_main.xml`, `logo_nutriai.xml` | `LogoYNombre` (usa el mismo `logo_nutriai`) |
| Campos con contorno blanco y esquinas de 24dp | `rounded_corner.xml` | `CampoAuth` |
| Botón LOGIN verde #488B6D, 20sp, sombra | `activity_main.xml`, `colors.xml` | `BotonAuth` |
| Enlace blanco en negrita ("Crear una cuenta") | `activity_main.xml` | `TextoEnlace` |
| Encabezado verde #43A047 con título blanco de 20sp | `dashboard_layout.xml` | `EncabezadoVerde` / `BarraSuperior` |
| Campo de búsqueda gris #F2F2F2, esquinas de 12dp | `search_bg.xml` | `CampoBusqueda` |
| Botón de contorno verde de 1.5dp, esquinas de 20dp | `outline_pill_button.xml` | `BotonPildora` |
| Avatar circular verde con silueta blanca | `circle_mask.xml`, `ic_person_placeholder.xml` | `AvatarPerfil` |
| Fila de lista: avatar de 48dp y nombre de 18sp en negrita | `item_nino.xml` | `FilaPerfil` |
| Accesos rápidos: mosaico gris con emoji de 24sp | `dashboard_layout.xml` | `TileAcceso` |
| Barra inferior con emoji de 20sp y etiqueta de 11sp (activa en verde y negrita) | `dashboard_layout.xml` | `BarraInferiorNutri` |
| Formulario: etiqueta en negrita de 14sp y campo gris de 12dp | `agregar_nino_layout.xml` | `CampoTexto` |
| Botón verde #43A047 con texto blanco de 16sp | `agregar_nino_layout.xml` | `BotonPrincipal` |
| Textos #333333, #666666 y #9E9E9E | varios | `ColoresNutri` |

## Decisiones
- Solo hay tema claro: en la app original todos los colores están fijos y las pantallas son claras.
- Los textos de los campos de acceso se mantienen como en el original: `Username`, `Password`, `Confirm password`, `LOGIN`, `CREAR CUENTA`.
- La barra inferior pasa de 3 a 4 pestañas, como en el original: 👶 Perfiles, 💬 Asistente, 🔔 Recordatorios, 👤 Cuenta. No se implementó la pestaña "Inicio".
- «Evaluación» (📊) en los accesos rápidos abre el resumen del primer perfil de la lista.
- No se incluyó la selección de foto del avatar (ya no era parte del alcance).
- El degradado usa `verticalGradient`, una aproximación del ángulo de 100° del XML.

## Verificación
Sin emulador no se pudo comparar visualmente. Al ejecutar la app, revisar login, crear cuenta, perfiles, nuevo perfil y detalle frente a la versión original (capturas 01, 02, 03, 04 y 05).
