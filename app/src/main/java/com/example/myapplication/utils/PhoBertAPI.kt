package com.example.myapplication.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.myapplication.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object PhoBertAPI {
    // Create an OkHttpClient instance for making HTTP requests
    private val client = OkHttpClient()
    // Define a constant for logging tag
    private const val TAG = "PhoBertAPI"
    // Define the server URL where the PhoBERT API is hosted
    private const val SERVER_URL = "http://192.168.51.40:5001/analyze"
    /**
     * This function sends the input text to the PhoBERT API for sentiment analysis.
     * The API responds with a sentiment label (positive, negative, neutral),
     * and an icon resource ID representing the sentiment.
     *
     * @param context The context of the application
     * @param input The text input for sentiment analysis
     * @param callback A callback function to return the sentiment label and icon resource ID
     */
    fun generateResponse(context: Context, input: String, callback: (String?, Int?) -> Unit) {
        try {
            val json = JSONObject().apply {
                put("text", input)
            }

            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = json.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(SERVER_URL)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "API call failed: ${e.message}")
                    Handler(Looper.getMainLooper()).post {
                        callback(null, null)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        Log.e(TAG, "API response failed with code ${response.code}")
                        Handler(Looper.getMainLooper()).post {
                            callback(null, null)
                        }
                        return
                    }

                    response.body?.string()?.let {
                        val jsonResponse = JSONObject(it)
                        val label = jsonResponse.getString("label")
                        val iconResId = when (label) {
                            "positive" -> R.drawable.ic_happy
                            "negative" -> R.drawable.ic_sad
                            "neutral" -> R.drawable.ic_neutral
                            else -> null
                        }

                        Handler(Looper.getMainLooper()).post {
                            callback(label, iconResId)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error: ${e.message}")
            callback(null, null)
        }
    }
}
