package com.example.stocksandbox.camera

import android.content.Context
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import java.io.File

class CacheCleaner(private val context: Context) {

    private val photoCacheDir: File by lazy {
        File(context.cacheDir, PHOTO_CACHE_DIR).apply { mkdirs() }
    }

    fun cacheDirectory(): File = photoCacheDir

    fun clearCameraCache() {
        val success = runCatching {
            photoCacheDir.deleteRecursively()
            photoCacheDir.mkdirs()
        }.isSuccess
        if (!success) {
            Toast.makeText(context, "Cache konnte nicht gelöscht werden", Toast.LENGTH_SHORT).show()
        }
    }

    @VisibleForTesting
    fun listPhotos(): List<File> = photoCacheDir.listFiles()?.toList().orEmpty()

    companion object {
        private const val PHOTO_CACHE_DIR = "photos"
    }
}
