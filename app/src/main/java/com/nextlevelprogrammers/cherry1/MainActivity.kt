package com.nextlevelprogrammers.cherry1

import AppPreferences
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val continueButton = findViewById<Button>(R.id.button2)
        continueButton.setOnClickListener {
            // Mark intro as completed
            AppPreferences.setIntroCompleted(this, true)
            // Launch WelcomePage
            startActivity(Intent(this, WelcomePage::class.java))
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right)
            finish()
        }

        val createAcc = findViewById<Button>(R.id.button1)
        createAcc.setOnClickListener {
            // Launch WelcomePage
            startActivity(Intent(this, PhoneEnter::class.java))
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right)
        }
    }
}