package com.adyen.sampleapp

import com.adyen.ipp.authentication.AuthenticationProvider
import com.adyen.ipp.authentication.AuthenticationResponse
import com.adyen.ipp.authentication.AuthenticationService
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume

class MyAuthenticationService : AuthenticationService() {
    override val authenticationProvider: AuthenticationProvider
        get() = object : AuthenticationProvider {
            override suspend fun authenticate(setupToken: String): Result<AuthenticationResponse> {
                val client = OkHttpClient()

                val jsonObject = JSONObject()
                // Please put below your merchant account
                jsonObject.put("merchantAccount", "Your merchant account")
                jsonObject.put("setupToken", setupToken)

                val mediaType = "application/json".toMediaType()
                val body = jsonObject.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    // Please add your URL to receive session token
                    .url("Your URL to receive session tokens")
                    .addHeader(
                        "x-api-key",
                        // Please add api key
                        "Your api key to receive session tokens"
                    )
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
                                            json.optString(
                                                "sdkData"
                                            )
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
