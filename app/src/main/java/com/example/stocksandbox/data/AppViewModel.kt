package com.example.stocksandbox.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(private val repo: PreferencesRepo) : ViewModel() {

    val settings: StateFlow<AppSettings> = repo.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettings()
    )

    fun markOnboardingShown() {
        viewModelScope.launch {
            repo.markOnboardingShown()
        }
    }
}

class AppViewModelFactory(private val repo: PreferencesRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
