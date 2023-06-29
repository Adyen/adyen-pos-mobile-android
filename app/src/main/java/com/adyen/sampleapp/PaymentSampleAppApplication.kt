package com.adyen.sampleapp

import android.app.Application
import com.adyen.ipp.InPersonPayments

class PaymentSampleAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        InPersonPayments.initialize(application = this)
    }
}