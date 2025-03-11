package com.adyen.sampleapp.dynamic

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.startup.AppInitializer
import com.adyen.ipp.api.InPersonPayments
import com.adyen.ipp.api.InPersonPaymentsInitializer
import com.adyen.ipp.api.payment.PaymentInterface
import com.adyen.ipp.api.payment.PaymentInterfaceType
import com.adyen.ipp.api.payment.TransactionRequest
import com.adyen.ipp.api.ui.MerchantUiParameters
import com.adyen.sampleapp.dynamic.databinding.ActivityPaymentBinding
import java.util.Base64
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

class PaymentActivity : AppCompatActivity() {
    private val tag = "PaymentActivity"

    private lateinit var binding: ActivityPaymentBinding

    private val resultLauncher = InPersonPayments.registerForPaymentResult(this) { result ->
        val resultText = result.fold(
            onSuccess = { paymentResult ->
                val decodedResult = String(Base64.getDecoder().decode(paymentResult.data))
                Log.d(tag, "Result: \n $decodedResult")
                if (paymentResult.success) "Payment Successful" else "Payment Failed"
            },
            onFailure = { error ->
                Log.d(tag, "Result failed with: ${error.message}")
                "Payment Failed"
            },
        )
        Toast.makeText(this, resultText, Toast.LENGTH_LONG).show()
        binding.startPaymentButton.isEnabled = true
    }

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

        setup()
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun setup() {
        // Initialize SDK
        AppInitializer.getInstance(this@PaymentActivity)
            .initializeComponent(InPersonPaymentsInitializer::class.java)

        binding.startPaymentButton.setOnClickListener {
            binding.startPaymentButton.isEnabled = false

            lifecycleScope.launch {
                // Suspend until SDK is initialized
                InPersonPayments.initialised.takeWhile { true }.first()

                val paymentInterfaceResult = InPersonPayments.getPaymentInterface(PaymentInterfaceType.createTapToPayType())
                paymentInterfaceResult.fold(
                    onSuccess = { paymentInterface ->
                        startPayment(paymentInterface)
                    },
                    onFailure = { error ->
                        Log.e(tag, "Failed to create PaymentInterface", error)
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun startPayment(paymentInterface: PaymentInterface<*>) {
        val nexoRequest: String = generateNexoRequest(
            requestedAmount = "5",
            currency = "USD",
            poiId = InPersonPayments.getInstallationId().getOrNull() ?: "UNKNOWN"
        )


        InPersonPayments.performTransaction(
            context = this,
            paymentInterface = paymentInterface,
            transactionRequest = TransactionRequest.create(nexoRequest).getOrThrow(),
            paymentLauncher = resultLauncher,
            merchantUiParameters = MerchantUiParameters.create()
        )
    }

    companion object {

        private val DATE_FORMAT =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

        private fun generateNexoRequest(
            serviceId: String = UUID.randomUUID().toString(),
            saleId: String = "AndroidSampleApp",
            transactionID: String = "SampleApp-AndroidTx",
            poiId: String,
            currency: String,
            requestedAmount: String,
        ): String {

            val timeStamp = DATE_FORMAT.format(Date())
            val maxServiceIdSize = 10

            return """
                |{
                |  "SaleToPOIRequest": {
                |    "MessageHeader": {
                |      "ProtocolVersion": "3.0",
                |      "MessageClass": "Service",
                |      "MessageCategory": "Payment",
                |      "MessageType": "Request",
                |      "ServiceID": "${serviceId.take(maxServiceIdSize)}",
                |      "SaleID": "$saleId",
                |      "POIID": "$poiId"
                |    },
                |    "PaymentRequest": {
                |      "SaleData": {
                |        "SaleTransactionID": {
                |          "TransactionID": "$transactionID",
                |          "TimeStamp": "$timeStamp"
                |        }
                |      },
                |      "PaymentTransaction": {
                |        "AmountsReq": {
                |          "Currency": "$currency",
                |          "RequestedAmount": $requestedAmount
                |        }
                |      }
                |    }
                |  }
                |}
            """.trimMargin("|")
        }
    }
}