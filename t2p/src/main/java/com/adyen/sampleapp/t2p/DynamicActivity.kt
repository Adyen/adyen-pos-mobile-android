package com.adyen.sampleapp.t2p

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.startup.AppInitializer
import com.adyen.ipp.InPersonPaymentsInitializer
import com.adyen.sampleapp.t2p.databinding.ActivityDynamicBinding
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class DynamicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDynamicBinding

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        Log.e("DynamicActivity", "attachBaseContext")
        SplitCompat.installActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("DynamicActivity", "onCreate")

        binding = ActivityDynamicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeSDK()
    }

    private fun initializeSDK() {
        Log.e("DynamicActivity", "initializeSDK")
        AppInitializer.getInstance(this)
            .initializeComponent(InPersonPaymentsInitializer::class.java)
    }
}