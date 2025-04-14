package com.example.myapplication.ui  // Define the package at the top

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.utils.PhoBertAPI

/**
 * MainActivity is the main screen of the application, allowing users to input text
 * and analyze sentiment using the PhoBERT API.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        val inputText = findViewById<EditText>(R.id.inputText)
        val analyzeButton = findViewById<Button>(R.id.btnAnalyze)
        val resultIcon = findViewById<ImageView>(R.id.resultIcon)

        // Set up click listener for the "Analyze" button
        analyzeButton.setOnClickListener {
            val userInput = inputText.text.toString().trim()
            if (userInput.isNotEmpty()) {
                analyzeSentiment(userInput, resultIcon)
            } else {
                resultIcon.visibility = View.GONE
            }
        }
    }

    /**
     * Sends user input to the PhoBERT API for sentiment analysis and updates the UI.
     *
     * @param input The text input by the user.
     * @param resultIcon ImageView to display the corresponding sentiment icon.
     */
    private fun analyzeSentiment(input: String, resultIcon: ImageView) {
        PhoBertAPI.generateResponse(this, input) { label, iconResId ->
            runOnUiThread {
                Log.d("MainActivity", "Sentiment: $label, Icon Resource ID: $iconResId")
                if (iconResId != null) {
                    resultIcon.setImageResource(iconResId)
                    resultIcon.visibility = View.VISIBLE
                } else {
                    Log.e("MainActivity", "Icon not updated because iconResId = null")
                    resultIcon.visibility = View.GONE
                }
            }
        }
    }
}
