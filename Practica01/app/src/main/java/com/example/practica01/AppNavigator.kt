package com.example.practica01

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "first_screen"
    ) {
        // Pantalla 1 - FirstScreen
        composable("first_screen") {
            FirstScreen(onNavigateToSecondScreen = { texto ->
                navController.navigate("second_screen/$texto")
            })
        }

        // Pantalla 2 con argumento - SecondScreen
        composable(
            route = "second_screen/{texto}",
            arguments = listOf(navArgument("texto") { type = NavType.StringType })
        ) { backStackEntry ->
            val textoRecibido = backStackEntry.arguments?.getString("texto") ?: ""
            SecondScreen(
                mensaje = textoRecibido,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}