package com.example.stocksandbox.phone

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.stocksandbox.R
import com.example.stocksandbox.settings.SettingsViewModel

@Composable
fun PhoneScreen(
    viewModel: PhoneViewModel,
    settingsViewModel: SettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val fakeWifiEnabled by settingsViewModel.fakeWifi.collectAsState(initial = false)
    PhoneScreenContent(
        uiState = uiState,
        fakeWifiEnabled = fakeWifiEnabled,
        onBackspace = { viewModel.onBackspace() },
        onClearNumber = { viewModel.clearNumber() },
        onSymbol = { viewModel.onDialPadInput(it) },
        onCall = { viewModel.onCallPressed() },
        onEnd = { viewModel.onEndCall() },
        onMute = { viewModel.toggleMute() },
        onSpeaker = { viewModel.toggleSpeaker() }
    )
}

@Composable
fun PhoneScreenContent(
    uiState: PhoneViewModel.PhoneUiState,
    fakeWifiEnabled: Boolean,
    onBackspace: () -> Unit,
    onClearNumber: () -> Unit,
    onSymbol: (String) -> Unit,
    onCall: () -> Unit,
    onEnd: () -> Unit,
    onMute: () -> Unit,
    onSpeaker: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (uiState.showOnboarding) {
                OnboardingCard()
            }
            Text(
                text = if (fakeWifiEnabled) "WLAN: Verbunden (simuliert)" else "WLAN: Getrennt (simuliert)",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            DialDisplay(
                number = uiState.dialedNumber,
                status = uiState.statusText,
                onBackspace = onBackspace,
                onClear = onClearNumber
            )
        }

        if (uiState.isInCall) {
            InCallScreen(
                uiState = uiState,
                onEnd = onEnd,
                onMute = onMute,
                onSpeaker = onSpeaker
            )
        } else {
            DialPad(onSymbol = onSymbol, onCall = onCall)
        }

        Text(
            text = stringResource(id = R.string.dial_simulation_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OnboardingCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.onboarding_message),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun DialDisplay(
    number: String,
    status: String,
    onBackspace: () -> Unit,
    onClear: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (number.isEmpty()) "Nummer eingeben" else number,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onBackspace) {
                Icon(imageVector = Icons.Default.Backspace, contentDescription = "Backspace")
            }
        }
        FilledTonalButton(
            onClick = onClear,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(text = "Nummer löschen")
        }
    }
}

@Composable
private fun DialPad(onSymbol: (String) -> Unit, onCall: () -> Unit) {
    val symbols = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "*", "0", "#"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(symbols.size) { index ->
                val symbol = symbols[index]
                DialPadButton(symbol = symbol) { onSymbol(symbol) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCall, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Anrufen (simuliert)")
        }
    }
}

@Composable
private fun DialPadButton(symbol: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        Text(text = symbol, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun InCallScreen(
    uiState: PhoneViewModel.PhoneUiState,
    onEnd: () -> Unit,
    onMute: () -> Unit,
    onSpeaker: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "👤", style = MaterialTheme.typography.displaySmall)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = uiState.dialedNumber, style = MaterialTheme.typography.headlineSmall)
        Text(text = formatSeconds(uiState.elapsedSeconds), style = MaterialTheme.typography.bodyLarge)
        Text(text = uiState.statusText, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            FilledTonalButton(onClick = onMute) {
                Text(text = if (uiState.isMuted) "Stumm aus" else "Stumm")
            }
            FilledTonalButton(onClick = onSpeaker) {
                Text(text = if (uiState.isSpeaker) "Lautsprecher aus" else "Lautsprecher")
            }
            Button(onClick = onEnd) {
                Text(text = "Beenden")
            }
        }
    }
}

private fun formatSeconds(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}
