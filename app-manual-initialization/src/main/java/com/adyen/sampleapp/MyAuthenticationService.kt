package com.adyen.sampleapp

import com.adyen.ipp.api.authentication.AuthenticationProvider
import com.adyen.ipp.api.authentication.AuthenticationResponse
import com.adyen.ipp.api.authentication.MerchantAuthenticationService
import java.io.IOException
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import org.json.JSONObject

class MyAuthenticationService : MerchantAuthenticationService() {

    /**
     *  ------------
     * | IMPORTANT |
     *  ------------
     *
     * This part of the code sends the `setupToken` to authenticate you and your app with Adyen.
     *
     * In this example, for simplicity and ease of use, we are using okhttp to connect directly to Adyen.
     * This is NOT how your app should be implemented! Your credentials and API Key should be kept secret and safe
     * withing your servers, and should only be used for direct server to server communication with Adyen.
     *
     * In a production environment you should send the `setupToken` to you server and forward the authentication
     * request from there to the Adyen server, and then return the `sdkData` result here.
     *
     * More information on the Docs page.
     * https://docs.adyen.com/point-of-sale/ipp-mobile/card-reader-android/integration-reader#session
     */

    // See README for how to define these.
    val apiKey = BuildConfig.EnvironmentApiKey
    val merchantAccount = BuildConfig.EnvironmentMerchantAccount

    // This app is intended to be used only against the TEST environment.
    val apiUrl = "https://checkoutpos-test.adyen.com/checkoutpos/v3/auth/certificate/"

    // You can also declare this implementation somewhere else and pass it using your Dependency Injection system.
    override val authenticationProvider: AuthenticationProvider
        get() = object : AuthenticationProvider {
            override suspend fun authenticate(setupToken: String): Result<AuthenticationResponse> {
                val client = createOkHttpClient()

                val jsonObject = JSONObject().apply {
                    put("merchantAccount", merchantAccount)
                    put("setupToken", setupToken)
                }
                val mediaType = "application/json".toMediaType()
                val requestBody = jsonObject.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(apiUrl)
                    .addHeader("x-api-key", apiKey)
                    .post(requestBody)
                    .build()

                return suspendCancellableCoroutine { continuation ->
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            continuation.resume(Result.failure(Throwable(e)))
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (response.isSuccessful && response.body != null) {
                                val json = JSONObject(response.body!!.string())
                                continuation.resume(
                                    Result.success(
                                        AuthenticationResponse.create(
                                            json.optString("sdkData")
                                        )
                                    )
                                )
                            } else {
                                continuation.resume(Result.failure(Throwable("error")))
                            }
                        }
                    })
                }
            }

            private fun createOkHttpClient(): OkHttpClient {
                val logging = HttpLoggingInterceptor().apply {
                    setLevel(Level.BODY)
                }
                return OkHttpClient.Builder().apply {
                    addInterceptor(logging)
                }.build()
            }
        }
}
