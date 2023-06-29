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
import com.adyen.ipp.payment.PaymentInterfaceType
import com.adyen.ipp.payment.TransactionRequest
import com.adyen.sampleapp.databinding.FragmentPaymentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class PaymentSampleAppFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val resultLauncher = InPersonPayments.registerForPaymentResult(this) {

        Toast.makeText(
            requireContext(),
            "PAYMENT RESULT RECEIVED -> RESULT -> success is -> ${it.success}",
            Toast.LENGTH_LONG
        ).show()
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonPay.setOnClickListener {
            uiScope.launch {
                tryToMakePayment()
            }
        }
    }

    private suspend fun tryToMakePayment() {
        val paymentInterface = InPersonPayments.getPaymentInterface(PaymentInterfaceType.TapToPay)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private val DATE_FORMAT =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

        private fun generateNexoRequest(
            serviceId: String = "1117162143",
            saleId: String = "mb-cagriu",
            transactionID: String = "29752",
            poiId: String,
            currency: String,
            requestedAmount: String,
        ): String {

            val timeStamp = DATE_FORMAT.format(Date())

            return "{\n" +
                    "  \"SaleToPOIRequest\": {\n" +
                    "    \"MessageHeader\": {\n" +
                    "      \"ProtocolVersion\": \"3.0\",\n" +
                    "      \"MessageClass\": \"Service\",\n" +
                    "      \"MessageCategory\": \"Payment\",\n" +
                    "      \"MessageType\": \"Request\",\n" +
                    "      \"ServiceID\": \"$serviceId\",\n" +
                    "      \"SaleID\": \"$saleId\",\n" +
                    "      \"POIID\": \"$poiId\"\n" +
                    "    },\n" +
                    "    \"PaymentRequest\": {\n" +
                    "      \"SaleData\": {\n" +
                    "        \"SaleTransactionID\": {\n" +
                    "          \"TransactionID\": \"$transactionID\",\n" +
                    "          \"TimeStamp\": \"$timeStamp\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"PaymentTransaction\": {\n" +
                    "        \"AmountsReq\": {\n" +
                    "          \"Currency\": \"$currency\",\n" +
                    "          \"RequestedAmount\": $requestedAmount\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"
        }
    }
}