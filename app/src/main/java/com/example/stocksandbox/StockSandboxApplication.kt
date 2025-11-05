package com.example.stocksandbox

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.stocksandbox.camera.CacheCleaner

class StockSandboxApplication : Application() {
    private val cacheCleaner by lazy { CacheCleaner(this) }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                cacheCleaner.clearCameraCache()
            }
        })
    }
}
