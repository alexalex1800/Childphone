package com.example.stocksandbox.phone

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stocksandbox.data.AppSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

sealed class CallStatus {
    object Idle : CallStatus()
    object Dialing : CallStatus()
    data class Connected(val message: String) : CallStatus()
}

data class PhoneUiState(
    val dialedNumber: String = "",
    val status: CallStatus = CallStatus.Idle,
    val elapsedSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false
)

class PhoneViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val _uiState = MutableStateFlow(PhoneUiState())
    val uiState: StateFlow<PhoneUiState> = _uiState.asStateFlow()

    private var tts: TextToSpeech = TextToSpeech(application, this)
    private var ttsReady: Boolean = false
    private var callJob: Job? = null
    private var currentPrompts: List<String> = defaultPrompts["ringtone_classic"] ?: defaultPrompts.values.first()
    private var pendingLocale: Locale? = null

    fun onDigitPressed(value: String) {
        if (_uiState.value.status != CallStatus.Idle) return
        _uiState.value = _uiState.value.copy(dialedNumber = (_uiState.value.dialedNumber + value).take(32))
    }

    fun onBackspace() {
        if (_uiState.value.status != CallStatus.Idle) return
        _uiState.value = _uiState.value.copy(dialedNumber = _uiState.value.dialedNumber.dropLast(1))
    }

    fun clearDialPad() {
        if (_uiState.value.status != CallStatus.Idle) return
        _uiState.value = _uiState.value.copy(dialedNumber = "")
    }

    fun startCall() {
        if (_uiState.value.dialedNumber.isBlank()) return
        _uiState.value = _uiState.value.copy(status = CallStatus.Dialing, elapsedSeconds = 0)
        callJob?.cancel()
        callJob = viewModelScope.launch {
            delay(1_500)
            val prompt = currentPrompts.random()
            _uiState.value = _uiState.value.copy(status = CallStatus.Connected(prompt))
            speak(prompt)
            var seconds = 0
            while (isActive) {
                delay(1_000)
                seconds += 1
                _uiState.value = _uiState.value.copy(elapsedSeconds = seconds)
            }
        }
    }

    fun toggleMute() {
        val current = _uiState.value
        if (current.status is CallStatus.Idle) return
        val newMuted = !current.isMuted
        _uiState.value = current.copy(isMuted = newMuted)
        if (newMuted) {
            tts.stop()
        } else if (current.status is CallStatus.Connected) {
            speak((current.status as CallStatus.Connected).message)
        }
    }

    fun toggleSpeaker() {
        val current = _uiState.value
        if (current.status is CallStatus.Idle) return
        _uiState.value = current.copy(isSpeakerOn = !current.isSpeakerOn)
    }

    fun endCall() {
        callJob?.cancel()
        callJob = null
        tts.stop()
        _uiState.value = PhoneUiState()
    }

    fun applySettings(settings: AppSettings) {
        currentPrompts = defaultPrompts[settings.ringtone] ?: defaultPrompts.values.first()
        val locale = runCatching { Locale.forLanguageTag(settings.ttsLocale) }.getOrDefault(Locale.GERMAN)
        if (ttsReady) {
            tts.language = locale
        } else {
            pendingLocale = locale
        }
    }

    private fun speak(message: String) {
        if (!_uiState.value.isMuted && ttsReady) {
            tts.stop()
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "sandbox-call")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            ttsReady = true
            pendingLocale?.let { locale ->
                tts.language = locale
            }
            pendingLocale = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        callJob?.cancel()
        tts.stop()
        tts.shutdown()
    }

    companion object {
        private val defaultPrompts = mapOf(
            "ringtone_classic" to listOf(
                "Guten Tag. Sie haben die Zentrale erreicht.",
                "Bitte warten Sie. Ihr Anruf ist uns wichtig.",
                "Niemand ist derzeit erreichbar. Versuchen Sie es später erneut.",
                "Dies ist eine automatische Testansage."
            ),
            "ringtone_beep" to listOf(
                "Piep, piep. Dies ist der Testmodus.",
                "Automatische Durchwahl aktiv. Bitte bleiben Sie dran.",
                "Wir verbinden Sie gleich mit der richtigen Testperson."
            ),
            "ringtone_sci_fi" to listOf(
                "Willkommen im Orbit der Sandbox.",
                "Kommunikationskanal hergestellt. Simulation läuft.",
                "Dies ist eine futuristische Testansage."
            )
        )
    }
}

class PhoneViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhoneViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhoneViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
