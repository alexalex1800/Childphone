package com.example.stocksandbox

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.stocksandbox.camera.CacheCleaner
import com.example.stocksandbox.data.PreferencesRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class StockSandboxApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val preferencesRepo: PreferencesRepo by lazy { PreferencesRepo(this) }
    val cacheCleaner: CacheCleaner by lazy { CacheCleaner(this) }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                applicationScope.launch {
                    cacheCleaner.clearCameraCache()
                }
            }
        })
    }
}
