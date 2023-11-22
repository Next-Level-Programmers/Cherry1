package com.nextlevelprogrammers.cherry1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isFirstLaunch = AppPreferences.isFirstLaunch(this)
        val isIntroCompleted = AppPreferences.isIntroCompleted(this)

        if (isFirstLaunch) {
            startActivity(Intent(this, MainActivity::class.java))
            AppPreferences.setFirstLaunch(this, false) // Marking first launch completed
        } else if (isIntroCompleted) {
            startActivity(Intent(this, WelcomePage::class.java))
        }
        finish()
    }
}
