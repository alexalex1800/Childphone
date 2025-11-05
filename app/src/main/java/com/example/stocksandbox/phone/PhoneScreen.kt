package com.example.stocksandbox.phone

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.stocksandbox.R
import com.example.stocksandbox.data.AppSettings

@Composable
fun PhoneScreen(
    viewModel: PhoneViewModel,
    appSettings: AppSettings
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(appSettings) {
        viewModel.applySettings(appSettings)
    }

    when (uiState.status) {
        is CallStatus.Idle -> DialerScreen(uiState = uiState, onAction = viewModel)
        else -> InCallScreen(uiState = uiState, onAction = viewModel)
    }
}

@Composable
private fun DialerScreen(uiState: PhoneUiState, onAction: PhoneViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.dialedNumber,
            onValueChange = {},
            enabled = false,
            label = { Text(text = stringResource(id = R.string.dialer_label)) },
            trailingIcon = {
                IconButton(onClick = { onAction.onBackspace() }) {
                    Icon(Icons.Default.Backspace, contentDescription = null)
                }
            }
        )

        Text(
            modifier = Modifier
                .padding(top = 12.dp),
            text = stringResource(id = R.string.dial_simulation_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))
        DialPad(onDigit = { onAction.onDigitPressed(it) })
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onAction.startCall() },
            enabled = uiState.dialedNumber.isNotBlank()
        ) {
            Text(text = stringResource(id = R.string.call))
        }
    }
}

@Composable
private fun DialPad(onDigit: (String) -> Unit) {
    val buttons = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("*", "0", "#")
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                row.forEach { label ->
                    FilledTonalButton(
                        modifier = Modifier.size(80.dp, 64.dp),
                        onClick = { onDigit(label) }
                    ) {
                        Text(text = label, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun InCallScreen(uiState: PhoneUiState, onAction: PhoneViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(64.dp)
            )
        }
        Text(
            text = uiState.dialedNumber,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        when (val status = uiState.status) {
            CallStatus.Dialing -> Text(text = stringResource(id = R.string.call_connecting))
            is CallStatus.Connected -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = formatDuration(uiState.elapsedSeconds))
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = status.message,
                    textAlign = TextAlign.Center
                )
            }
            else -> Unit
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ElevatedButton(onClick = { onAction.toggleMute() }) {
                Icon(Icons.Default.MicOff, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = if (uiState.isMuted) stringResource(id = R.string.mute_enabled) else stringResource(id = R.string.mute))
            }
            ElevatedButton(onClick = { onAction.toggleSpeaker() }) {
                Icon(Icons.Default.VolumeUp, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = if (uiState.isSpeakerOn) stringResource(id = R.string.speaker_enabled) else stringResource(id = R.string.speaker))
            }
        }

        OutlinedButton(
            onClick = { onAction.endCall() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CallEnd, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = stringResource(id = R.string.end_call))
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
