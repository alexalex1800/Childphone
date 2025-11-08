package com.example.stocksandbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stocksandbox.data.PreferencesRepo
import com.example.stocksandbox.phone.PhoneViewModel
import com.example.stocksandbox.settings.SettingsViewModel
import com.example.stocksandbox.camera.CameraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferencesRepo = PreferencesRepo(applicationContext)
        setContent {
            val phoneViewModel: PhoneViewModel = viewModel(factory = PhoneViewModel.provideFactory(applicationContext, preferencesRepo))
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.provideFactory(preferencesRepo))
            val cameraViewModel: CameraViewModel = viewModel(factory = CameraViewModel.provideFactory(applicationContext))
            StockSandboxApp(
                phoneViewModel = phoneViewModel,
                settingsViewModel = settingsViewModel,
                cameraViewModel = cameraViewModel
            )
        }
    }
}
