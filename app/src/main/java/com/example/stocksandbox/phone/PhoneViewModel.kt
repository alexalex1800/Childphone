package com.example.stocksandbox.phone

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stocksandbox.data.PreferencesRepo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class PhoneViewModel(
    private val appContext: Context,
    private val preferencesRepo: PreferencesRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneUiState())
    val uiState: StateFlow<PhoneUiState> = _uiState.asStateFlow()

    private var callTimerJob: Job? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var targetLocale: Locale = Locale.GERMAN
    private var ringtoneChoice: String = "classic"

    private val ansagen = mapOf(
        "classic" to listOf(
            "Guten Tag. Sie haben die Zentrale erreicht.",
            "Bitte warten Sie. Ihr Anruf ist uns wichtig.",
            "Niemand ist derzeit erreichbar. Versuchen Sie es später erneut.",
            "Dies ist eine automatische Testansage."
        ),
        "modern" to listOf(
            "Willkommen im Sandbox-Callcenter.",
            "Ihre Anfrage wird gleich bearbeitet.",
            "Wir schätzen Ihren simulierten Anruf.",
            "Dies ist lediglich eine Demo."),
        "retro" to listOf(
            "Sie sprechen mit der Vermittlung.",
            "Die Leitung ist frei. Bitte bleiben Sie dran.",
            "Kein Anschluss unter dieser Nummer – zumindest nicht wirklich.",
            "Vielen Dank fürs Testen!"
        )
    )

    init {
        initialiseTextToSpeech()
        observePreferences()
    }

    fun onDialPadInput(symbol: String) {
        if (_uiState.value.dialedNumber.length < 20) {
            _uiState.update { it.copy(dialedNumber = it.dialedNumber + symbol) }
        }
    }

    fun onBackspace() {
        if (_uiState.value.dialedNumber.isNotEmpty()) {
            _uiState.update { it.copy(dialedNumber = it.dialedNumber.dropLast(1)) }
        }
    }

    fun onCallPressed() {
        val number = _uiState.value.dialedNumber
        if (number.isBlank()) return
        if (_uiState.value.isInCall) return
        _uiState.update {
            it.copy(
                isInCall = true,
                isConnected = false,
                elapsedSeconds = 0,
                statusText = "Verbindung wird hergestellt…",
                showOnboarding = false
            )
        }
        viewModelScope.launch {
            delay(1200)
            _uiState.update { it.copy(isConnected = true, statusText = "Verbindung hergestellt") }
            startCallTimer()
            speakRandomAnnouncement()
        }
    }

    fun onEndCall() {
        callTimerJob?.cancel()
        callTimerJob = null
        tts?.stop()
        _uiState.update {
            it.copy(
                isInCall = false,
                isConnected = false,
                elapsedSeconds = 0,
                statusText = "Bereit",
                isMuted = false,
                isSpeaker = false
            )
        }
    }

    fun toggleMute() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        if (_uiState.value.isMuted) {
            tts?.stop()
        } else if (_uiState.value.isConnected) {
            speakRandomAnnouncement()
        }
    }

    fun toggleSpeaker() {
        _uiState.update { it.copy(isSpeaker = !it.isSpeaker) }
    }

    fun clearNumber() {
        _uiState.update { it.copy(dialedNumber = "") }
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { state -> state.copy(elapsedSeconds = state.elapsedSeconds + 1) }
            }
        }
    }

    private fun initialiseTextToSpeech() {
        tts = TextToSpeech(appContext) { status ->
            isTtsReady = status == TextToSpeech.SUCCESS
            if (isTtsReady) {
                updateTtsLanguage(targetLocale)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {}
                    override fun onError(utteranceId: String?) {}
                })
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepo.ttsLocale.collect { localeCode ->
                val locale = when (localeCode) {
                    "en" -> Locale.UK
                    else -> Locale.GERMAN
                }
                targetLocale = locale
                updateTtsLanguage(locale)
            }
        }
        viewModelScope.launch {
            preferencesRepo.ringtoneChoice.collect { choice ->
                ringtoneChoice = choice
            }
        }
    }

    private fun updateTtsLanguage(locale: Locale) {
        if (isTtsReady) {
            tts?.language = locale
        }
    }

    private fun speakRandomAnnouncement() {
        if (!isTtsReady || _uiState.value.isMuted) return
        val messages = ansagen[ringtoneChoice] ?: ansagen.getValue("classic")
        val message = messages.random()
        val bundle = Bundle().apply { putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, if (_uiState.value.isMuted) 0f else 1f) }
        tts?.speak(message, TextToSpeech.QUEUE_FLUSH, bundle, "sandbox-call")
    }

    override fun onCleared() {
        super.onCleared()
        callTimerJob?.cancel()
        tts?.stop()
        tts?.shutdown()
    }

    data class PhoneUiState(
        val dialedNumber: String = "",
        val isInCall: Boolean = false,
        val isConnected: Boolean = false,
        val elapsedSeconds: Int = 0,
        val isMuted: Boolean = false,
        val isSpeaker: Boolean = false,
        val statusText: String = "Bereit",
        val showOnboarding: Boolean = true
    )

    companion object {
        fun provideFactory(
            context: Context,
            repo: PreferencesRepo
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PhoneViewModel(context.applicationContext, repo) as T
            }
        }
    }
}
