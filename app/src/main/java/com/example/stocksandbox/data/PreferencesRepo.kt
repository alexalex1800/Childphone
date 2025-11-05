package com.example.stocksandbox.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sandbox_prefs")

class PreferencesRepo(private val context: Context) {

    val themeMode: Flow<Int> = context.dataStore.data.map { it[THEME_MODE] ?: THEME_MODE_SYSTEM }
    val fakeWifiEnabled: Flow<Boolean> = context.dataStore.data.map { it[FAKE_WIFI] ?: false }
    val ringtoneChoice: Flow<String> = context.dataStore.data.map { it[RINGTONE_CHOICE] ?: "classic" }
    val ttsLocale: Flow<String> = context.dataStore.data.map { it[TTS_LOCALE] ?: "de" }

    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { it[THEME_MODE] = mode }
    }

    suspend fun setFakeWifi(enabled: Boolean) {
        context.dataStore.edit { it[FAKE_WIFI] = enabled }
    }

    suspend fun setRingtone(choice: String) {
        context.dataStore.edit { it[RINGTONE_CHOICE] = choice }
    }

    suspend fun setTtsLocale(locale: String) {
        context.dataStore.edit { it[TTS_LOCALE] = locale }
    }

    companion object {
        const val THEME_MODE_SYSTEM = 0
        const val THEME_MODE_LIGHT = 1
        const val THEME_MODE_DARK = 2

        private val THEME_MODE = intPreferencesKey("theme_mode")
        private val FAKE_WIFI = booleanPreferencesKey("fake_wifi")
        private val RINGTONE_CHOICE = stringPreferencesKey("ringtone_choice")
        private val TTS_LOCALE = stringPreferencesKey("tts_locale")
    }
}
