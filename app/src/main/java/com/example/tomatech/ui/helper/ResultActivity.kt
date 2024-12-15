package com.example.tomatech.ui.helper

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tomatech.R

class ResultActivity : AppCompatActivity() {
    private lateinit var resultImageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var confidenceTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_result)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.result_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayShowTitleEnabled(false)

        resultImageView = findViewById(R.id.result_image)
        resultTextView = findViewById(R.id.result_text)
        confidenceTextView = findViewById(R.id.confidence_score_text)

        val diagnosis = intent.getStringExtra("RESULT")?.trim() ?: "No result"
        val confidenceScore = intent.getFloatExtra("CONFIDENCE_SCORE", 0.0f)
        val imageUrl = intent.getStringExtra("IMAGE_URL")

        Log.d("ResultActivity", "Diagnosis received: $diagnosis")
        Log.d("ResultActivity", "Confidence Score: $confidenceScore")

        resultTextView.text = diagnosis


        val confidencePercentage = confidenceScore * 100
        confidenceTextView.text = getString(R.string.confidence_score_text, confidencePercentage)

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(resultImageView)
        } else {
            resultImageView.setImageResource(R.drawable.image_card)
        }
        findViewById<Button>(R.id.back_home_button).setOnClickListener {
            goBack()
        }
    }
    fun goBack() {
        onBackPressed()
    }
}