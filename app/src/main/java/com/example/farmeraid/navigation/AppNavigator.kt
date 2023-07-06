package com.example.farmeraid.navigation

import androidx.navigation.NavHostController

class AppNavigator {
    private var _navController : NavHostController? = null
    val navController : NavHostController?
        get() = _navController

    fun setNavController(navHostController: NavHostController) {
        _navController = navHostController
    }

    fun navigateToMode(navRoute: NavRoute) {
        _navController?.navigate(navRoute.route)
    }

    fun navigateBack() {
        _navController?.navigateUp()
    }

    fun navigateToTransactions() {
        _navController?.navigate(NavRoute.Transactions.route)
    }

    fun navigateToAddQuota() {
        _navController?.navigate(NavRoute.AddEditQuota.route)
    }

    fun navigateToEditQuota(marketId : String) {
        _navController?.navigate(NavRoute.AddEditQuota.route + "?marketId=${marketId}")
    }

    fun navigateToViewQuota(marketId : String) {
        _navController?.navigate(NavRoute.ViewQuota.route + "/${marketId}")
    }

    fun navigateToJoinFarm() {
        _navController?.navigate(NavRoute.JoinFarm.route)
    }
}