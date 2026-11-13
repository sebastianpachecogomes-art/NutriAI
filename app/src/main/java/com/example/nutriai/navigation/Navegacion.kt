package com.example.nutriai.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.nutriai.ui.screens.AjustesScreen
import com.example.nutriai.ui.screens.AsistenteScreen
import com.example.nutriai.ui.screens.CrearCuentaScreen
import com.example.nutriai.ui.screens.LoginScreen
import com.example.nutriai.ui.screens.NuevoPerfilScreen
import com.example.nutriai.ui.screens.NuevoRegistroScreen
import com.example.nutriai.ui.screens.PerfilDetalleScreen
import com.example.nutriai.ui.screens.PerfilesScreen
import com.example.nutriai.ui.viewmodel.AsistenteViewModel
import com.example.nutriai.ui.viewmodel.AuthViewModel
import com.example.nutriai.ui.viewmodel.PerfilesViewModel
import com.example.nutriai.ui.viewmodel.RegistroViewModel
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

    fun perfil(id: Int) = "perfil/$id"
    fun nuevoRegistro(id: Int) = "nuevo_registro/$id"

    val publicas = setOf(LOGIN, CREAR_CUENTA)
}

private data class Pestana(val ruta: String, val etiqueta: String, val icono: ImageVector)

private val pestanas = listOf(
    Pestana(Rutas.PERFILES, "Perfiles", Icons.Filled.Person),
    Pestana(Rutas.ASISTENTE, "Asistente", Icons.Filled.Face),
    Pestana(Rutas.AJUSTES, "Ajustes", Icons.Filled.Settings)
)

/** Barra de navegación inferior (reutilizable en las 3 pantallas principales). */
@Composable
private fun BarraInferior(nav: NavController, rutaActual: String) {
    NavigationBar {
        pestanas.forEach { p ->
            NavigationBarItem(
                selected = rutaActual == p.ruta,
                onClick = {
                    if (rutaActual != p.ruta) {
                        nav.navigate(p.ruta) {
                            popUpTo(Rutas.PERFILES) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(p.icono, contentDescription = p.etiqueta) },
                label = { Text(p.etiqueta) }
            )
        }
    }
}

@Composable
fun NutriApp() {
    val nav = rememberNavController()
    val fabrica = remember { VmFactory(DatosApplication.repositorio) }
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
                barraInferior = { BarraInferior(nav, Rutas.PERFILES) }
            )
        }
        composable(Rutas.ASISTENTE) {
            val vm: AsistenteViewModel = viewModel(factory = fabrica)
            AsistenteScreen(vm = vm, barraInferior = { BarraInferior(nav, Rutas.ASISTENTE) })
        }
        composable(Rutas.AJUSTES) {
            val vm: PerfilesViewModel = viewModel(factory = fabrica)
            AjustesScreen(
                usuario = usuario.orEmpty(),
                vm = vm,
                onCerrarSesion = auth::cerrarSesion,
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
                onNuevoRegistro = { nav.navigate(Rutas.nuevoRegistro(id)) }
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
    }
}
