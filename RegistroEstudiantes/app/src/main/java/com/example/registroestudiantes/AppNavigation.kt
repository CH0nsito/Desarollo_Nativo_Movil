package com.example.registroestudiantes

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "registration"
    ) {
        composable("registration") {
            RegistrationScreen(
                onNavigateToConfirmation = { matricula, nombre, carrera, turno, estatus ->
                    // Codificamos los textos por si contienen espacios
                    val nombreEnc = URLEncoder.encode(nombre, "UTF-8")
                    val carreraEnc = URLEncoder.encode(carrera, "UTF-8")
                    navController.navigate(
                        "confirmation/$matricula/$nombreEnc/$carreraEnc/$turno/$estatus"
                    )
                }
            )
        }

        composable(
            route = "confirmation/{matricula}/{nombre}/{carrera}/{turno}/{estatus}",
            arguments = listOf(
                navArgument("matricula") { type = NavType.StringType },
                navArgument("nombre") { type = NavType.StringType },
                navArgument("carrera") { type = NavType.StringType },
                navArgument("turno") { type = NavType.StringType },
                navArgument("estatus") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val matricula = backStackEntry.arguments?.getString("matricula") ?: ""
            val nombre = URLDecoder.decode(backStackEntry.arguments?.getString("nombre") ?: "", "UTF-8")
            val carrera = URLDecoder.decode(backStackEntry.arguments?.getString("carrera") ?: "", "UTF-8")
            val turno = backStackEntry.arguments?.getString("turno") ?: ""
            val estatus = backStackEntry.arguments?.getBoolean("estatus") ?: true

            ConfirmationScreen(
                matricula = matricula,
                nombre = nombre,
                carrera = carrera,
                turno = turno,
                estatus = estatus,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}