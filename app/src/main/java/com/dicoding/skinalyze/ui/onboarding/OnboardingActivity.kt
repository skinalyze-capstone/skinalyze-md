package com.dicoding.skinalyze.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.skinalyze.R

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("OnboardingPrefs", MODE_PRIVATE)
        val isFirstTime = sharedPref.getBoolean("isFirstTime", true)

        if (!isFirstTime) {
            // Jika pengguna sudah pernah melihat onboarding, langsung ke SignIn
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        } else {
            // Logika untuk menampilkan onboarding flow
            setContentView(R.layout.activity_onboarding)
        }
    }
}