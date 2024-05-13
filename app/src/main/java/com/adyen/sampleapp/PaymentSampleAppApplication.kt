package com.adyen.sampleapp

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import logcat.AndroidLogcatLogger
import logcat.LogcatLogger

class PaymentSampleAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LogcatLogger.install(AndroidLogcatLogger())
        checkService()
    }

    private fun checkService() {
        packageManager
            .getPackageInfo(packageName, PackageManager.GET_SERVICES)
            .services.forEach {
                Log.e("TEST", "Service: ${it.name}")
            }
    }
}