package com.adyen.sampleapp

import android.app.Application
import com.adyen.ipp.InPersonPayments
import logcat.AndroidLogcatLogger
import logcat.LogcatLogger

class PaymentSampleAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LogcatLogger.install(AndroidLogcatLogger())
    }
}