package com.adyen.sampletestuploadapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.adyen.sampletestuploadapp.databinding.ActivityMainBinding
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var splitInstallManager: SplitInstallManager
    private val listener = SplitInstallStateUpdatedListener { state ->
        Log.e("MainActivity", "onInstalled: ${state.sessionId()} - ${state.status()} - ${state.moduleNames()}")
        if (state.status() == SplitInstallSessionStatus.INSTALLED) {
            startSdk()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadModule()
    }

    private fun loadModule() {
        Log.d("MainActivity", "loadModule")
        splitInstallManager = SplitInstallManagerFactory.create(this)
        splitInstallManager.registerListener(listener)

        val request = SplitInstallRequest.newBuilder()
                .addModule("t2p")
                .build()

        splitInstallManager
            .startInstall(request)
            .addOnSuccessListener { sessionId ->
                Log.d("MainActivity", "onSuccess: $sessionId")
                val installedModules: Set<String> = splitInstallManager.installedModules
                Log.e("MainActivity", "Installed Modules: $installedModules")
                if (installedModules.contains("t2p")) {
//                    startSdk()
                } else {
                    Log.e("MainActivity", "T2P NOT INSTALLED: $sessionId")
                }
                Log.d("MainActivity", "addOnSuccessListener finished")
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Install failed with: ${exception.message}", exception)
            }

    }

    private fun startSdk() {
        Log.d("MainActivity", "startSdk")
        unregister()
        startActivity(
            Intent().setClassName(
                "com.adyen.sampletestuploadapp",
                "com.adyen.sampletestuploadapp.t2p.DynamicActivity"
            )
        )
        finish()
        Log.d("MainActivity", "startActivity called")
    }

    private fun unregister() {
        val splitInstallManager = SplitInstallManagerFactory.create(this)
        splitInstallManager.unregisterListener(listener)
    }
}