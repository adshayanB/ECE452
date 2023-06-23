package com.example.farmeraid.navigation

import BottomNavigationBar
import FarmScreenView
import HomeScreenView
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.farmeraid.sign_in.views.SignInScreenView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootNavigationHost(appNavigator: AppNavigator) {
    appNavigator.navController?.let{
        Scaffold(
            bottomBar = { BottomNavigationBar(appNavigator) }
        ) { padding ->
            NavHost(
                navController = appNavigator.navController!!,
                startDestination = NavRoute.SignIn.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(NavRoute.SignIn.route) {
                    SignInScreenView()
                }
                composable(NavRoute.Farm.route) {
                    FarmScreenView()
                }
                composable(NavRoute.Home.route) {
                    HomeScreenView()
                }
            }
        }
    }
}