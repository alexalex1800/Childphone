package com.example.stocksandbox.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stocksandbox.data.PreferencesRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repo: PreferencesRepo) : ViewModel() {

    val themeMode: StateFlow<Int> = repo.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PreferencesRepo.THEME_MODE_SYSTEM
    )

    val fakeWifi: StateFlow<Boolean> = repo.fakeWifiEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val ringtoneChoice: StateFlow<String> = repo.ringtoneChoice.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "classic"
    )

    val ttsLocale: StateFlow<String> = repo.ttsLocale.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "de"
    )

    fun setTheme(mode: Int) {
        viewModelScope.launch { repo.setThemeMode(mode) }
    }

    fun setFakeWifi(enabled: Boolean) {
        viewModelScope.launch { repo.setFakeWifi(enabled) }
    }

    fun setRingtone(choice: String) {
        viewModelScope.launch { repo.setRingtone(choice) }
    }

    fun setTtsLocale(locale: String) {
        viewModelScope.launch { repo.setTtsLocale(locale) }
    }

    companion object {
        fun provideFactory(repo: PreferencesRepo): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(repo) as T
            }
        }
    }
}
