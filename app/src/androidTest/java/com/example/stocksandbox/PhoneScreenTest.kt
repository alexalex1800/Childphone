package com.example.stocksandbox

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.stocksandbox.phone.PhoneScreenContent
import com.example.stocksandbox.phone.PhoneViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhoneScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun dialerShowsSimulationDisclaimer() {
        val uiState = PhoneViewModel.PhoneUiState(dialedNumber = "123", statusText = "Bereit")
        composeTestRule.setContent {
            PhoneScreenContent(
                uiState = uiState,
                fakeWifiEnabled = true,
                onBackspace = {},
                onClearNumber = {},
                onSymbol = {},
                onCall = {},
                onEnd = {},
                onMute = {},
                onSpeaker = {}
            )
        }

        composeTestRule.onNodeWithText("Dies ist eine Simulation – es werden keine echten Anrufe getätigt.")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("WLAN: Verbunden (simuliert)").assertIsDisplayed()
    }
}
