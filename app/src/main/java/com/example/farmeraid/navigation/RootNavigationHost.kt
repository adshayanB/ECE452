package com.example.farmeraid.navigation

import AddEditQuotaScreenView
import BottomNavigationBar
import FarmScreenView
import HomeScreenView
import TransactionsView
import ViewQuotaScreenView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.farmeraid.join_farm.views.JoinFarmScreenView
import com.example.farmeraid.sign_in.views.SignInScreenView
import com.example.farmeraid.sign_up.views.SignUpScreenView
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootNavigationHost(
    appNavigator: AppNavigator,
    snackbarHostState: SnackbarHostState,
) {
    appNavigator.navController?.let{
        Scaffold(
            bottomBar = { BottomNavigationBar(appNavigator) },
            snackbarHost = { SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        actionColor = PrimaryColour,
                        actionContentColor = PrimaryColour,
                        dismissActionContentColor = Color.Black,
                    )
                }
            ) }
        ) { padding ->
            NavHost(
                navController = appNavigator.navController!!,
                startDestination = NavRoute.JoinFarm.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(NavRoute.SignUp.route){
                    SignUpScreenView()
                }
                composable(NavRoute.SignIn.route) {
                    SignInScreenView()
                }
                composable(NavRoute.JoinFarm.route) {
                    JoinFarmScreenView()
                }
                composable(NavRoute.Farm.route) {
                    FarmScreenView()
                }
                composable(NavRoute.Home.route) {
                    HomeScreenView()
                }
                composable(
                    route = NavRoute.AddEditQuota.route + "?marketId={marketId}",
                    arguments = listOf(navArgument("marketId") { nullable = true })
                ) {
                    AddEditQuotaScreenView()
                }
                composable(NavRoute.Transactions.route) {
                    TransactionsView()
                }
                composable(
                    route = NavRoute.ViewQuota.route + "/{marketId}",
                    arguments = listOf(navArgument("marketId") { type = NavType.StringType})
                ) {
                    ViewQuotaScreenView()
                }
            }
        }
    }
}