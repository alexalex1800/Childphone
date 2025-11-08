package com.example.stocksandbox.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.stocksandbox.R
import com.example.stocksandbox.data.PreferencesRepo
import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val themeMode = viewModel.themeMode.value
    val fakeWifi = viewModel.fakeWifi.value
    val ringtone = viewModel.ringtoneChoice.value
    val ttsLocale = viewModel.ttsLocale.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.settings_disclaimer),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Theme", style = MaterialTheme.typography.titleMedium)
        ThemeSelection(selected = themeMode, onSelect = viewModel::setTheme)

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "WLAN (nur optisch)", modifier = Modifier.weight(1f))
            Switch(checked = fakeWifi, onCheckedChange = { viewModel.setFakeWifi(it) })
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Klingelton", style = MaterialTheme.typography.titleMedium)
        DropdownSetting(
            options = listOf(
                "classic" to "Klassisch",
                "modern" to "Modern",
                "retro" to "Retro"
            ),
            selected = ringtone,
            onSelect = viewModel::setRingtone
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "TTS-Sprache", style = MaterialTheme.typography.titleMedium)
        DropdownSetting(
            options = listOf(
                "de" to "Deutsch",
                "en" to "Englisch"
            ),
            selected = ttsLocale,
            onSelect = viewModel::setTtsLocale
        )
    }
}

@Composable
private fun ThemeSelection(selected: Int, onSelect: (Int) -> Unit) {
    Column {
        ThemeOption(label = "System", selected = selected == PreferencesRepo.THEME_MODE_SYSTEM) {
            onSelect(PreferencesRepo.THEME_MODE_SYSTEM)
        }
        ThemeOption(label = "Hell", selected = selected == PreferencesRepo.THEME_MODE_LIGHT) {
            onSelect(PreferencesRepo.THEME_MODE_LIGHT)
        }
        ThemeOption(label = "Dunkel", selected = selected == PreferencesRepo.THEME_MODE_DARK) {
            onSelect(PreferencesRepo.THEME_MODE_DARK)
        }
    }
}

@Composable
private fun ThemeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun DropdownSetting(
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val selectedLabel = options.firstOrNull { it.first == selected }?.second ?: selected
            Text(text = selectedLabel)
        }
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        options.forEach { (value, label) ->
            DropdownMenuItem(
                text = { Text(label) },
                onClick = {
                    expanded = false
                    onSelect(value)
                }
            )
        }
    }
}
