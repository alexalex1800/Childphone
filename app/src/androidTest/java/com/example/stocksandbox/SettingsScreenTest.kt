package com.example.stocksandbox

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.stocksandbox.data.PreferencesRepo
import com.example.stocksandbox.settings.SettingsScreen
import com.example.stocksandbox.settings.SettingsViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun settingsDisclaimerIsVisible() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val repo = PreferencesRepo(context)
        val viewModel = SettingsViewModel(repo)
        composeTestRule.setContent {
            SettingsScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Diese Einstellungen gelten nur in dieser App und ändern keine System-Einstellungen.")
            .assertIsDisplayed()
    }
}
