package com.example.farmeraid.navigation

import AddEditProduceScreenView
import AddEditQuotaScreenView
import BottomNavigationBar
import FarmScreenView
import HomeScreenView
import MarketScreenView
import SellProduceView
import TransactionsView
import ViewQuotaScreenView
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.farmeraid.charity.views.CharityMapsScreenView
import com.example.farmeraid.join_farm.views.JoinFarmScreenView
import com.example.farmeraid.create_farm.views.CreateFarmScreenView
import com.example.farmeraid.create_farm.views.FarmCodeScreenView
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.farm_selection.views.FarmSelectionScreenView
import com.example.farmeraid.fridge.views.AddEditFridgeScreenView
import com.example.farmeraid.market.add_edit_market.views.AddEditMarketScreenView
import com.example.farmeraid.sign_in.views.LoadingScreenView
import com.example.farmeraid.sign_in.views.SignInScreenView
import com.example.farmeraid.sign_in.views.SignOutScreenView
import com.example.farmeraid.sign_up.views.SignUpScreenView
import com.example.farmeraid.ui.theme.PrimaryColour

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                startDestination = NavRoute.LoadingScreen.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(NavRoute.SignUp.route){
                    SignUpScreenView()
                }
                composable(NavRoute.SignOut.route){
                    SignOutScreenView()
                }
                composable(NavRoute.LoadingScreen.route){
                    LoadingScreenView()
                }
                composable(NavRoute.SignIn.route) {
                    SignInScreenView()
                }
                composable(NavRoute.JoinFarm.route) {
                    JoinFarmScreenView()
                }            
                composable(NavRoute.CreateFarm.route) {
                    CreateFarmScreenView()
                }           
                composable(NavRoute.FarmCode.route) {
                    FarmCodeScreenView()
                }          
                composable(NavRoute.Farm.route) {
                    FarmScreenView()
                }
                composable(NavRoute.Home.route) {
                    HomeScreenView()
                }
                composable(NavRoute.Market.route) {
                    MarketScreenView()
                }
                composable(
                    route = NavRoute.AddEditQuota.route + "?marketId={marketId}",
                    arguments = listOf(navArgument("marketId") {
                        type = NavType.StringType
                        nullable = true
                    })
                ) {
                    AddEditQuotaScreenView()
                }
                composable(
                    route = NavRoute.Transactions.route + "?transactionType={transactionType}",
                    arguments = listOf(navArgument("transactionType") {
                        type = NavType.StringType
                        nullable = true
                    })
                ) {
                    TransactionsView()
                }
                composable(
                    route = NavRoute.ViewQuota.route + "/{marketId}",
                    arguments = listOf(navArgument("marketId") { type = NavType.StringType})
                ) {
                    ViewQuotaScreenView()
                }
                composable(
                    route = NavRoute.AddEditProduce.route + "?produceName={produceName}&produceAmount={produceAmount}",
                    arguments = listOf(
                        navArgument("produceName") {
                            type = NavType.StringType
                            nullable = true
                        },
                        navArgument("produceAmount") {
                            type = NavType.StringType
                            nullable = true
                        }
                    )
                ) {
                    AddEditProduceScreenView()
                }
                composable(NavRoute.FarmSelection.route){
                    FarmSelectionScreenView()
                }
                composable(
                    route = NavRoute.SellProduce.route + "/{marketId}",
                    arguments = listOf(navArgument("marketId") { type = NavType.StringType})
                ) {
                    SellProduceView()
                }
                composable(
                    route = NavRoute.AddEditMarket.route + "?marketId={marketId}",
                    arguments = listOf(navArgument("marketId") {
                        type = NavType.StringType
                        nullable = true
                    })
                ) {
                    AddEditMarketScreenView()
                }
                composable(NavRoute.Charity.route){
                    CharityMapsScreenView()
                }
                composable(NavRoute.AddEditFridge.route){
                    AddEditFridgeScreenView()
                }
            }
        }
    }
}