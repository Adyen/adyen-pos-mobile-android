package com.adyen.sampleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Toast
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

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var sessionId: Int = 0
    private val sdkModuleName = "dynamic_sdk"
    private lateinit var splitInstallManager: SplitInstallManager

    private val installationListener = SplitInstallStateUpdatedListener { state ->
        if (state.sessionId() == sessionId) {
            when (state.status()) {
                SplitInstallSessionStatus.FAILED -> {
                    Log.e(TAG, "Module install failed with ${state.errorCode()}")
                }

                SplitInstallSessionStatus.INSTALLED -> {
                    DynamicModuleCountingResource.resource.decrement()
                    Toast.makeText(this, "Dynamic module installation complete.", Toast.LENGTH_SHORT).show()
                    openPaymentScreen()
                }

                SplitInstallSessionStatus.DOWNLOADING -> {
                    Log.i(TAG, "Downloading modules: ${state.moduleNames()}")
                }

                else -> Log.d(TAG, "Installation Status: ${state.status()}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        splitInstallManager.unregisterListener(installationListener)
    }

    private fun setup() {
        splitInstallManager = SplitInstallManagerFactory.create(this)

        if (splitInstallManager.installedModules.contains(sdkModuleName)) {
            Log.d(TAG, "$sdkModuleName module is already installed. Skipping.")
            openPaymentScreen()
        }

        binding.loadModuleButton.setOnClickListener {
            binding.loadModuleButton.isEnabled = false
            binding.loadingSpinner.visibility = VISIBLE
            Log.d(TAG, "Installing dynamic $sdkModuleName module.")
            loadSdkModule()
        }
    }

    private fun loadSdkModule() {
        splitInstallManager.registerListener(installationListener)

        val request = SplitInstallRequest
            .newBuilder()
            .addModule(sdkModuleName)
            .build()

        Toast.makeText(this, "Starting dynamic module installation.", Toast.LENGTH_SHORT).show()
        splitInstallManager
            .startInstall(request)
            .addOnSuccessListener {
                sessionId = it
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Install failed with: ${exception.message}", exception)
            }
        DynamicModuleCountingResource.resource.increment()
    }

    private fun openPaymentScreen() {
        Log.d(TAG, "startPaymentScreen")
        startActivity(
            Intent().setClassName(
                /* packageName = */ application.packageName,
                /* className = */ "com.adyen.sampleapp.dynamic.PaymentActivity"
            )
        )
        finish()
    }
}