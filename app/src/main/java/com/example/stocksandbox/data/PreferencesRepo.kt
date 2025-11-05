package com.example.stocksandbox.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "stock_sandbox")

enum class ThemeOption { SYSTEM, LIGHT, DARK }

data class AppSettings(
    val theme: ThemeOption = ThemeOption.SYSTEM,
    val fakeWifiEnabled: Boolean = false,
    val ringtone: String = "ringtone_classic",
    val ttsLocale: String = "de-DE",
    val onboardingShown: Boolean = false
)

class PreferencesRepo(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("theme_mode")
        val WIFI = booleanPreferencesKey("fake_wifi_enabled")
        val RINGTONE = stringPreferencesKey("ringtone_choice")
        val TTS = stringPreferencesKey("tts_locale")
        val ONBOARDING = booleanPreferencesKey("onboarding_shown")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            theme = prefs[Keys.THEME]?.let { ThemeOption.valueOf(it) } ?: ThemeOption.SYSTEM,
            fakeWifiEnabled = prefs[Keys.WIFI] ?: false,
            ringtone = prefs[Keys.RINGTONE] ?: "ringtone_classic",
            ttsLocale = prefs[Keys.TTS] ?: "de-DE",
            onboardingShown = prefs[Keys.ONBOARDING] ?: false
        )
    }

    suspend fun setTheme(option: ThemeOption) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME] = option.name
        }
    }

    suspend fun setFakeWifi(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.WIFI] = enabled
        }
    }

    suspend fun setRingtone(value: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.RINGTONE] = value
        }
    }

    suspend fun setTtsLocale(value: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TTS] = value
        }
    }

    suspend fun markOnboardingShown() {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING] = true
        }
    }
}
