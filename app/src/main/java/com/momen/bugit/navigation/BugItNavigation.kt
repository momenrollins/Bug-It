package com.momen.bugit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.momen.bugit.ui.screens.HomeScreen
import com.momen.bugit.ui.screens.BugFormScreen
import com.momen.bugit.ui.screens.SuccessScreen

@Composable
fun BugItNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToBugForm = {
                    navController.navigate(Screen.BugForm.route)
                }
            )
        }
        
        composable(Screen.BugForm.route) {
            BugFormScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSuccess = {
                    navController.navigate(Screen.Success.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }
        
        composable(Screen.Success.route) {
            SuccessScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
