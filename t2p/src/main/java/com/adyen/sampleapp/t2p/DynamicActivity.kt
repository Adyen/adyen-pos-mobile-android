package com.adyen.sampleapp.t2p

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.adyen.sampleapp.t2p.databinding.ActivityDynamicBinding
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class DynamicActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDynamicBinding

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        Log.e("DynamicActivity", "attachBaseContext")
        SplitCompat.installActivity(this)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("DynamicActivity", "onCreate")

        Log.e("DynamicActivity", "setContentView")
        binding = ActivityDynamicBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        Log.e("DynamicActivity", "setContentView")
//        setContentView(R.layout.activity_dynamic)
        initializeSDK()

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        binding.buttonConnectDevice.setOnClickListener {
//            DeviceManagementActivity.start(this)
//        }
    }

    private fun initializeSDK() {
        Log.e("DynamicActivity", "initializeSDK")
        loadNatives()
    }

    private fun loadNatives() {
        Log.e("TEST", "loadNatives")
        val context = this
        SplitInstallHelper.loadLibrary(context, "c++_shared")
        SplitInstallHelper.loadLibrary(context, "su")
        SplitInstallHelper.loadLibrary(context, "si")
        SplitInstallHelper.loadLibrary(context, "sa")
        SplitInstallHelper.loadLibrary(context, "pttp")
        SplitInstallHelper.loadLibrary(context, "pm")
        SplitInstallHelper.loadLibrary(context, "pcr")
        SplitInstallHelper.loadLibrary(context, "pc")
        SplitInstallHelper.loadLibrary(context, "maa")
        SplitInstallHelper.loadLibrary(context, "l")
        SplitInstallHelper.loadLibrary(context, "crypto_wrapper")
        SplitInstallHelper.loadLibrary(context, "km_l2")
        SplitInstallHelper.loadLibrary(context, "c")
        SplitInstallHelper.loadLibrary(context, "agnos")
        SplitInstallHelper.loadLibrary(context, "ZDefend")
        Log.e("TEST", "nativesLoaded")
    }
}