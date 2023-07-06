package com.adyen.sampleapp

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.adyen.ipp.InPersonPayments
import com.adyen.ipp.cardreader.AdyenCardReaders
import com.adyen.ipp.cardreader.device.DeviceManager
import com.adyen.ipp.payment.PaymentInterfaceType
import com.adyen.ipp.payment.TransactionRequest
import com.adyen.sampleapp.databinding.FragmentPaymentBinding
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class PaymentSampleAppFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding
    private val resultLauncher = InPersonPayments.registerForPaymentResult(this) { paymentResult ->
        val resultText = if (paymentResult.success) "Payment Successful" else "Payment Failed"
        Toast.makeText(requireContext(), resultText, Toast.LENGTH_LONG).show()
    }

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)

        DeviceManager.INSTANCE
        AdyenCardReaders.deviceManager.activeDeviceInfo
        InPersonPayments


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonPay.setOnClickListener {
            uiScope.launch {
                startPayment()
            }
        }
    }

    private suspend fun startPayment() {
        val paymentInterface = InPersonPayments.getPaymentInterface(PaymentInterfaceType.CardReader())

        InPersonPayments.performTransaction(
            context = requireContext(),
            paymentInterface = paymentInterface.getOrThrow(),
            transactionRequest = TransactionRequest.create(
                generateNexoRequest(
                    requestedAmount = "5",
                    currency = "USD",
                    poiId = InPersonPayments.getInstallationId()
                )
            ).getOrThrow(),
            paymentLauncher = resultLauncher,
            authenticationServiceClass = MyAuthenticationService::class.java,
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

            return """
                |{
                |  "SaleToPOIRequest": {
                |    "MessageHeader": {
                |      "ProtocolVersion": "3.0",
                |      "MessageClass": "Service",
                |      "MessageCategory": "Payment",
                |      "MessageType": "Request",
                |      "ServiceID": "$serviceId",
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