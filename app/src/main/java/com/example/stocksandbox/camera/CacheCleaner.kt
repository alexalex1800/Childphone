package com.example.stocksandbox.camera

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CacheCleaner(private val context: Context) {

    suspend fun clearCameraCache(): Boolean = withContext(Dispatchers.IO) {
        val dir = cameraCacheDir(context)
        if (!dir.exists()) return@withContext true
        dir.walkBottomUp().forEach { file ->
            if (file != dir) {
                file.delete()
            }
        }
        dir.delete()
        if (!dir.exists()) {
            dir.mkdirs()
        }
        dir.listFiles().isNullOrEmpty()
    }

    companion object {
        const val CAMERA_CACHE_FOLDER = "camera_photos"

        fun cameraCacheDir(context: Context): File =
            File(context.cacheDir, CAMERA_CACHE_FOLDER)
    }
}
