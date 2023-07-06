package com.adyen.sampleapp

import com.adyen.ipp.authentication.AuthenticationProvider
import com.adyen.ipp.authentication.AuthenticationResponse
import com.adyen.ipp.authentication.AuthenticationService
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

class MyAuthenticationService : AuthenticationService() {

    val apiKey = BuildConfig.EnvironmentApiKey
    val merchantAccount = BuildConfig.EnvironmentMerchantAccount
    val apiUrl = BuildConfig.EnvironmentUrl

    override val authenticationProvider: AuthenticationProvider
        get() = object : AuthenticationProvider {
            override suspend fun authenticate(setupToken: String): Result<AuthenticationResponse> {
                val logging = HttpLoggingInterceptor().apply {
                    setLevel(Level.BODY)
                }
                val client = OkHttpClient.Builder().apply {
                    addInterceptor(logging)
                }.build()

                val jsonObject = JSONObject()
                jsonObject.put("merchantAccount", merchantAccount)
                jsonObject.put("setupToken", setupToken)

                val mediaType = "application/json".toMediaType()
                val body = jsonObject.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(apiUrl)
                    .addHeader("x-api-key", apiKey)
                    .post(body)
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
                                        AuthenticationResponse(
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
        }
}
