package com.example.farmeraid.navigation

sealed class NavRoute(val route: String) {
    object Home : NavRoute("home_route")
}