package com.example.stocksandbox

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stocksandbox.camera.CameraScreen
import com.example.stocksandbox.camera.CameraViewModel
import com.example.stocksandbox.data.PreferencesRepo
import com.example.stocksandbox.phone.PhoneScreen
import com.example.stocksandbox.phone.PhoneViewModel
import com.example.stocksandbox.settings.SettingsScreen
import com.example.stocksandbox.settings.SettingsViewModel
import com.example.stocksandbox.ui.theme.StockSandboxTheme

private enum class MainDestination(val route: String, val label: String) {
    Phone("phone", "Telefon"),
    Settings("settings", "Einstellungen"),
    Camera("camera", "Kamera")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockSandboxApp(
    phoneViewModel: PhoneViewModel,
    settingsViewModel: SettingsViewModel,
    cameraViewModel: CameraViewModel
) {
    val themeMode by settingsViewModel.themeMode.collectAsState(initial = PreferencesRepo.THEME_MODE_SYSTEM)
    val darkTheme = when (themeMode) {
        PreferencesRepo.THEME_MODE_LIGHT -> false
        PreferencesRepo.THEME_MODE_DARK -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    StockSandboxTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        val destinations = remember { MainDestination.values() }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: MainDestination.Phone.route

        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = when (currentRoute) {
                    MainDestination.Phone.route -> "Telefon"
                    MainDestination.Settings.route -> "Einstellungen"
                    MainDestination.Camera.route -> "Kamera"
                    else -> "Stock Sandbox"
                }) })
            },
            bottomBar = {
                NavigationBar {
                    destinations.forEach { destination ->
                        val selected = destination.route == currentRoute
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                val icon = when (destination) {
                                    MainDestination.Phone -> Icons.Default.Call
                                    MainDestination.Settings -> Icons.Default.Settings
                                    MainDestination.Camera -> Icons.Default.PhotoCamera
                                }
                                Icon(imageVector = icon, contentDescription = destination.label)
                            },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = MainDestination.Phone.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(MainDestination.Phone.route) {
                    PhoneScreen(viewModel = phoneViewModel, settingsViewModel = settingsViewModel)
                }
                composable(MainDestination.Settings.route) {
                    SettingsScreen(viewModel = settingsViewModel)
                }
                composable(MainDestination.Camera.route) {
                    CameraScreen(viewModel = cameraViewModel)
                }
            }
        }
    }
}
