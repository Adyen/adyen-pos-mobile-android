package com.adyen.sampleapp.t2p

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.startup.AppInitializer
import com.adyen.ipp.InPersonPaymentsInitializer
import com.adyen.ipp.cardreader.bluetooth.ui.DeviceManagementActivity
import com.adyen.sampleapp.t2p.databinding.ActivityDynamicBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class DynamicActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDynamicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDynamicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeSDK()

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.buttonConnectDevice.setOnClickListener {
            DeviceManagementActivity.start(this)
        }
    }

    private fun initializeSDK() {
        val ipp = AppInitializer.getInstance(this)
            .initializeComponent(InPersonPaymentsInitializer::class.java)
    }
}