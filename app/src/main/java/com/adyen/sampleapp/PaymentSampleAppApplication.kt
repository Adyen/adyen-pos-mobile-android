package com.adyen.sampleapp

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallHelper
import logcat.AndroidLogcatLogger
import logcat.LogcatLogger

class PaymentSampleAppApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // Emulates installation of future on demand modules using SplitCompat.
        SplitCompat.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        LogcatLogger.install(AndroidLogcatLogger())
    }
}