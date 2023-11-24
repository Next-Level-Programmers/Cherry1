package com.nextlevelprogrammers.cherry1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView

class PhoneEnter : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_phone_enter)

        val otpButton = findViewById<Button>(R.id.otpButton)
        otpButton.setOnClickListener {
            val intent = Intent(this, EnterOtp::class.java)
            startActivity(intent)
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right)
        }

        // Get the CardView reference
        val cardView = findViewById<CardView>(R.id.cardView)

        // Check the current theme mode
        val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        // Set the appropriate background resource based on the theme mode
        val backgroundRes = if (isDarkMode) R.drawable.darkback else R.drawable.lightback
        cardView.setBackgroundResource(backgroundRes)
    }
}