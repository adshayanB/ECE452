package com.example.farmeraid.navigation

sealed class NavRoute(val route: String) {
    object SignIn : NavRoute("signin_route")
    object SignOut : NavRoute("signout_route")
    object Home : NavRoute("home_route")
    object Farm : NavRoute("farm_route")
    object Market : NavRoute("market_route")
    object Charity : NavRoute("charity_route")
}