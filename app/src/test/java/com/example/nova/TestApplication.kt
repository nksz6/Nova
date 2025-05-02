package com.example.nova

import android.app.Application
import org.robolectric.shadows.ShadowLog

//setting up logging for robolectric
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //redirect to system.out
        ShadowLog.stream = System.out
    }
}