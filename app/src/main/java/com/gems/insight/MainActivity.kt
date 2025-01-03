package com.gems.insight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.gems.insight.navigation.AppNavHost
import com.gems.insight.ui.theme.InsightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InsightTheme {
                val navController = rememberNavController()
                AppNavHost(navController)
            }
        }
    }
}
