package com.adyen.sampleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.adyen.sampleapp.databinding.ActivityMainBinding
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    private val sdkModuleName = "ippmobile"
    private lateinit var splitInstallManager: SplitInstallManager
    private val installationListener = SplitInstallStateUpdatedListener { state ->
        Log.e(tag, "onInstalled: ${state.sessionId()} - ${state.status()} - ${state.moduleNames()}")
        if (state.status() == SplitInstallSessionStatus.INSTALLED) {
            Log.e(tag, "Module INSTALLED")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        splitInstallManager.unregisterListener(installationListener)
    }

    private fun setup() {
        splitInstallManager =  SplitInstallManagerFactory.create(this)

        if (splitInstallManager.installedModules.contains(sdkModuleName)) {
            Log.d(tag, "SDK module is already installed. Skipping.")
            startPaymentScreen()
        }

        binding.loadModuleButton.setOnClickListener {
            binding.loadModuleButton.isEnabled = false
            binding.loadingSpinner.visibility = VISIBLE
            loadSdkModule()
        }
    }

    private fun loadSdkModule() {
        Log.d(tag, "loadSdkModule")
        splitInstallManager.registerListener(installationListener)

        val request = SplitInstallRequest.newBuilder()
            .addModule(sdkModuleName)
            .build()

        splitInstallManager
            .startInstall(request)
            .addOnSuccessListener { sessionId ->
                Log.d(tag, "onSuccess: $sessionId")
                val installedModules: Set<String> = splitInstallManager.installedModules
                Log.e(tag, "Installed Modules: $installedModules")
                if (installedModules.contains(sdkModuleName)) {
                    Log.e(tag, "Module installed, starting payment.")
                        startPaymentScreen()
                } else {
                    Log.e(tag, "SDK MODULE NOT INSTALLED: $sessionId")
                }
                Log.d(tag, "addOnSuccessListener finished")
            }
            .addOnFailureListener { exception ->
                Log.e(tag, "Install failed with: ${exception.message}", exception)
            }
    }

    private fun startPaymentScreen() {
        Log.d(tag, "startPaymentScreen")
//        startActivity(
//            Intent().setClassName(
//                "com.adyen.sampletestuploadapp",
//                "com.adyen.sampletestuploadapp.PaymentActivity"
//            )
//        )
        finish()
    }
}