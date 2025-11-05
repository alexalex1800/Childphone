package com.example.stocksandbox.camera

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraViewModel(private val context: Context) : ViewModel() {

    private val cacheCleaner = CacheCleaner(context)
    private val _photos = MutableStateFlow<List<File>>(emptyList())
    val photos: StateFlow<List<File>> = _photos.asStateFlow()

    init {
        refreshPhotos()
    }

    fun createPhotoFile(): File {
        val dir = cacheCleaner.cacheDirectory()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(dir, "IMG_$timeStamp.jpg")
    }

    fun onPhotoCaptured(file: File) {
        refreshPhotos()
    }

    fun deleteAllPhotos() {
        viewModelScope.launch {
            cacheCleaner.clearCameraCache()
            refreshPhotos()
        }
    }

    private fun refreshPhotos() {
        viewModelScope.launch {
            val files = cacheCleaner.listPhotos().sortedByDescending { it.lastModified() }
            _photos.update { files }
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CameraViewModel(context.applicationContext) as T
            }
        }
    }
}
