package com.example.stocksandbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stocksandbox.camera.CameraViewModel
import com.example.stocksandbox.camera.CameraViewModelFactory
import com.example.stocksandbox.data.AppViewModel
import com.example.stocksandbox.data.AppViewModelFactory
import com.example.stocksandbox.phone.PhoneViewModel
import com.example.stocksandbox.phone.PhoneViewModelFactory
import com.example.stocksandbox.settings.SettingsViewModel
import com.example.stocksandbox.settings.SettingsViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as StockSandboxApp
        setContent {
            val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory(app.preferencesRepo))
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(app.preferencesRepo))
            val phoneViewModel: PhoneViewModel = viewModel(factory = PhoneViewModelFactory(app))
            val cameraViewModel: CameraViewModel = viewModel(factory = CameraViewModelFactory(app))

            StockSandboxNavApp(
                appViewModel = appViewModel,
                settingsViewModel = settingsViewModel,
                phoneViewModel = phoneViewModel,
                cameraViewModel = cameraViewModel
            )
        }
    }
}
