package com.adyen.sampleapp

import androidx.test.espresso.idling.CountingIdlingResource

/**
 * Used to ensure the SmokeTest waits for the dyanmic module to be downloaded
 * before final verifications.
 */
object DynamicModuleCountingResource  {
    private const val DYNAMIC_MODULE = "dynamic-module"
    val resource = CountingIdlingResource(DYNAMIC_MODULE)
}