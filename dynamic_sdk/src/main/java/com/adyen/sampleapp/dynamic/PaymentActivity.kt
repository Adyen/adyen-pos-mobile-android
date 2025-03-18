package com.adyen.sampleapp.dynamic

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import com.adyen.sampleapp.dynamic.databinding.ActivityPaymentBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


class TestViewModel: ViewModel() {

    fun test() {
        Log.e("TEST", "test called")
    }

}

class PaymentActivity : AppCompatActivity() {
    private val tag = "PaymentActivity"

    private lateinit var binding: ActivityPaymentBinding

    private val testViewModel: TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        testViewModel.test()

        setup()
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun setup() {
        // Initialize SDK
//        AppInitializer.getInstance(this@PaymentActivity)
//            .initializeComponent(InPersonPaymentsInitializer::class.java)

        binding.startPaymentButton.setOnClickListener {
            binding.startPaymentButton.isEnabled = false

        }
    }

}