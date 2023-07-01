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
        _navController?.popBackStack()
    }

    fun navigateToTransactions() {
        _navController?.navigate(NavRoute.Transactions.route)
    }

    fun navigateToAddQuota() {
        _navController?.navigate(NavRoute.AddEditQuota.route)
    }
}