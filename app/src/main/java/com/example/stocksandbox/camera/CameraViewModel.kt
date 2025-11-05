package com.example.stocksandbox.camera

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val _photos = MutableStateFlow<List<File>>(emptyList())
    val photos: StateFlow<List<File>> = _photos.asStateFlow()

    init {
        refreshPhotos()
    }

    fun createPhotoFile(): File {
        val dir = CacheCleaner.cameraCacheDir(getApplication())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, "IMG_${System.currentTimeMillis()}.jpg")
    }

    fun onPhotoCaptured(file: File) {
        refreshPhotos()
    }

    fun refreshPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            val dir = CacheCleaner.cameraCacheDir(getApplication())
            val files = dir.listFiles()?.sortedByDescending { it.lastModified() } ?: emptyList()
            _photos.value = files
        }
    }

    fun deleteAllPhotos(onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = CacheCleaner(getApplication()).clearCameraCache()
            refreshPhotos()
            withContext(Dispatchers.Main) {
                onResult(success)
            }
        }
    }
}

class CameraViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
