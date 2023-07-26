package com.example.farmeraid.navigation

sealed class NavRoute(val route: String) {
    object LoadingScreen: NavRoute("loading_route")
    object SignOutScreen:NavRoute("signout_route")
    object SignIn : NavRoute("signin_route")
    object SignUp: NavRoute("signup_route")
    object CreateFarm: NavRoute("create_farm_route")
    object FarmCode: NavRoute("farm_code_route")
    object SignOut : NavRoute("signout_route")
    object JoinFarm: NavRoute("join_farm_route")
    object Home : NavRoute("home_route")
    object Farm : NavRoute("farm_route")
    object Market : NavRoute("market_route")
    object Charity : NavRoute("charity_route")

    object FarmSelection: NavRoute("farm_selection")

    object Transactions : NavRoute("transactions_route")
    object AddEditQuota : NavRoute("add_edit_quota_route")

    object ViewQuota : NavRoute("view_quota_route")

    object SellProduce : NavRoute("sell_produce_route")

    object AddEditProduce : NavRoute("add_edit_produce_route")

    object AddEditMarket : NavRoute("add_edit_market_route")
    object SettingsScreen : NavRoute("settings_route")
}