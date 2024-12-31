package com.gems.insight.navigation

sealed class NavigationItem(val route: String) {
    object HomeScreen : NavigationItem("home")
}