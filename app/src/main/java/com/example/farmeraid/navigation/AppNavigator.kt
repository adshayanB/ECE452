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

    fun navigateToAddProduce() {
        _navController?.navigate(NavRoute.AddEditProduce.route)
    }

    fun navigateToEditProduce(produceName : String, produceAmount : Int) {
        _navController?.navigate(NavRoute.AddEditProduce.route + "?produceName=${produceName}&produceAmount=${produceAmount}")
    }

    fun navigateToJoinFarm() {
        _navController?.navigate(NavRoute.JoinFarm.route)
    }
    
    fun navigateToCreateFarm() {
        _navController?.navigate(NavRoute.CreateFarm.route)
    }

    fun navigateToFarmCode() {
        _navController?.navigate(NavRoute.FarmCode.route)
    }

    fun navigateToFarmSelection(){
        _navController?.navigate(NavRoute.FarmSelection.route)
    }

    fun navigateToSellProduce(marketId: String) {
        _navController?.navigate(NavRoute.SellProduce.route + "/${marketId}")
    }

    fun navigateToAddMarket() {
        _navController?.navigate(NavRoute.AddEditMarket.route)
    }

    fun navigateToEditMarket(marketId : String) {
        _navController?.navigate(NavRoute.AddEditMarket.route + "?marketId=${marketId}")
    }
}