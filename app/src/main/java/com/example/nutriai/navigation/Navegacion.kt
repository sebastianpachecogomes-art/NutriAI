package com.example.nutriai.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nutriai.DatosApplication
import com.example.nutriai.ui.components.BarraInferiorNutri
import com.example.nutriai.ui.components.ItemNav
import com.example.nutriai.ui.screens.AjustesScreen
import com.example.nutriai.ui.screens.AsistenteScreen
import com.example.nutriai.ui.screens.CrearCuentaScreen
import com.example.nutriai.ui.screens.LoginScreen
import com.example.nutriai.ui.screens.NuevoPerfilScreen
import com.example.nutriai.ui.screens.NuevoRegistroScreen
import com.example.nutriai.ui.screens.RecordatorioScreen
import com.example.nutriai.ui.screens.ResumenScreen
import com.example.nutriai.ui.screens.PerfilDetalleScreen
import com.example.nutriai.ui.screens.PerfilesScreen
import com.example.nutriai.ui.viewmodel.AsistenteViewModel
import com.example.nutriai.ui.viewmodel.AuthViewModel
import com.example.nutriai.ui.viewmodel.PerfilesViewModel
import com.example.nutriai.ui.viewmodel.RecordatorioViewModel
import com.example.nutriai.ui.viewmodel.RegistroViewModel
import com.example.nutriai.ui.viewmodel.ResumenViewModel
import com.example.nutriai.ui.viewmodel.SincronizacionViewModel
import com.example.nutriai.ui.viewmodel.VmFactory

object Rutas {
    const val LOGIN = "login"
    const val CREAR_CUENTA = "crear_cuenta"
    const val PERFILES = "perfiles"
    const val ASISTENTE = "asistente"
    const val AJUSTES = "ajustes"
    const val NUEVO_PERFIL = "nuevo_perfil"
    const val PERFIL = "perfil/{perfilId}"
    const val NUEVO_REGISTRO = "nuevo_registro/{perfilId}"
    const val RESUMEN = "resumen/{perfilId}"
    const val ASISTENTE_PERFIL = "asistente/{perfilId}"
    const val RECORDATORIO = "recordatorio"

    fun perfil(id: Int) = "perfil/$id"
    fun nuevoRegistro(id: Int) = "nuevo_registro/$id"
    fun resumen(id: Int) = "resumen/$id"
    fun asistentePerfil(id: Int) = "asistente/$id"

    val publicas = setOf(LOGIN, CREAR_CUENTA)
}

private val pestanas = listOf(
    ItemNav(Rutas.PERFILES, "👶", "Perfiles"),
    ItemNav(Rutas.ASISTENTE, "💬", "Asistente"),
    ItemNav(Rutas.RECORDATORIO, "🔔", "Recordatorios"),
    ItemNav(Rutas.AJUSTES, "👤", "Cuenta")
)

