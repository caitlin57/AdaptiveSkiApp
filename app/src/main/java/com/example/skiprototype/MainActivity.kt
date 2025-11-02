package com.example.skiprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.skiprototype.Settings.SettingsViewModel
import com.example.skiprototype.Settings.SettingsViewModelFactory
import com.example.skiprototype.ui.theme.SkiPrototypeTheme
//defines the main entry point of a Jetpack Compose Android app
// that uses navigation, a bottom navigation bar,
// and ViewModels to manage UI state across multiple screens.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkiPrototypeTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val dataViewModel: DataViewModel = viewModel()
                val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory())
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute != "welcome") {
                            BottomNavigationBar(navController)
                        }
                    },
                    containerColor = Color(0xFFADDBF1)
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "welcome", //first screen
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Welcome screen
                        composable("welcome") {
                            WelcomeScreen(navController = navController)
                        }

                        composable(BottomNavItem.Home.route) {
                            SkiStatusScreen(navController = navController, dataViewModel = dataViewModel, settingsViewModel = settingsViewModel)
                        }
                        composable(BottomNavItem.Record.route) {
                            TempScreen(navController = navController, dataViewModel = dataViewModel, settingsViewModel = settingsViewModel)
                        }
                        composable(BottomNavItem.Settings.route) {
                            SettingsScreen(viewModel = settingsViewModel)
                        }

                        composable(BottomNavItem.Pressure.route) {
                            PressureScreen(navController)
                        }
                    }
                }
            }
        }
    }
}


sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Live Data", Icons.Filled.Home)
    object Record : BottomNavItem("record", "Temperature", Icons.Filled.AddCircle)
    object Pressure : BottomNavItem("pressure", "Pressure", Icons.Filled.Info)
    object Settings : BottomNavItem("settings", "Settings", Icons.Filled.Settings)
}

@Composable
fun BottomNavigationBar(navController: NavController) { //bottom bar
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Record,
        BottomNavItem.Pressure,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                label = { Text(item.label) },
                icon = { Icon(item.icon, contentDescription = item.label) }
            )
        }
    }
}
