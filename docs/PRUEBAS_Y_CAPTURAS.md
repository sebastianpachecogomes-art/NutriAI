# Pruebas manuales y capturas – Sprint 1

Usuario de prueba: `demo` / `demo123` (se crea solo en el primer arranque).

## Casos de prueba manuales
| ID | Caso | Pasos | Resultado esperado | OK |
|---|---|---|---|---|
| P01 | Login correcto | Entrar con demo / demo123 | Abre «Mis perfiles» con 2 perfiles | ☐ |
| P02 | Login con campos vacíos | Pulsar «Iniciar sesión» sin datos | Mensajes bajo cada campo; no entra | ☐ |
| P03 | Login incorrecto | demo / mala | Mensaje «Usuario o contraseña incorrectos» | ☐ |
| P04 | Crear cuenta válida | Usuario nuevo, clave de 6+ y confirmación igual | Entra a «Mis perfiles» (vacío) | ☐ |
| P05 | Crear cuenta duplicada | Repetir `demo` | Mensaje «Ese nombre de usuario ya está registrado» | ☐ |
| P06 | Contraseñas distintas | Confirmación diferente | Error «Las contraseñas no coinciden» | ☐ |
| P07 | Buscar perfil | Escribir «Sof» | 1 resultado (Sofía) | ☐ |
| P08 | Crear perfil | + → nombre y edad válidos → Guardar | Vuelve a la lista con el perfil nuevo | ☐ |
| P09 | Ver historial | Abrir «Sofía» | Último registro, gráfica y 7 tarjetas | ☐ |
| P10 | Cambiar gráfica | Chip «Talla» | La gráfica cambia | ☐ |
| P11 | Registro válido | «Nuevo registro» con datos válidos | Regresa al historial con una tarjeta más, «Pendiente de sincronizar» | ☐ |
| P12 | Registro inválido | Peso 0, talla 10, horas 30, comidas vacías | Errores en cada campo; no guarda | ☐ |
| P13 | Asistente | Escribir «¿Cómo mejoro mi sueño?» | Responde tras ~1 s; aviso legal siempre visible | ☐ |
| P14 | Ajustes | Abrir pestaña Ajustes | Muestra usuario y registros pendientes (suben tras P11) | ☐ |
| P15 | Cerrar sesión | Ajustes → Cerrar sesión | Vuelve al login; el botón atrás no regresa a pantallas internas | ☐ |
| P16 | Sin conexión | Activar modo avión y repetir P11 | Funciona igual (offline-first) | ☐ |
| P17 | Resumen | Perfil «Demo Estudiante» → Ver resumen | Promedios ≈ 6.9 h / 3.6 / 3.7; r ≈ 0.95 y 0.96; hallazgos | ☐ |
| P18 | Resumen con pocos registros | Perfil nuevo con 1 registro → Ver resumen | Mensaje «Se necesitan al menos 5 registros» | ☐ |
| P19 | Asistente con contexto | Detalle → Preguntar al asistente → «sueño» | Respuesta con los promedios del perfil | ☐ |
| P20 | Sincronizar sin conexión | Ajustes: interruptor ON → Sincronizar | Mensaje de error; pendientes sin cambio | ☐ |
| P21 | Sincronizar con conexión | Interruptor OFF → Sincronizar | «Se sincronizaron N registros»; pendientes 0 | ☐ |
| P22 | Recordatorio válido | Hora 20, minutos 0 → Guardar y activar | Estado activo; notificación a la hora (puede tardar: alarma inexacta) | ☐ |
| P23 | Recordatorio inválido | Hora 25 | Error bajo el campo | ☐ |

## Capturas a tomar (guardar en `docs/capturas/`)
1. `01_login.png` – pantalla de login
2. `02_crear_cuenta_errores.png` – validaciones visibles
3. `03_perfiles.png` – lista de perfiles
4. `04_nuevo_perfil.png`
5. `05_detalle_grafica.png` – historial y gráfica
6. `06_nuevo_registro.png`
7. `07_nuevo_registro_errores.png`
8. `08_asistente.png` – con aviso legal
9. `09_ajustes.png`
10. `10_resumen.png`
11. `11_asistente_contexto.png`
12. `12_sincronizacion_error.png`
13. `13_sincronizacion_exito.png`
14. `14_recordatorio.png`

En Android Studio: Running Devices ▸ icono de cámara; o `adb exec-out screencap -p > captura.png`.

## Pruebas unitarias
`./gradlew testDebugUnitTest` – ejecuta `ValidadoresTest`, `AnalizadorBienestarTest` y `ServiciosSimuladosTest`.
