package com.gems.insight.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gems.insight.ui.screen.HomeScreen
import com.gems.insight.ui.screen.ItemScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = NavigationItem.HomeScreen.route
) {
    NavHost(
        modifier = Modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.HomeScreen.route) {
            HomeScreen(navController)
        }

        composable("item/{query}/{itemTitle}") { backStackEntry ->
            val itemTitle = backStackEntry.arguments?.getString("itemTitle").toString()
            val query = backStackEntry.arguments?.getString("query").toString()
            ItemScreen(itemTitle, navController, query)
        }
    }
}