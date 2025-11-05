package com.example.stocksandbox

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.stocksandbox.camera.CameraViewModel
import com.example.stocksandbox.camera.CacheCleaner
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class CameraViewModelTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    private lateinit var viewModel: CameraViewModel
    private lateinit var cacheCleaner: CacheCleaner

    @Before
    fun setup() {
        viewModel = CameraViewModel(context)
        cacheCleaner = CacheCleaner(context)
        cacheCleaner.clearCameraCache()
    }

    @After
    fun tearDown() {
        cacheCleaner.clearCameraCache()
    }

    @Test
    fun photoFilesAreStoredInCache() {
        val file = viewModel.createPhotoFile()
        file.writeText("test")
        viewModel.onPhotoCaptured(file)
        val files = viewModel.photos.value
        assertThat(files.isNotEmpty(), `is`(true))
        assertThat(files.first().parentFile, `is`(cacheCleaner.cacheDirectory()))
    }

    @Test
    fun cacheCleanerDeletesFiles() {
        val dir = cacheCleaner.cacheDirectory()
        val file = File(dir, "temp.txt")
        file.writeText("temp")
        cacheCleaner.clearCameraCache()
        assertThat(dir.listFiles()?.isEmpty() ?: true, `is`(true))
    }
}
