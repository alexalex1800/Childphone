package com.example.stocksandbox.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stocksandbox.data.PreferencesRepo
import com.example.stocksandbox.data.ThemeOption
import kotlinx.coroutines.launch

class SettingsViewModel(private val repo: PreferencesRepo) : ViewModel() {

    fun setTheme(option: ThemeOption) {
        viewModelScope.launch {
            repo.setTheme(option)
        }
    }

    fun setFakeWifi(enabled: Boolean) {
        viewModelScope.launch {
            repo.setFakeWifi(enabled)
        }
    }

    fun setRingtone(value: String) {
        viewModelScope.launch {
            repo.setRingtone(value)
        }
    }

    fun setTtsLocale(value: String) {
        viewModelScope.launch {
            repo.setTtsLocale(value)
        }
    }
}

class SettingsViewModelFactory(private val repo: PreferencesRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
