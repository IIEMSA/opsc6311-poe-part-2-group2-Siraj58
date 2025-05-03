package com.example.moneyv1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity

class StartUpScreenActivity : AppCompatActivity() {
    private lateinit var startupRoot: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        // Reference the root view or logo if you prefer
        startupRoot = findViewById(R.id.startup)

        // Optional fade-in animation on startup
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1000
            fillAfter = true
        }
        startupRoot.startAnimation(fadeIn)

        // Delay for 2 seconds, then fade out and switch activity
        Handler(Looper.getMainLooper()).postDelayed({
            val fadeOut = AlphaAnimation(1f, 0f).apply {
                duration = 500
                fillAfter = true
            }
            startupRoot.startAnimation(fadeOut)

            // Navigate to the next screen after fade out
            Handler(Looper.getMainLooper()).postDelayed({
                val isLoggedIn = checkLoginStatus()
                if (isLoggedIn) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                finish()
            }, 500) // wait for fade-out animation to complete

        }, 2000) // Delay before transition
    }

    private fun checkLoginStatus(): Boolean {
        // Replace with Firebase or shared preferences logic later
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        return prefs.getBoolean("loggedIn", false)
    }
}