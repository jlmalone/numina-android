package com.numina

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NuminaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
