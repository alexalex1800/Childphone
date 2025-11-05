package com.example.stocksandbox

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.stocksandbox.camera.CacheCleaner
import com.example.stocksandbox.camera.CameraViewModel
import com.example.stocksandbox.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StockSandboxInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun dialerShowsSimulationDisclaimer() {
        composeRule.onNodeWithText("OK").performClick()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.dial_simulation_disclaimer)).assertIsDisplayed()
    }

    @Test
    fun settingsToggleDoesNotLeaveAppScope() {
        composeRule.onNodeWithText("OK").performClick()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.settings_tab)).performClick()
        val toggle = composeRule.onAllNodes(isToggleable()).onFirst()
        toggle.performClick()
        toggle.assertIsOn()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.theme)).assertIsDisplayed()
    }

    @Test
    fun cameraFilesStayInCache() = runBlocking {
        val app = composeRule.activity.application as StockSandboxApp
        val viewModel = CameraViewModel(app)
        val file = viewModel.createPhotoFile()
        file.writeText("test")
        viewModel.onPhotoCaptured(file)
        viewModel.refreshPhotos()
        val items = viewModel.photos.first()
        assertTrue(items.any { it.name == file.name })
        CacheCleaner(app).clearCameraCache()
        viewModel.refreshPhotos()
        val cleared = viewModel.photos.first()
        assertTrue(cleared.isEmpty())
    }

    @Test
    fun cacheCleanerDeletesOnRequest() = runBlocking {
        val app = composeRule.activity.application as StockSandboxApp
        val cacheCleaner = CacheCleaner(app)
        val dir = CacheCleaner.cameraCacheDir(app)
        if (!dir.exists()) dir.mkdirs()
        val file = app.cacheDir.resolve("camera_photos/test.txt")
        file.parentFile?.mkdirs()
        file.writeText("test")
        val result = cacheCleaner.clearCameraCache()
        assertTrue(result)
        assertTrue(CacheCleaner.cameraCacheDir(app).listFiles().isNullOrEmpty())
    }
}
