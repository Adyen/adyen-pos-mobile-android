package com.adyen.sampleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.adyen.sampleapp.databinding.ActivityMainBinding
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadModule()
    }

    private fun loadModule() {
        Log.d("TEST", "loadModule")
        // Creates an instance of SplitInstallManager.
        val splitInstallManager = SplitInstallManagerFactory.create(this)

        // Creates a request to install a module.
        val request =
            SplitInstallRequest.newBuilder()
                // You can download multiple on demand modules per
                // request by invoking the following method for each
                // module you want to install.
                .addModule("t2p")
                .build()

        splitInstallManager
            // Submits the request to install the module through the
            // asynchronous startInstall() task. Your app needs to be
            // in the foreground to submit the request.
            .startInstall(request)
            // You should also be able to gracefully handle
            // request state changes and errors. To learn more, go to
            // the section about how to Monitor the request state.
            .addOnSuccessListener { sessionId ->
                Log.d("TEST", "onSuccess: $sessionId")
                startSdk()
            }
            .addOnFailureListener { exception ->
                Log.e("TEST", "Install failed with: ${exception.message}", exception)
            }

    }

    private fun startSdk() {
        Log.d("TEST", "startSdk")
        startActivity(
            Intent().setClassName(
                "com.adyen.sampleapp",
                "com.adyen.sampleapp.t2p.DynamicActivity"
            )
        )


    }
}