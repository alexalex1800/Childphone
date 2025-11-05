package com.example.stocksandbox.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.stocksandbox.R
import com.example.stocksandbox.data.AppSettings
import com.example.stocksandbox.data.ThemeOption

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    currentSettings: AppSettings
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.settings_disclaimer),
                style = MaterialTheme.typography.bodyMedium
            )
            ThemePicker(current = currentSettings.theme, onSelect = viewModel::setTheme)
            WifiToggle(current = currentSettings.fakeWifiEnabled, onToggle = viewModel::setFakeWifi)
            RingtonePicker(current = currentSettings.ringtone, onSelect = viewModel::setRingtone)
            TtsPicker(current = currentSettings.ttsLocale, onSelect = viewModel::setTtsLocale)
        }
    }
}

@Composable
private fun ThemePicker(current: ThemeOption, onSelect: (ThemeOption) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = stringResource(id = R.string.theme), style = MaterialTheme.typography.titleMedium)
        ThemeOption.values().forEach { option ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                RadioButton(
                    selected = option == current,
                    onClick = { onSelect(option) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (option) {
                        ThemeOption.SYSTEM -> stringResource(id = R.string.system)
                        ThemeOption.LIGHT -> stringResource(id = R.string.light)
                        ThemeOption.DARK -> stringResource(id = R.string.dark)
                    }
                )
            }
        }
    }
}

@Composable
private fun WifiToggle(current: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = stringResource(id = R.string.fake_wifi), style = MaterialTheme.typography.titleMedium)
            Text(text = stringResource(id = R.string.fake_wifi_note), style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = current, onCheckedChange = onToggle)
    }
}

@Composable
private fun RingtonePicker(current: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        "ringtone_classic" to stringResource(id = R.string.ringtone_classic),
        "ringtone_beep" to stringResource(id = R.string.ringtone_beep),
        "ringtone_sci_fi" to stringResource(id = R.string.ringtone_sci_fi)
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = stringResource(id = R.string.ringtone), style = MaterialTheme.typography.titleMedium)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = options.firstOrNull { it.first == current }?.second ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(text = stringResource(id = R.string.ringtone)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (key, label) ->
                    DropdownMenuItem(
                        text = { Text(text = label) },
                        onClick = {
                            onSelect(key)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TtsPicker(current: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        "de-DE" to stringResource(id = R.string.german),
        "en-US" to stringResource(id = R.string.english)
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = stringResource(id = R.string.tts_language), style = MaterialTheme.typography.titleMedium)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = options.firstOrNull { it.first == current }?.second ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(text = stringResource(id = R.string.tts_language)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (key, label) ->
                    DropdownMenuItem(
                        text = { Text(text = label) },
                        onClick = {
                            onSelect(key)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
