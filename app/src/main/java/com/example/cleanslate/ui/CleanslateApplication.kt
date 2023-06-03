package com.example.cleanslate.ui

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class CleanslateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("5c2bbc63-476b-4740-b2b6-c6909de7f074")
    }
}