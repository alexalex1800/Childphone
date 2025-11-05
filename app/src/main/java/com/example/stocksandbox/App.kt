package com.example.stocksandbox

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stocksandbox.camera.CameraScreen
import com.example.stocksandbox.camera.CameraViewModel
import com.example.stocksandbox.data.AppViewModel
import com.example.stocksandbox.data.ThemeOption
import com.example.stocksandbox.phone.PhoneScreen
import com.example.stocksandbox.phone.PhoneViewModel
import com.example.stocksandbox.settings.SettingsScreen
import com.example.stocksandbox.settings.SettingsViewModel
import com.example.stocksandbox.ui.theme.StockSandboxTheme

private enum class MainDestination(val route: String) {
    Phone("phone"),
    Settings("settings"),
    Camera("camera")
}

@Composable
fun StockSandboxNavApp(
    appViewModel: AppViewModel,
    settingsViewModel: SettingsViewModel,
    phoneViewModel: PhoneViewModel,
    cameraViewModel: CameraViewModel
) {
    val settings by appViewModel.settings.collectAsState()
    val darkTheme = when (settings.theme) {
        ThemeOption.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
        ThemeOption.LIGHT -> false
        ThemeOption.DARK -> true
    }
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: MainDestination.Phone.route

    StockSandboxTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentRoute) {
                                MainDestination.Phone.route -> stringResource(id = R.string.phone_tab)
                                MainDestination.Settings.route -> stringResource(id = R.string.settings_tab)
                                MainDestination.Camera.route -> stringResource(id = R.string.camera_tab)
                                else -> stringResource(id = R.string.app_name)
                            }
                        )
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    MainDestination.values().forEach { destination ->
                        val selected = currentRoute == destination.route
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
                                when (destination) {
                                    MainDestination.Phone -> Icon(Icons.Default.Call, contentDescription = null)
                                    MainDestination.Settings -> Icon(Icons.Default.Settings, contentDescription = null)
                                    MainDestination.Camera -> Icon(Icons.Default.PhotoCamera, contentDescription = null)
                                }
                            },
                            label = {
                                Text(
                                    text = when (destination) {
                                        MainDestination.Phone -> stringResource(id = R.string.phone_tab)
                                        MainDestination.Settings -> stringResource(id = R.string.settings_tab)
                                        MainDestination.Camera -> stringResource(id = R.string.camera_tab)
                                    }
                                )
                            }
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = MainDestination.Phone.route
                ) {
                    composable(MainDestination.Phone.route) {
                        PhoneScreen(
                            viewModel = phoneViewModel,
                            appSettings = settings
                        )
                    }
                    composable(MainDestination.Settings.route) {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            currentSettings = settings
                        )
                    }
                    composable(MainDestination.Camera.route) {
                        CameraScreen(
                            viewModel = cameraViewModel,
                            snackbarHostState = snackbarHostState
                        )
                    }
                }

                if (!settings.onboardingShown) {
                    OnboardingCard(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        onDismiss = { appViewModel.markOnboardingShown() }
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingCard(modifier: Modifier = Modifier, onDismiss: () -> Unit) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(id = R.string.onboarding_message),
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 12.dp),
                onClick = onDismiss
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        }
    }
}