/** Navegación entre las 4 pestañas principales (barra inferior de NutriAI). */
private fun irAPestana(nav: NavController, ruta: String) {
    nav.navigate(ruta) {
        popUpTo(Rutas.PERFILES) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Barra de navegación inferior (reutilizable en las pantallas principales). */
@Composable
private fun BarraInferior(nav: NavController, rutaActual: String) {
    BarraInferiorNutri(pestanas, rutaActual) { destino ->
        if (destino != rutaActual) irAPestana(nav, destino)
    }
}

@Composable
fun NutriApp() {
    val nav = rememberNavController()
    val fabrica = remember { VmFactory(DatosApplication.contenedor) }
    val auth: AuthViewModel = viewModel(factory = fabrica)
    val usuario by auth.usuario.collectAsStateWithLifecycle()
    val estadoAuth by auth.estado.collectAsStateWithLifecycle()
    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route

    // Guardia de sesión: sin usuario solo se puede estar en login / crear cuenta;
    // con usuario, login y crear cuenta redirigen a Perfiles.
    LaunchedEffect(usuario, ruta) {
        if (ruta == null) return@LaunchedEffect
        if (usuario == null && ruta !in Rutas.publicas) {
            nav.navigate(Rutas.LOGIN) {
                popUpTo(nav.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        } else if (usuario != null && ruta in Rutas.publicas) {
            nav.navigate(Rutas.PERFILES) {
                popUpTo(nav.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = nav, startDestination = Rutas.LOGIN) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                estado = estadoAuth,
                onLogin = auth::iniciarSesion,
                onIrCrearCuenta = { auth.limpiarError(); nav.navigate(Rutas.CREAR_CUENTA) },
                onLimpiarError = auth::limpiarError
            )
        }
        composable(Rutas.CREAR_CUENTA) {
            CrearCuentaScreen(
                estado = estadoAuth,
                onCrear = auth::registrar,
                onVolver = { auth.limpiarError(); nav.popBackStack() },
                onLimpiarError = auth::limpiarError
            )
        }
        composable(Rutas.PERFILES) {
            val vm: PerfilesViewModel = viewModel(factory = fabrica)
            PerfilesScreen(
                usuario = usuario.orEmpty(),
                vm = vm,
                onAbrirPerfil = { nav.navigate(Rutas.perfil(it)) },
                onNuevoPerfil = { nav.navigate(Rutas.NUEVO_PERFIL) },
                onAbrirRecordatorios = { irAPestana(nav, Rutas.RECORDATORIO) },
                onEvaluacion = { nav.navigate(Rutas.resumen(it)) },
                barraInferior = { BarraInferior(nav, Rutas.PERFILES) }
            )
        }
        composable(Rutas.ASISTENTE) {
            val vm: AsistenteViewModel = viewModel(factory = fabrica)
            AsistenteScreen(vm = vm, barraInferior = { BarraInferior(nav, Rutas.ASISTENTE) })
        }
        composable(Rutas.AJUSTES) {
            val vm: PerfilesViewModel = viewModel(factory = fabrica)
            val sync: SincronizacionViewModel = viewModel(factory = fabrica)
            AjustesScreen(
                usuario = usuario.orEmpty(),
                vm = vm,
                sync = sync,
                onCerrarSesion = auth::cerrarSesion,
                onVolverAlPanel = { irAPestana(nav, Rutas.PERFILES) },
                onAbrirPerfil = { nav.navigate(Rutas.perfil(it)) },
                barraInferior = { BarraInferior(nav, Rutas.AJUSTES) }
            )
        }
        composable(Rutas.NUEVO_PERFIL) {
            val vm: PerfilesViewModel = viewModel(factory = fabrica)
            NuevoPerfilScreen(usuario = usuario.orEmpty(), vm = vm, onAtras = { nav.popBackStack() })
        }
        composable(
            route = Rutas.PERFIL,
            arguments = listOf(navArgument("perfilId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("perfilId") ?: 0
            val vm: PerfilesViewModel = viewModel(factory = fabrica)
            PerfilDetalleScreen(
                perfilId = id,
                vm = vm,
                onAtras = { nav.popBackStack() },
                onNuevoRegistro = { nav.navigate(Rutas.nuevoRegistro(id)) },
                onResumen = { nav.navigate(Rutas.resumen(id)) },
                onAsistente = { nav.navigate(Rutas.asistentePerfil(id)) }
            )
        }
        composable(
            route = Rutas.NUEVO_REGISTRO,
            arguments = listOf(navArgument("perfilId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("perfilId") ?: 0
            val vm: RegistroViewModel = viewModel(factory = fabrica)
            NuevoRegistroScreen(perfilId = id, vm = vm, onAtras = { nav.popBackStack() })
        }
        composable(
            route = Rutas.RESUMEN,
            arguments = listOf(navArgument("perfilId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("perfilId") ?: 0
            val vm: ResumenViewModel = viewModel(factory = fabrica)
            ResumenScreen(perfilId = id, vm = vm, onAtras = { nav.popBackStack() })
        }
        composable(
            route = Rutas.ASISTENTE_PERFIL,
            arguments = listOf(navArgument("perfilId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("perfilId") ?: 0
            val vm: AsistenteViewModel = viewModel(factory = fabrica)
            AsistenteScreen(vm = vm, perfilId = id, onAtras = { nav.popBackStack() })
        }
        composable(Rutas.RECORDATORIO) {
            val vm: RecordatorioViewModel = viewModel(factory = fabrica)
            RecordatorioScreen(vm = vm, barraInferior = { BarraInferior(nav, Rutas.RECORDATORIO) })
        }
    }
}
